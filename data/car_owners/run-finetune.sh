#!/bin/bash -xe

configFile=/home/cong/Code/demo/data/car_owners/finetune-config.json # 配置文件，hdfs中的路径（请确保已经上传）。
jarFile=/home/cong/Code/demo/target/demo-1.0.jar # 项目构建产生的Jar包（请修改为实际的路径）。
mainClass=com.example.experiment.KnnFineTune # 项目主类名，不需要修改。
outputFile=/home/cong/Code/demo/data/car_owners/result/finetune-result-4.json

# 先确保jar包是最新的。
mvn package -f "/home/cong/Code/demo/pom.xml"

hadoop jar $jarFile $mainClass $configFile $outputFile

echo $?
