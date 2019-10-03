model='model/seg'
input_file=$1
output_file=$2
#export CUDA_VISIBLE_DEVICES=''
/usr/local/anaconda3/bin/python BatchTest.py ${model} ${input_file} ${output_file}
