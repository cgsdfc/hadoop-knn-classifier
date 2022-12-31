# 基于 Hadoop MapReduce 的 K 近邻分类器

[English version](README.md)


## 项目简介

本项目基于虚拟机上搭建的 Hadoop 集群，用 Java 实现了 MapReduce 框架下的K近邻分类器算法，
并在一个小规模数据集上验证了算法实现的正确性。

- [算法和软件架构](docs/算法和软件架构.md)
- [实验结果](docs/实验结果.md)



## K 近邻分类器

K 近邻分类器（K-Nearest-Neighbors Classifier），简称 KNN 分类器，是一种无参数模型，
它的基本思想是：给定训练数据集 Train 和一个测试实例 S，计算 S 和 Train 中所有实例的距离，
取距离最近的 K 个训练集实例，则预测S的标签为这些实例中占比最大的标签。

KNN 分类器的数学表达式如下：
$$
Pr(Y=j|X=x_0)=\frac{1}{K} \sum_{i \in N_0} I(y_i = j)
$$
这个式子表明：给定 x0，Y=j 的条件概率等于在K邻域内所有标签为 j 的训练实例占K邻域的大小 K 的比例。
这是对贝叶斯最优分类器的条件概率公式的一个近似。

## 基于 MapReduce 的 KNN 分类器

由于KNN分类器需要计算测试实例和所有训练实例的距离，所以时间复杂度比较高，算法对大数据的扩展性不好。在 Hadoop 分布式文件系统 HDFS 和 并行计算框架 MapReduce 的帮助下，我们可以对 KNN 分类算法进行加速。基于 MapReduce 的 KNN 分类器的基本思想，就是把训练数据分布到各个服务器，同时计算训练实例和测试实例的距离，由于不同训练实例与测试实例的距离的计算是彼此无关，正好符合MapReduce框架的特性，因此能获得很好的加速效果，也易于编程实现。



## 项目构建方法

本项目使用 Maven 作为构建工具，构建过程比较自动化。
构建本项目的命令如下（以 Ubuntu 操作系统为例）：

```shell
$ sudo apt-get install mvn
$ cd mapreduce-knn-demo && mvn package
```

除了在命令行进行构建，本项目也支持用主流 IDE（如 Eclipse，IDEA等）进行构建，
只需在 IDE 中导入为 Maven 项目即可。
