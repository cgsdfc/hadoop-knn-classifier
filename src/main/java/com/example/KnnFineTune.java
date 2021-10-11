package com.example;

import java.util.ArrayList;

import com.example.KnnExperiment.*;

// 实现一个比较基本的调参过程。
// 在训练集上，通过交叉检验来获取比较好的K值，然后在测试数据集上看性能。
public class KnnFineTune {
    private KnnExpConfigData configData;
    private int Kmin = 1;
    private int Kstep = 1;
    private int Kmax = defaultKmax;

    private static final int numArgs = 3;
    private static final int defaultKmax = 10;

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
        KnnExpConfigData config;
        public ArrayList<TuningResult> tuningResults = new ArrayList<>();
        public ArrayList<TestingResult> testingResults = new ArrayList<>();
        public TuningResult bestTuningResult = new TuningResult();
        public TestingResult bestTestingResult = new TestingResult();

        public FineTuneResult(KnnExpConfigData config) {
            this.config = config;
            bestTuningResult.K = bestTestingResult.K = -1;
            bestTuningResult.mean = bestTestingResult.mean = -1;
        }
    }

    public KnnFineTune(KnnExpConfigData configData, int Kmax) {
        this.configData = configData;
        this.Kmax = Kmax;
    }

    // 对每个K值，通过交叉检验来找到性能最好的。
    private void findBestParams(FineTuneResult fineTuneResult) throws Exception {
        for (int K = Kmin; K <= Kmax; K += Kstep) {
            configData.K = K;
            KnnExperiment experiment = new KnnExperiment(configData);
            EvaluationResult result = experiment.run();
            TuningResult tuningResult = new TuningResult();
            tuningResult.K = K;
            tuningResult.mean = result.mean;
            tuningResult.std = result.std;

            fineTuneResult.tuningResults.add(tuningResult);
            if (tuningResult.mean - tuningResult.std > fineTuneResult.bestTuningResult.mean
                    + fineTuneResult.bestTuningResult.std) {
                fineTuneResult.bestTuningResult = tuningResult;
            }
        }
    }

    // 对于每个K值，都到测试集上去跑一下，看性能是不是最好的。
    private void runOnTestingData(FineTuneResult funeTuneResult) throws Exception {
        for (TuningResult result : funeTuneResult.tuningResults) {
            KnnPredictor predictor = new KnnPredictor(result.K);
            ResultJsonData jsonData = predictor.predict(configData.dsInfo);
            TestingResult testingResult = new TestingResult();
            testingResult.K = result.K;
            testingResult.mean = jsonData.accuracy;
            funeTuneResult.testingResults.add(testingResult);
            // 测试集上没有std。
            if (testingResult.mean > funeTuneResult.bestTestingResult.mean) {
                funeTuneResult.bestTestingResult = testingResult;
            }

        }
    }

    public FineTuneResult run() throws Exception {
        FineTuneResult finalExpResult = new FineTuneResult(this.configData);
        findBestParams(finalExpResult);
        runOnTestingData(finalExpResult);
        return finalExpResult;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != numArgs) {
            System.err.println("Usage: KnnFineTune <configFile> <Kmax> <outputFile>");
            System.exit(4);
        }

        KnnExpConfigData configData = FsUtils.readFromJsonFormatLocal(args[0], KnnExpConfigData.class);
        int Kmax = Integer.parseInt(args[1]);
        String outputFile = args[2];
        KnnFineTune fineTune = new KnnFineTune(configData, Kmax);
        FineTuneResult result = fineTune.run();
        FsUtils.writeInJsonFormatLocal(outputFile, result);
        System.out.println(FsUtils.toJsonString(result));
    }

}
