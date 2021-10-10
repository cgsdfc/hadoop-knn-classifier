package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.hadoop.fs.Path;

// 这个类是KnnEvaluator的封装，提供更加高级的接口。
public class KnnExperiment {

    public static void main(String[] args) throws Exception {
        final String datasetPath = "/demo/data/iris/iris_training.csv";
        final int numFolds = 3;
        EvalDatasetsGenerator generator = new CrossValidationDataGenerator(numFolds);
        KnnConfigData configData = new KnnConfigData();
        configData.k = 5;
        configData.ds = "iris";
        KnnEvaluator evaluator = new KnnEvaluator(generator, configData, new Path(datasetPath));
        EvaluationResult result = evaluator.doEvaluation();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String string = gson.toJson(result);
        System.out.println(string);
    }

}
