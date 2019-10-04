# Segment
BiGRU+CRF and IDCNN+CRF for Word Segment


######################### DL+CRF ###############################

Data format

1. word1 word2 word3 ...
2. more details please see DL+CRF/data/train.txt samples

Data preprocess
1. cd DL+CRF/preprocess
3. bash data.sh ../data/train.txt ../data/train.out
4. bash data.sh ../data/dev.txt ../data/dev.out

Train model
1. bash train.sh data/train.out data/dev.out idcnn (or bigru)

Predict
1. bash predict.sh test.txt test.out 

##################### Entropy #################################

Put your entropy dict in Entropy/model directory

Data format 
1. word \t left entropy \t right entropy
2. more details please see model/entropy.dict samples

Main function
1. /src/main/java/com/jz/tcp/Main_Seg.jave
2. /src/main/java/com/jz/tcp/Main_Tag.jave

