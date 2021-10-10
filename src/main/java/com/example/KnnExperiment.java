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

    public static void main(String[] args) throws Exception {
        if (args.length < numArgs) {
            System.err.println("Usage: KnnExperiment <configFile> <trainingFile> <resampleMethod> [resampleParams...]");
            System.exit(3);
        }

        final String resampleMethod = args[resampleMethodIndex];
        String[] resampleParams = Arrays.copyOfRange(args, resampleMethodIndex + 1, args.length);
        EvalDataGenerator generator = EvalDataGeneratorFactory.create(resampleMethod, resampleParams);

        Gson gson = new Gson();
        Reader reader = new FileReader(new File(args[configFileIndex]));
        KnnConfigData configData = gson.fromJson(reader, KnnConfigData.class);

        final String trainingFile = args[trainingFileIndex];
        KnnEvaluator evaluator = new KnnEvaluator(generator, configData, new Path(trainingFile));
        EvaluationResult result = evaluator.doEvaluation();

        gson = new GsonBuilder().setPrettyPrinting().create();
        String string = gson.toJson(result);
        System.out.println(string);
    }

}
