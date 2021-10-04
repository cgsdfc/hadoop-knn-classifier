# 基于 Hadoop MapReduce 的 K 近邻分类器

## 项目简介

本项目基于虚拟机上搭建的 Hadoop 集群，用 Java 实现了 MapReduce 框架下的K近邻分类器算法，
并在一个小规模数据集上验证了算法实现的正确性。

## K 近邻分类器

K 近邻分类器（K-Nearest-Neighbors Classifier），简称 KNN 分类器，是一种无参数模型，
它的基本思想是：给定训练数据集 Train 和一个测试实例 S，计算 S 和 Train 中所有实例的距离，
取距离最近的 K 个训练集实例，则预测S的标签为这些实例中占比最大的标签。

KNN 分类器的数学表达如下：
$$
Pr(Y=j|X=x_0)=\frac{1}{K} \sum_{i \in N_0} I(y_i = j)
$$
这个式子表明：给定 x0，Y=j 的条件概率等于在K邻域内所有标签为 j 的训练实例占K邻域的大小 K 的比例。
这是对贝叶斯最优分类器的条件概率公式的一个近似。

## 基于 MapReduce 的 KNN 分类器

由于KNN分类器需要计算测试实例和所有训练实例的距离，所以时间复杂度比较高，算法对大数据的扩展性不好。
在 Hadoop 分布式文件系统 HDFS 和 并行计算框架 MapReduce 的帮助下，我们可以对 KNN 分类算法进行加速。
基于 MapReduce 的 KNN 分类器的基本思想，就是把训练数据分布到各个服务器，同时计算训练实例和测试实例的距离，
由于不同训练实例与测试实例的距离的计算是彼此无关，正好符合MapReduce框架的特性，因此能获得很好的加速效果，
也易于编程实现。


## 数据集

本项目使用的数据集是 CarOwners，一个包含了车主各种信息的数据集，特征包括：

- 年龄（Age）
- 收入（Income）
- 婚姻状况（Status）
- 性别（Gender）
- 孩子的个数（Children）
- 车主拥有的车的型号（Model）

该数据集有2500个实例。数据集示例如下：
```csv
Age,Income,Status,Gender,Children,Model
60,17247,Married,Female,0,Corsa
41,41963,Divorced,Female,1,Corsa
44,31915,Single,Male,0,MX5
72,37091,Single,Female,2,MX5
21,29802,Single,Male,4,Zafira
44,18533,Widowed,Male,1,Zafira
```

本项目基于单台机器上的虚拟机搭建了Hadoop集群，由于计算资源有限，只能选取不太大的数据集进行系统测试。
所选取的 CarOwners 数据集正好在计算资源的限制内，所以作为我们演示效果的数据集。

在本次实验中，我们利用除了车辆型号（Model）以外的所有特征来预测车辆型号。由于车辆型号是一个离散变量，
我们的模型属于分类器（Classifier）。


## 项目功能

本项目的功能是能根据 CarOwners 的训练数据集（CSV格式），预测一位没有见过的车主的车的型号（Model）。

## 项目构建方法

本项目使用 Maven 作为构建工具，构建过程比较自动化，无须过多人工干涉。
构建本项目的命令如下（以 Ubuntu 操作系统为例）：

```shell
$ sudo apt-get install mvn # 安装 Maven，如果已经安装可忽略。
$ cd mapreduce-knn-demo # 进入项目根目录。
$ mvn package # 编译产生 Jar 包。
```

除了在命令行进行构建，本项目也支持用主流 IDE（如 Eclipse，IDEA等）进行构建，
只需在 IDE 中导入为 Maven 项目即可。

## 项目使用方法

项目构建成功后，先进入项目家目录：
```
$ cd mapreduce-knn-demo
```
在 KNN 子目录，用户可通过修改 KnnParams.txt 文件来传入参数 K 和测试实例，该文件的各字段的含义如下：
```
K, Age, Income, Status, Gender, Children
```
只需按此顺序输入逗号分割的数据即可，不需要输入上述文字。该文件的一个示例如下：
```csv
5, 67, 16668, Single, Male, 3
```
它表示 K=5，实例的年龄为67岁，输入为16668，婚姻状态为 Single（单身），性别是男，有三个孩子。

注意：目前本项目仅支持一次运行预测一个测试实例的标签，故 KnnParams.txt 文件必须只有一行。
