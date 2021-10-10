package com.example;

import java.util.Collections;

public class CrossValidationDataGenerator implements EvalDatasetsGenerator {
    private int numFolds;

    public CrossValidationDataGenerator(int numFolds) {
        this.numFolds = numFolds;
    }

    @Override
    public void generate(TextLineDataset originalDataset, EvalDatasetSink sink) {
        Collections.shuffle(originalDataset.data);
        for (int start = 0; start < originalDataset.data.size(); start += numFolds) {
            int end=

        }
    }
}
