#!/bin/bash

# 数据上传到hdfs
hadoop fs -mkdir /demo
hadoop fs -put /home/cong/Code/demo/KNN/CarOwners.csv /demo
hadoop fs -put /home/cong/Code/demo/KNN/KnnParams.csv /demo
