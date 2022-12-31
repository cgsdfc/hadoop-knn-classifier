# Hadoop $k$-nearest-neighbors classifier

## Introduction

Based on the Hadoop cluster built on the virtual machine, this project implemented the K-nearest-neighbors classifier algorithm under the MapReduce framework in Java and verified the correctness of the algorithm implementation on a small-scale dataset.

- [Algorithm and software architecture](docs/algorithm-architecture.md)
- [Experimental results](docs/experimental-results.md)


## The basic idea of the KNN classifier

K-Nearest Neighbors Classifier is a parameterless model. Its basic idea is: given the training data set Train and a test case $S$, calculate the distance between $S$ and all the instances in Train, take the nearest K training set instances, and the predicted $S$ label is the label with the largest proportion in these instances.


The KNN classifier can be formulated as follows:
$$
Pr(Y=j|X=x_0)=\frac{1}{K} \sum_{i \in N_0} I(y_i = j)
$$

This formula shows that given $x_0$, the conditional probability of $Y=j$ is equal to the proportion of all training instances labeled $j$ in the K neighborhood to the size K of the K neighborhood. It can also be seen as an approximation of the conditional probability of the Bayesian optimal classifier.


## MapReduce KNN classifier

Because the KNN classifier needs to calculate the distance between test cases and all training cases, the time complexity is relatively high, and the algorithm has poor scalability to big data. With the help of the Hadoop distributed file system and parallel computing framework MapReduce, we can accelerate the KNN classification algorithm. The basic idea of the MapReduce-based KNN classifier is to distribute the training data to each server and calculate the distance between the training instance and the test instance at the same time. Since the distance between different training instances and the test instance is calculated independently of each other, which just conforms to the characteristics of the MapReduce framework, it can achieve a good acceleration effect and is easy to program.


## Build

Maven is used as the construction tool in this project, and the construction process is automated.
The command to build this project is as follows (taking the Ubuntu operating system as an example):

```shell
$ sudo apt-get install mvn
$ cd hadoop-knn-classifier && mvn package
```

In addition to building on the command line, this project also supports building with mainstream IDEs (such as Eclipse, IDEA, etc.), just importing them into the IDE as Maven projects.
