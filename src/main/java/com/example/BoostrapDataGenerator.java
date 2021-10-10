package com.example;

import java.util.Random;

public class BoostrapDataGenerator implements EvalDataGenerator {
    private int numTimes;
    private double testSampleRatio;

    public BoostrapDataGenerator(int numTimes, double testSampleRatio) {
        this.numTimes = numTimes;
        this.testSampleRatio = testSampleRatio; // 0-1
    }

    @Override
    public void generate(TextLineDataset originalDataset, EvalDatasetSink sink) throws Exception {
        final int numSamples = originalDataset.data.size();
        final Random random = new Random();
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
}
