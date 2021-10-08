#!/bin/bash

# 数据上传到hdfs。请把路径修改为实际的路径。

# 新建项目输入数据的根目录。
hadoop fs -mkdir /demo

# 上传训练数据集。
hadoop fs -put /home/cong/Code/demo/KNN/CarOwners.csv /demo

# 上传配置文件，包括 K 值的设置和测试实例。
hadoop fs -put /home/cong/Code/demo/KNN/KnnParams.csv /demo
