#!/bin/bash -xe

# 运行本程序以预测车辆类型。

trainingFile=/demo/data/car_owners/CarOwners_train.csv # 训练数据集，hdfs中的路径（请确保已经上传）。
configFile=/home/cong/Code/demo/data/car_owners/KnnParams.json # 配置文件，hdfs中的路径（请确保已经上传）。
jarFile=/home/cong/Code/demo/target/demo-1.0.jar # 项目构建产生的Jar包（请修改为实际的路径）。
mainClass=com.example.KnnExperiment # 项目主类名，不需要修改。
numFolds=5

# 先确保jar包是最新的。
mvn package -f "/home/cong/Code/demo/pom.xml"

hadoop jar $jarFile $mainClass $configFile $numFolds $trainingFile

echo $?
