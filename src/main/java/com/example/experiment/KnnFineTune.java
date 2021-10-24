package com.example.experiment;

import java.util.ArrayList;

import com.example.experiment.KnnExperiment.*;
import com.example.knn.ResultJsonData;
import com.example.resampler.ResampleInfo;
import com.example.utils.FsUtils;
import com.example.utils.LogUtils;

// 实现一个比较基本的调参过程。
// 在训练集上，通过交叉检验来获取比较好的K值，然后在测试数据集上看性能。
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

    // 对每个K值，通过交叉检验来找到性能最好的。
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
