// MIT License
// 
// Copyright (c) 2021 cgsdfc
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

package com.example.experiment;

import java.util.ArrayList;

import com.example.experiment.KnnExperiment.*;
import com.example.knn.ResultJsonData;
import com.example.resampler.ResampleInfo;
import com.example.utils.FsUtils;
import com.example.utils.LogUtils;

// Implement a relatively basic parameter tuning process.
// On the training set, obtain a better K value through cross-validation, 
// and then look at the performance on the test data set.
public class KnnFineTune {

    private KnnFineTuneConfigData configData;
    private static final int numArgs = 2;
    private static final String tag = "KnnFineTune";

    public static class KnnFineTuneConfigData {
        private int Kmin;
        private int Kstep;
        private int Kmax;
        public DatasetInfo dsInfo;
        public ResampleInfo rsInfo;
    }

    public static class TuningResult {
        public int K;
        public double mean;
        public double std;
    }

    public static class TestingResult {
        public int K;
        public double mean;
    }

    public static class FineTuneResult {
        KnnFineTuneConfigData config;
        public ArrayList<TuningResult> tuningResults = new ArrayList<>();
        public ArrayList<TestingResult> testingResults = new ArrayList<>();
        public TuningResult bestTuningResult = new TuningResult();
        public TestingResult bestTestingResult = new TestingResult();

        public FineTuneResult(KnnFineTuneConfigData config) {
            this.config = config;
            bestTuningResult.K = bestTestingResult.K = -1;
            bestTuningResult.mean = bestTestingResult.mean = -1;
        }
    }

    public KnnFineTune(KnnFineTuneConfigData configData) {
        this.configData = configData;
    }

    // For each value of K, cross-check to find the best performer.
    private void findBestParams(FineTuneResult fineTuneResult) throws Exception {
        KnnExpConfigData expConfigData = new KnnExpConfigData();
        expConfigData.dsInfo = this.configData.dsInfo;
        expConfigData.rsInfo = this.configData.rsInfo;

        for (int K = configData.Kmin; K <= configData.Kmax; K += configData.Kstep) {
            expConfigData.K = K;
            KnnExperiment experiment = new KnnExperiment(expConfigData);
            EvaluationResult result = experiment.run();
            TuningResult tuningResult = new TuningResult();
            tuningResult.K = K;
            tuningResult.mean = result.mean;
            tuningResult.std = result.std;

            fineTuneResult.tuningResults.add(tuningResult);
            if (tuningResult.mean > fineTuneResult.bestTuningResult.mean) {
                fineTuneResult.bestTuningResult = tuningResult;
            }
        }
    }

    // For each K value, run it on the test set to see if the performance is the
    // best.
    private void runOnTestingData(FineTuneResult funeTuneResult) throws Exception {
        for (TuningResult result : funeTuneResult.tuningResults) {
            KnnPredictor predictor = new KnnPredictor(result.K);
            ResultJsonData jsonData = predictor.predict(configData.dsInfo);
            TestingResult testingResult = new TestingResult();
            testingResult.K = result.K;
            testingResult.mean = jsonData.accuracy;
            funeTuneResult.testingResults.add(testingResult);
            if (testingResult.mean > funeTuneResult.bestTestingResult.mean) {
                funeTuneResult.bestTestingResult = testingResult;
            }
        }
    }

    public FineTuneResult run() throws Exception {
        FineTuneResult finalExpResult = new FineTuneResult(this.configData);
        LogUtils.info(tag, "finding best K on trainingFile %s", configData.dsInfo.trainingFile);
        findBestParams(finalExpResult);
        LogUtils.info(tag, "done");

        LogUtils.info(tag, "running different Ks on testingFile %s", configData.dsInfo.testingFile);
        runOnTestingData(finalExpResult);
        LogUtils.info(tag, "done");
        return finalExpResult;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != numArgs) {
            System.err.println("Usage: KnnFineTune <configFile> <outputFile>");
            System.exit(4);
        }

        KnnFineTuneConfigData configData = FsUtils.readFromJsonFormatLocal(args[0], KnnFineTuneConfigData.class);
        String outputFile = args[1];
        KnnFineTune fineTune = new KnnFineTune(configData);
        FineTuneResult result = fineTune.run();
        FsUtils.writeInJsonFormatLocal(outputFile, result);
        System.out.println(FsUtils.toJsonString(result));
    }

}
