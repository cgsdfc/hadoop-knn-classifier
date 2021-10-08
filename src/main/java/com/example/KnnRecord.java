package com.example;

// 这个接口要实现一个距离计算的功能。
public interface KnnRecord {
    public double distance(KnnRecord other);

    public static double computeDistance(KnnRecord a, KnnRecord b) {
        return a.distance(b);
    }
}
