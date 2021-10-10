package com.example;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.hadoop.fs.Path;

// 这个类是KnnEvaluator的封装，提供更加高级的接口。
public class KnnExperiment {

    private static final int numArgs = 3;

    public static void main(String[] args) throws Exception {
        if (args.length != numArgs) {
            System.err.println("Usage: KnnExperiment <configFile> <numFolds> <trainingFile>");
            System.exit(3);
        }

        final String datasetPath = args[2];
        final int numFolds = Integer.parseInt(args[1]);
        EvalDatasetsGenerator generator = new CrossValidationDataGenerator(numFolds);

        Gson gson = new Gson();
        Reader reader = new FileReader(new File(args[0]));
        KnnConfigData configData = gson.fromJson(reader, KnnConfigData.class);

        KnnEvaluator evaluator = new KnnEvaluator(generator, configData, new Path(datasetPath));
        EvaluationResult result = evaluator.doEvaluation();

        gson = new GsonBuilder().setPrettyPrinting().create();
        String string = gson.toJson(result);
        System.out.println(string);
    }

}
