package com.example.utils;

public class DataUtils {

    // 把输入的字符串转化为浮点数，并以输入的最大最小值对其进行正则化。
    // 正则化后的取值范围为0-1。
    public static double minMaxNormalize(double n1, double minValue, double maxValue) {
        return (n1 - minValue) / (maxValue - minValue);
    }

    // 计算两个离散型变量的距离。这里我们简单把距离定义为两个变量是否相等。
    // 这个定义所产生的距离与上述的正则化产生的距离在值域上是一致的。
    public static double nominalDistance(String t1, String t2) {
        if (t1.equals(t2)) {
            return 0;
        } else {
            return 1;
        }
    }

    public static double sumOfSquares(double... args) {
        double ans = 0;
        for (double val : args) {
            ans += Math.pow(val, 2);
        }
        return ans;
    }

    public static double squaredEuclideDistance(double[] v1, double[] v2) {
        double[] diff = new double[v1.length];
        for (int i = 0; i < diff.length; ++i) {
            diff[i] = v1[i] - v2[i];
        }
        return sumOfSquares(diff);
    }

    public static double[] computeMeanAndStd(Iterable<Double> data) {
        double mean = 0;
        double std = 0;
        int length = 0;
        for (double x : data) {
            mean += x;
            length++;
        }
        mean /= length;
        for (double x : data) {
            std += Math.pow(x - mean, 2);
        }
        std /= length - 1;
        std = Math.pow(std, 0.5);
        return new double[] { mean, std };
    }
}
