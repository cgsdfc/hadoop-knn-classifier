#!/bin/bash -xe


class_name=com.example.KnnExperiment # 项目主类名，不需要修改。
jar_file=/home/cong/Code/demo/target/demo-1.0.jar # 项目构建产生的Jar包（请修改为实际的路径）。

# 先确保jar包是最新的。
mvn package -f "/home/cong/Code/demo/pom.xml"

hadoop jar $jar_file $class_name

echo $?
