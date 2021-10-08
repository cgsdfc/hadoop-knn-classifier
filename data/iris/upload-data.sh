#!/bin/bash

# 数据上传到hdfs。请把路径修改为实际的路径。

# 新建项目输入数据的根目录。
hadoop fs -mkdir -p /demo/data/iris

# 上传训练数据集。
hadoop fs -put /home/cong/Code/demo/data/iris/* /demo/data/iris
