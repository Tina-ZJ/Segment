model='model/seg'
input_file=$1
output_file=$2
python BatchTest.py ${model} ${input_file} ${output_file}
