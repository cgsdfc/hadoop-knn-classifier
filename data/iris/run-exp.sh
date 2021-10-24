#!/bin/bash -xe

# 运行本程序以预测车辆类型。

trainingFile=/demo/data/iris/iris_training.csv # 训练数据集，hdfs中的路径（请确保已经上传）。
configFile=/home/cong/Code/demo/data/iris/KnnParams.json # 配置文件，hdfs中的路径（请确保已经上传）。
jarFile=/home/cong/Code/demo/target/demo-1.0.jar # 项目构建产生的Jar包（请修改为实际的路径）。
mainClass=com.example.experiment.KnnExperiment # 项目主类名，不需要修改。
resampleMethod=boostrap
resampleParams="5 0.2"

# 先确保jar包是最新的。
mvn package -f "/home/cong/Code/demo/pom.xml"

hadoop jar $jarFile $mainClass $configFile $trainingFile $resampleMethod $resampleParams

echo $?
