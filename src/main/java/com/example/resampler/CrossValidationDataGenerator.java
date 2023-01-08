// MIT License
// 
// Copyright (c) 2021 Cong Feng
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

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
            // Generate the i-th fold
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
