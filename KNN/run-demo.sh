#!/bin/bash

# 运行本程序

input_data=/demo/CarOwners.csv
output_dir=/demo/res
params_file=/demo/KnnParams.txt
jar_file=/home/cong/Code/demo/target/demo-1.0.jar
class_name=com.example.KnnPattern

hadoop jar $jar_file $class_name $input_data $output_dir $params_file
