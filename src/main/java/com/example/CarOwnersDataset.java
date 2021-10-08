package com.example;

public class CarOwnersDataset implements KnnDataset {
    @Override
    public KnnRecord createRecord(String string, boolean is_testing) {
        return new CarOwnerRecord(string, is_testing);
    }
}
