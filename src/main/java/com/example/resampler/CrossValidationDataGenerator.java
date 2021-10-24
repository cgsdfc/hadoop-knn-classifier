package com.example.resampler;

import java.util.Collections;
import java.util.Random;

public class CrossValidationDataGenerator implements EvalDataGenerator {
    private int numFolds;
    private Random random;

    public CrossValidationDataGenerator(int numFolds, Random random) {
        this.numFolds = numFolds;
        this.random = random;
    }

    @Override
    public void generate(TextLineDataset originalDataset, EvalDatasetSink sink) throws Exception {
        final int numSamples = originalDataset.data.size();
        final int foldSize = numSamples / this.numFolds;
        if (foldSize == 0) {
            throw new Exception("numFold must >= originalDataset.size");
        }

        Collections.shuffle(originalDataset.data, this.random);
        for (int i = 0; i < this.numFolds; ++i) {
            // 生成第i个Fold
            int testStart = i * foldSize;
            int testEnd = Math.min(testStart + foldSize, numSamples);
            EvalDataset dataset = new EvalDataset();

            for (int j = 0; j < numSamples; ++j) {
                TextLineDataset testOrTrain = (testStart <= j && j < testEnd) ? //
                        dataset.testing : dataset.training;
                testOrTrain.data.add(originalDataset.data.get(j));
            }
            sink.receive(dataset);
        }
    }

    @Override
    public String getSpecs() {
        return String.format("cv-%d-fold", this.numFolds);
    }
}
