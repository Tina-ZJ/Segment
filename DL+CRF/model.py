from tflearn.data_utils import pad_sequences
import math
import helper
import numpy as np
import tensorflow as tf
from tensorflow.contrib.rnn.python.ops import rnn
from tensorflow.contrib.crf.python.ops import crf
from tensorflow.python.ops import rnn_cell_impl as rnn_cell
import sys
import model_base



class Model(object):
    def __init__(self, model_type, num_chars, num_classes, num_steps=120, num_epochs=3, batch_size=256,
                 emb_dim=300, hidden_dim=200, num_filter=100, num_layers=1, dropout_rate=0.5,
                 learning_rate=0.001, embedding_matrix=None, is_training=True):
        # Parameter
        self.model_type = model_type
        self.bLstm = False
        self.use_peepholes = True
        self.max_f1 = 0
        self.learning_rate = learning_rate
        self.dropout_rate = dropout_rate
        self.batch_size = batch_size
        self.num_layers = num_layers
        self.emb_dim = emb_dim
        self.hidden_dim = hidden_dim
        self.num_epochs = num_epochs
        self.num_steps = num_steps
        self.num_chars = num_chars
        self.num_classes = num_classes

        # placeholder of x, y
        self.inputs = tf.placeholder(tf.int32, [None, self.num_steps])
        self.targets = tf.placeholder(tf.int32, [None, self.num_steps])
        
        # get the length of each sample
        self.length = tf.reduce_sum(tf.sign(self.inputs), reduction_indices=1)
        self.length = tf.cast(self.length, tf.int32)

        # char embedding
        if embedding_matrix is not None:
            embedding = tf.Variable(embedding_matrix, trainable=True, name="emb", dtype=tf.float32)
        else:
            embedding = tf.get_variable("emb", [self.num_chars, self.emb_dim])
        inputs_emb = tf.nn.embedding_lookup(embedding, self.inputs)
        
        # for idcnn
        self.layers = [
            {
                'dilation': 1
            },
            {
                'dilation': 1
            },
            {
                'dilation': 2
            },
        ]
        self.filter_width = 3
        self.num_filter = num_filter
        self.repeat_times = 4
        self.cnn_output_width = 0

        if self.model_type=='bigru':
            outputs = self.bigru(is_training, inputs_emb) 
            # softmax
            self.outputs = tf.reshape(outputs, [-1, self.hidden_dim*2])

            softmax_w = tf.get_variable("softmax_w", [self.hidden_dim * 2, self.num_classes])
            softmax_b = tf.get_variable("softmax_b", [self.num_classes])
            self.logits = tf.reshape(tf.matmul(self.outputs, softmax_w) + softmax_b,
                                     [self.batch_size, self.num_steps, self.num_classes])
        elif self.model_type=='idcnn':
            outputs = self.idcnn(inputs_emb) 
            # softmax
            self.outputs = tf.reshape(outputs, [-1, self.cnn_output_width])

            softmax_w = tf.get_variable("softmax_w", [self.cnn_output_width, self.num_classes])
            softmax_b = tf.get_variable("softmax_b", [self.num_classes])
            self.logits = tf.reshape(tf.matmul(self.outputs, softmax_w) + softmax_b,
                                     [self.batch_size, self.num_steps, self.num_classes])
        else:
            raise KeyError 


        # crf
        self.transition_params = tf.get_variable("transitions", [self.num_classes, self.num_classes])
        log_likelihood, _ = crf.crf_log_likelihood(
            self.logits, self.targets, self.length, self.transition_params)
        # Add a training op to tune the parameters.
        self.loss = tf.reduce_mean(-log_likelihood)
        self.train_op = tf.train.AdamOptimizer(self.learning_rate).minimize(self.loss)
    
    def idcnn(self, inputs_emb):
        """
        :param idcnn_inputs: [batch_size, num_steps, emb_size] 
        :return: [batch_size, num_steps, cnn_output_width]
        """
        inputs_emb = tf.expand_dims(inputs_emb, 1)
        reuse = False
        if self.dropout_rate == 1.0:
            reuse = True
        with tf.variable_scope("idcnn"):
            shape=[1, self.filter_width, self.emb_dim,
                       self.num_filter]
            print(shape)
            filter_weights = tf.get_variable(
                "idcnn_filter",
                shape=[1, self.filter_width, self.emb_dim,
                       self.num_filter],
                initializer=tf.contrib.layers.xavier_initializer())
            
            """
            shape of input = [batch, in_height, in_width, in_channels]
            shape of filter = [filter_height, filter_width, in_channels, out_channels]
            """
            layerInput = tf.nn.conv2d(inputs_emb,
                                      filter_weights,
                                      strides=[1, 1, 1, 1],
                                      padding="SAME",
                                      name="init_layer")
            finalOutFromLayers = []
            totalWidthForLastDim = 0
            for j in range(self.repeat_times):
                for i in range(len(self.layers)):
                    dilation = self.layers[i]['dilation']
                    isLast = True if i == (len(self.layers) - 1) else False
                    with tf.variable_scope("atrous-conv-layer-%d" % i,
                                           reuse=True
                                           if (reuse or j>0) else False):
                        w = tf.get_variable(
                            "filterW",
                            shape=[1, self.filter_width, self.num_filter,
                                   self.num_filter],
                            initializer=tf.contrib.layers.xavier_initializer())
                        b = tf.get_variable("filterB", shape=[self.num_filter])
                        conv = tf.nn.atrous_conv2d(layerInput,
                                                   w,
                                                   rate=dilation,
                                                   padding="SAME")
                        conv = tf.nn.bias_add(conv, b)
                        conv = tf.nn.relu(conv)
                        if isLast:
                            finalOutFromLayers.append(conv)
                            totalWidthForLastDim += self.num_filter
                        layerInput = conv
            finalOut = tf.concat(axis=3, values=finalOutFromLayers)
            keepProb = 1.0 if reuse else 0.5
            finalOut = tf.nn.dropout(finalOut, keepProb)

            finalOut = tf.squeeze(finalOut, [1])
            finalOut = tf.reshape(finalOut, [-1, totalWidthForLastDim])
            self.cnn_output_width = totalWidthForLastDim
            return finalOut
     
    def bigru(self, is_training, inputs_emb): 
        # rnn cell
        if self.bLstm:
            rnn_cell_fw = rnn_cell.LSTMCell(self.hidden_dim, use_peepholes=self.use_peepholes)
            rnn_cell_bw = rnn_cell.LSTMCell(self.hidden_dim, use_peepholes=self.use_peepholes)
        else:
            rnn_cell_fw = rnn_cell.GRUCell(self.hidden_dim)
            rnn_cell_bw = rnn_cell.GRUCell(self.hidden_dim)
        # dropout
        if is_training:
            rnn_cell_fw = rnn_cell.DropoutWrapper(rnn_cell_fw, output_keep_prob=(1 - self.dropout_rate))
            rnn_cell_bw = rnn_cell.DropoutWrapper(rnn_cell_bw, output_keep_prob=(1 - self.dropout_rate))
        
        # stack rnn 
        # attention hidden_dim should equal emb_dim
        #rnn_cell_fw = rnn_cell.MultiRNNCell([rnn_cell_fw] * self.num_layers)
        #rnn_cell_bw = rnn_cell.MultiRNNCell([rnn_cell_bw] * self.num_layers)


        # forward and backward
        lstm_outputs, _, _ = rnn.stack_bidirectional_dynamic_rnn(
            [rnn_cell_fw]*self.num_layers,
            [rnn_cell_bw]*self.num_layers,
            inputs_emb,
            sequence_length=self.length,
            dtype=tf.float32
        )
        lstm_outputs = tf.reshape(lstm_outputs, [self.batch_size, self.num_steps, self.hidden_dim * 2])
        return lstm_outputs

    def train(self, sess, char2id, label2id, num_steps, batch_size, save_file, train_path, dev_path):

        log = open('log', 'a')
        # config tensorboard
        tf.summary.scalar("loss",self.loss)
        merged_summary = tf.summary.merge_all()
        writer = tf.summary.FileWriter('./tensorboard',sess.graph)
        cnt = 0
        for epoch in range(self.num_epochs):
            train_data = helper.getTrain_New(train_path, char2id, label2id, num_steps, batch_size)
            log.write("current epoch: %d\n" % (epoch))
            iteration =0
            for x_train_batch, y_train_batch in train_data:
                iteration+=1
                x_train_batch = pad_sequences(x_train_batch,self.num_steps, value=0)
                y_train_batch = pad_sequences(y_train_batch,self.num_steps, value=0)

                logits, transition_params, loss_train, sequence_lengths, _ = \
                    sess.run([
                        self.logits, self.transition_params, self.loss, self.length, self.train_op],
                        feed_dict={
                            self.inputs: x_train_batch,
                            self.targets: y_train_batch
                        })
                if iteration % 5 == 0:
                    cnt += 1
                    correct_labels, total_labels = self.evaluate(logits, transition_params, y_train_batch,
                                                                 sequence_lengths)
                    accuracy = 100.0 * correct_labels / float(total_labels)

                    log.write("iteration: %5d, train loss: %.3f, train precision: %.3f%%\n" % (
                        iteration, loss_train, accuracy))
                    log.flush()
                    # print loss_train
                if iteration > 0 and iteration % 1 == 0:
                    # data to tensorboard
                    feed_dict = {
                        self.inputs: x_train_batch,
                        self.targets: y_train_batch
                    }
                    s = sess.run(merged_summary,feed_dict=feed_dict)
                    writer.add_summary(s,iteration)
                    dev_data = helper.getTrain_New(dev_path, char2id, label2id, num_steps, batch_size)

                    self.verify(sess, dev_data, log, save_file, epoch, iteration)

        log.close()


    def verify(self, sess, val_data, log, save_file, epoch, iteration, sample=False):
        numCorrects = 0
        numTotal = 0
        for x_val_batch, y_val_batch in val_data:
            x_val_batch = pad_sequences(x_val_batch, maxlen=self.num_steps, value=0)
            y_val_batch = pad_sequences(y_val_batch, maxlen=self.num_steps, value=0)
            logits, transition_params, loss_train, sequence_lengths = \
                sess.run([
                    self.logits, self.transition_params, self.loss, self.length],
                    feed_dict={
                        self.inputs: x_val_batch,
                        self.targets: y_val_batch
                    })
            correct_labels, total_labels = self.evaluate(logits, transition_params, y_val_batch, sequence_lengths)
            numCorrects += correct_labels
            numTotal += total_labels
        if numTotal > 0:
            saver = tf.train.Saver()
            accuracy = 100.0 * numCorrects / float(numTotal)
            log.write("epoch: %5d, iteration: %5d, verify precision: %.3f%%\n" % (epoch, iteration, accuracy))
            log.flush()
            if accuracy >= self.max_f1:
                self.max_f1 = accuracy
                saver.save(sess, save_file)
                info = open(save_file + 'log', 'a+')
                info.write("epoch: %5d, iteration: %5d, verify precision: %.3f%%\n" % (
                    epoch, iteration, accuracy
                ))
                info.flush()
                info.close()

    def evaluate(self, tf_unary_scores, tf_transition_params, y, sequence_lengths):
        correct_labels = 0
        total_labels = 0
        for tf_unary_scores_, y_, sequence_length_ in zip(tf_unary_scores, y,
                                                          sequence_lengths):
            if sequence_length_ > 0:
                # Remove padding from the scores and tag sequence.
                tf_unary_scores_ = tf_unary_scores_[:sequence_length_]
                y_ = y_[:sequence_length_]

                # Compute the highest scoring sequence.
                viterbi_sequence, _ = crf.viterbi_decode(
                    tf_unary_scores_, tf_transition_params)

                # Evaluate word-level accuracy.
                correct_labels += np.sum(np.equal(viterbi_sequence, y_))
                total_labels += sequence_length_

        return correct_labels, total_labels

    def decode(self, sess, sentence, pretags=None):
        logits, transition_params, sequence_lengths = \
            sess.run([
                self.logits, self.transition_params, self.length],
                feed_dict={
                    self.inputs: sentence,
                })
        score = logits[0][:sequence_lengths[0]]
        if sequence_lengths[0] <= 0:
            return []
        if pretags != None:
            plen = min(len(pretags), self.num_steps)
            for i in range(plen):
                if pretags[i] >= 0:
                    score[i][pretags[i]] += 10.0
        viterbi_sequence, _ = crf.viterbi_decode(score, transition_params)
        return viterbi_sequence

    def predictBatch(self, sess, x):
        results = []
        logits, transition_params, sequence_lengths = \
            sess.run([
                self.logits, self.transition_params, self.length],
                feed_dict={
                    self.inputs: x,
                })
        for logit, _seq_len in zip(logits, sequence_lengths):
            if _seq_len > 0:
                score = logit[:_seq_len]
                viterbi_sequence, _ = crf.viterbi_decode(score, transition_params)
                results.append(viterbi_sequence)
            else:
                results.append([])
        return results
