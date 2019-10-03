#!/bin/bash

input_file_train=$1
input_file_dev=$2
model_type=$3
name='seg'
model_path='./model'
model_name='seg'

python train.py ${name} ${input_file_train} ${input_file_dev} ${model_path}/${model_name} ${model_type}
