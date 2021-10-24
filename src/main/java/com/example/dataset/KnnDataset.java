package com.example.dataset;

public interface KnnDataset {

    
    public static final double invalidDistance = -1;

    public KnnRecord createRecord(String string);

    public double distance(KnnRecord a, KnnRecord b);
}
