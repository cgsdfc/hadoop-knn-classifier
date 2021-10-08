package com.example;

import java.util.Map;
import java.util.HashMap;

// 这是一个单例模式的类，所有的 KnnDataset 都是由他返回的。
public class KnnDatasetFactory {

    private Map<String, KnnDataset> nameToDataset = new HashMap<String, KnnDataset>();
    private static KnnDatasetFactory instance;

    private void registerKnownDatasets() {
        registerDataset("iris", new IrisDataset());
        registerDataset("car_owners", new CarOwnersDataset());
    }

    private KnnDatasetFactory() {
        registerKnownDatasets();
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
