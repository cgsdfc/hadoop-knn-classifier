#!/bin/bash

# 数据上传到hdfs。请把路径修改为实际的路径。

# 新建项目输入数据的根目录。
hadoop fs -mkdir -p /demo/data/car_owners

# 上传训练数据集。
hadoop fs -put -f /home/cong/Code/demo/data/car_owners/* /demo/data/car_owners/
