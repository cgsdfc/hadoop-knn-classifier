#!/bin/bash

input_data=/demo/CarOwners.csv
output_dir=/demo/res
params_file=/demo/KnnParams.txt
jar_file=/home/cong/Code/demo/target/demo-1.0.jar

hadoop jar $jar_file KnnPattern $input_data $output_dir $params_file
