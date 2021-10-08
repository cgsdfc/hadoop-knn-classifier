package com.example;

import java.util.Map;
import java.util.HashMap;

public class KnnDatasetFactory {
    
    private Map<String, KnnDataset> nameToDataset = new HashMap<String, KnnDataset>();
    private static KnnDatasetFactory instance;

    private KnnDatasetFactory() {
    }

    public static KnnDatasetFactory get() {
        if (instance == null) {
            instance = new KnnDatasetFactory();
        }
        return instance;
    }

    public KnnDataset getDataset(String name) {
        return nameToDataset.get(name);
    }

    public void registerDataset(String name, KnnDataset ds) {
        nameToDataset.put(name, ds);
    }
}
