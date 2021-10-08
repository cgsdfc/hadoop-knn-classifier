#!/bin/bash -xe

# 运行本程序以预测车辆类型。

input_data=/demo/data/iris/iris_training.csv # 训练数据集，hdfs中的路径（请确保已经上传）。
output_dir=/demo/result/$(tempfile) # 输出目录，hdfs中的路径（请确保不存在）。
params_file=/demo/data/iris/KnnParams.txt # 配置文件，hdfs中的路径（请确保已经上传）。
jar_file=/home/cong/Code/demo/target/demo-1.0.jar # 项目构建产生的Jar包（请修改为实际的路径）。
class_name=com.example.KnnClassifier # 项目主类名，不需要修改。

# 先确保jar包是最新的。
mvn package -f "/home/cong/Code/demo/pom.xml"

hadoop jar $jar_file $class_name $input_data $output_dir $params_file

echo $?
