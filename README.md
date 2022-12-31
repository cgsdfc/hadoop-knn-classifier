# A Hadoop-based $k$-nearest-neighbors classifier

[中文版](README-zh.md)

## 1. Introduction

Based on the Hadoop cluster built on the virtual machine, this project implemented the K-nearest-neighbors classifier algorithm under the MapReduce framework in Java and verified its correctness on two small-scale datasets. For a detailed description of this project, please read the following documentation:

- [Algorithm illustration](docs/algorithm-illustration.md)
- [Software architecture](docs/software-architecture.md)
- [Experimental results](docs/experimental-results.md)


### 1.1 The basic idea of a KNN classifier

K-Nearest Neighbors Classifier is a parameterless model. Its basic idea is: given the training data set Train and a test case $S$, calculate the distance between $S$ and all the instances in Train, take the nearest K training set instances, and the predicted $S$ label is the label with the largest proportion in these instances.


The KNN classifier can be formulated as follows:

$$
Pr(Y=j|X=x_0)=\frac{1}{K} \sum_{i \in N_0} I(y_i = j)
$$

This formula shows that given $x_0$, the conditional probability of $Y=j$ is equal to the proportion of all training instances labeled $j$ in the K neighborhood to the size K of the K neighborhood. It can also be seen as an approximation of the conditional probability of the Bayesian optimal classifier.


### 1.2 MapReduce KNN classifier

Since the KNN classifier needs to calculate the distance between test cases and all training cases, the time complexity is relatively high, and the algorithm has poor scalability to big data. With the help of the Hadoop distributed file system and parallel computing framework MapReduce, we can accelerate the KNN classification algorithm. 

The basic idea of the MapReduce KNN classifier is to distribute the training data to each server and calculate the distance between the training instance and the test instance at the same time. Since the distance between different training instances and the test instance is calculated independently of each other, it conforms to the characteristics of the MapReduce framework. Thus it can achieve good acceleration and is easy to understand.


## 2. Build & run

### 2.1 Build

Maven is used as the construction tool in this project, and the construction process is automated.
The command to build this project is as follows (taking the Ubuntu operating system as an example):

```shell
$ sudo apt-get install mvn
$ cd hadoop-knn-classifier && mvn package
```

In addition to building on the command line, this project also supports building with mainstream IDEs (such as Eclipse, IDEA, etc.), just importing them into the IDE as Maven projects.

### 2.2 Run

For how to run the classifier and experiment of this project, please refer to the following scripts:
```
data/iris/run-demo.sh
data/iris/run-exp.sh
data/iris/run-finetune.sh
data/iris/upload-data.sh
```

## 3. Citation

If you find our work useful in your research, please cite us as:

```bibtex
@misc{cong_hadoop-based_2021,
	title = {A {Hadoop}-based {MapReduce} \$k\$-nearest-neighbors {Classifier}},
	shorttitle = {hadoop-knn-classifier},
	url = {https://github.com/cgsdfc/hadoop-knn-classifier.git},
	abstract = {We implemented the K-nearest-neighbors classifier algorithm under the MapReduce framework in Java and verified its correctness on two small-scale datasets. The basic idea of the MapReduce KNN classifier is to distribute the training data to each server and calculate the distance between the training instance and the test instance at the same time. Since the distance between different training instances and the test instance is calculated independently of each other, it conforms to the characteristics of the MapReduce framework. Thus it can achieve good acceleration and is easy to understand.},
	author = {Cong, Feng},
	year = {2021},
}
```
