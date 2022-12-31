package com.example.dataset;

import java.util.Map;
import java.io.IOException;
import java.util.HashMap;

// This is a singleton class. All KnnDatasets are returned by him.
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

    public KnnDataset getDataset(String name) throws IOException {
        KnnDataset ds = nameToDataset.get(name);
        if (ds == null) {
            throw new IOException(String.format("Invalid dataset name %s", name));
        }
        return ds;
    }

    public void registerDataset(String name, KnnDataset ds) {
        nameToDataset.put(name, ds);
    }
}
