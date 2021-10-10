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
        public double mean;
    }

    public static class FinalExpResult {
        public ArrayList<SingleExpResult> results = new ArrayList<>();
        public SingleExpResult best = new SingleExpResult();

        public FinalExpResult() {
            best.K = -1;
            best.mean = -1;
        }
    }

    public KnnFineTune(KnnExpConfigData configData, int Kmax) {
        this.configData = configData;
        this.Kmax = Kmax;
    }

    public FinalExpResult run() throws Exception {
        FinalExpResult finalExpResult = new FinalExpResult();

        for (int K = Kmin; K <= Kmax; K += Kstep) {
            configData.K = K;
            KnnExperiment experiment = new KnnExperiment(configData);
            EvaluationResult result = experiment.run();
            SingleExpResult singleExpResult = new SingleExpResult();
            singleExpResult.K = K;
            singleExpResult.mean = result.mean;
            finalExpResult.results.add(singleExpResult);
            if (singleExpResult.mean > finalExpResult.best.mean) {
                finalExpResult.best = singleExpResult;
            }
        }

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
