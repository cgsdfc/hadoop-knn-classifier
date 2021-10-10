package com.example;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.hadoop.fs.Path;

// 这个类是KnnEvaluator的封装，提供更加高级的接口。
public class KnnExperiment {

    private static final int numArgs = 3;
    private static final int configFileIndex = 0;
    private static final int trainingFileIndex = 1;
    private static final int resampleMethodIndex = 2;

    private ConfigData configData;

    public static class ConfigData {
        public int K;
        public String datasetName;
        public String trainingFile;
        public String resampleMethod;
        public String[] resampleParams;
    }

    public KnnExperiment(ConfigData configData) {
        this.configData = configData;
    }

    public EvaluationResult run() throws Exception {
        KnnConfigData config = new KnnConfigData();
        config.ds = configData.datasetName;
        config.k = configData.K;
        final String trainingFile = configData.trainingFile;

        EvalDataGenerator generator = EvalDataGeneratorFactory.create(configData.resampleMethod,
                configData.resampleParams);
        KnnEvaluator evaluator = new KnnEvaluator(generator, config, new Path(trainingFile));
        return evaluator.doEvaluation();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < numArgs) {
            System.err.println("Usage: KnnExperiment <configFile> <trainingFile> <resampleMethod> [resampleParams...]");
            System.exit(3);
        }

        ConfigData configData = new ConfigData();

        final String resampleMethod = args[resampleMethodIndex];
        String[] resampleParams = Arrays.copyOfRange(args, resampleMethodIndex + 1, args.length);
        configData.resampleMethod = resampleMethod;
        configData.resampleParams = resampleParams;

        Gson gson = new Gson();
        Reader reader = new FileReader(new File(args[configFileIndex]));
        KnnConfigData data = gson.fromJson(reader, KnnConfigData.class);
        configData.K = data.k;
        configData.datasetName = data.ds;

        final String trainingFile = args[trainingFileIndex];
        configData.trainingFile = trainingFile;

        KnnExperiment experiment = new KnnExperiment(configData);
        EvaluationResult result = experiment.run();

        gson = new GsonBuilder().setPrettyPrinting().create();
        String string = gson.toJson(result);
        System.out.println(string);
    }

}
