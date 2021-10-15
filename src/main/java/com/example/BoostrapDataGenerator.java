package com.example;

import java.util.Random;

public class BoostrapDataGenerator implements EvalDataGenerator {
    private int numTimes;
    private double testSampleRatio;
    private Random random;

    public BoostrapDataGenerator(int numTimes, double testSampleRatio, Random random) {
        this.numTimes = numTimes;
        this.testSampleRatio = testSampleRatio; // 0-1
        this.random = random;
    }

    @Override
    public void generate(TextLineDataset originalDataset, EvalDatasetSink sink) throws Exception {
        final int numSamples = originalDataset.data.size();
        final Random random = this.random;
        for (int i = 0; i < this.numTimes; ++i) {
            // The boostrapped dataset.
            TextLineDataset resampledData = new TextLineDataset();
            for (int j = 0; j < numSamples; ++j) {
                // 欧皇十连抽。
                int draw = random.nextInt(numSamples);
                resampledData.data.add(originalDataset.data.get(draw));
            }
            // Split the resampledData into test and train.
            EvalDataset dataset = new EvalDataset();
            int testEnd = (int) (numSamples * this.testSampleRatio);
            for (int j = 0; j < numSamples; ++j) {
                TextLineDataset testOrTrain = j < testEnd ? dataset.testing : dataset.training;
                testOrTrain.data.add(resampledData.data.get(j));
            }
            sink.receive(dataset);
        }
    }

    @Override
    public String getSpecs() {
        return String.format("boostrap-%d-times-%.1f-testSampleRatio", this.numTimes, this.testSampleRatio);
    }
}
