package com.example;

import java.util.Arrays;

import org.apache.hadoop.fs.Path;

// 这个类是KnnEvaluator的封装，提供更加高级的接口。
public class KnnExperiment {

    private static final int numArgs = 3;
    private static final int configFileIndex = 0;
    private static final int trainingFileIndex = 1;
    private static final int resampleMethodIndex = 2;
    private static final String tag = "KnnExperiment";

    private KnnExpConfigData configData;

    public static class DatasetInfo {
        public String datasetName;
        public String trainingFile;
        public String testingFile;
    }

    public static class KnnExpConfigData {
        public int K;
        public DatasetInfo dsInfo = new DatasetInfo();
        public EvalDataGeneratorFactory.ResampleInfo rsInfo = new EvalDataGeneratorFactory.ResampleInfo();
    }

    public KnnExperiment(KnnExpConfigData configData) {
        this.configData = configData;
    }

    public EvaluationResult run() throws Exception {
        KnnConfigData config = new KnnConfigData();
        config.ds = configData.dsInfo.datasetName;
        config.k = configData.K;
        final String trainingFile = configData.dsInfo.trainingFile;

        LogUtils.info(tag, "start experiment K=%d, trainingFile=%s", config.k, configData.dsInfo.trainingFile);
        EvalDataGenerator generator = EvalDataGeneratorFactory.create(configData.rsInfo);

        KnnEvaluator evaluator = new KnnEvaluator(generator, config, new Path(trainingFile));
        return evaluator.doEvaluation();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < numArgs) {
            System.err.println("Usage: KnnExperiment <configFile> <trainingFile> <resampleMethod> [resampleParams...]");
            System.exit(3);
        }

        KnnExpConfigData configData = new KnnExpConfigData();

        final String resampleMethod = args[resampleMethodIndex];
        String[] resampleParams = Arrays.copyOfRange(args, resampleMethodIndex + 1, args.length);
        configData.rsInfo.resampleMethod = resampleMethod;
        configData.rsInfo.resampleParams = resampleParams;

        KnnConfigData data = FsUtils.readFromJsonFormatLocal(args[configFileIndex], KnnConfigData.class);
        configData.K = data.k;
        configData.dsInfo.datasetName = data.ds;

        final String trainingFile = args[trainingFileIndex];
        configData.dsInfo.trainingFile = trainingFile;

        KnnExperiment experiment = new KnnExperiment(configData);
        EvaluationResult result = experiment.run();
        String string = FsUtils.toJsonString(result);
        System.out.println(string);
    }

}
