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

    private static final int numArgs = 2;
    private static final int defaultKmax = 10;

    public static class SingleExpResult {
        public int K;
        public double acc;
    }

    public static class FinalExpResult {
        KnnExpConfigData config;
        public ArrayList<SingleExpResult> tuningResults = new ArrayList<>();
        public ArrayList<SingleExpResult> testingResults = new ArrayList<>();
        public SingleExpResult bestTuningResult = new SingleExpResult();
        public SingleExpResult bestTestingResult = new SingleExpResult();

        public FinalExpResult(KnnExpConfigData config) {
            this.config = config;
            bestTuningResult.K = bestTestingResult.K = -1;
            bestTuningResult.acc = bestTestingResult.acc = -1;
        }
    }

    public KnnFineTune(KnnExpConfigData configData, int Kmax) {
        this.configData = configData;
        this.Kmax = Kmax;
    }

    // 对每个K值，通过交叉检验来找到性能最好的。
    private void findBestParams(FinalExpResult finalExpResult) throws Exception {
        for (int K = Kmin; K <= Kmax; K += Kstep) {
            configData.K = K;
            KnnExperiment experiment = new KnnExperiment(configData);
            EvaluationResult result = experiment.run();
            SingleExpResult tuningResult = new SingleExpResult();
            tuningResult.K = K;
            tuningResult.acc = result.mean;
            finalExpResult.tuningResults.add(tuningResult);
            if (tuningResult.acc > finalExpResult.bestTuningResult.acc) {
                finalExpResult.bestTuningResult = tuningResult;
            }
        }
    }

    // 对于每个K值，都到测试集上去跑一下，看性能是不是最好的。
    private void runOnTestingData(FinalExpResult finalExpResult) throws Exception {
        for (SingleExpResult result : finalExpResult.tuningResults) {
            KnnPredictor predictor = new KnnPredictor(result.K);
            ResultJsonData jsonData = predictor.predict(configData.dsInfo);
            SingleExpResult testingResult = new SingleExpResult();
            testingResult.K = result.K;
            testingResult.acc = jsonData.accuracy;
            finalExpResult.testingResults.add(testingResult);
            if (testingResult.acc > finalExpResult.bestTestingResult.acc) {
                finalExpResult.bestTestingResult = testingResult;
            }

        }
    }

    public FinalExpResult run() throws Exception {
        FinalExpResult finalExpResult = new FinalExpResult(this.configData);
        findBestParams(finalExpResult);
        runOnTestingData(finalExpResult);
        return finalExpResult;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != numArgs) {
            System.err.println("Usage: KnnFineTune <configFile> <Kmax>");
            System.exit(4);
        }

        KnnExpConfigData configData = FsUtils.readFromJsonFormatLocal(args[0], KnnExpConfigData.class);
        int Kmax = Integer.parseInt(args[1]);
        KnnFineTune fineTune = new KnnFineTune(configData, Kmax);
        FinalExpResult result = fineTune.run();
        String jsonString = FsUtils.toJsonString(result);
        System.out.println(jsonString);
    }

}
