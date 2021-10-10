package com.example;

// 这个类负责实例化 EvalDataGenerator 对象。
public class EvalDataGeneratorFactory {
    public static final String boostrapName = "boostrap";
    public static final String crossValidateName = "cv";

    public static EvalDataGenerator create(String name, String... args) throws Exception {
        if (name.equals(crossValidateName)) {
            int numFolds = Integer.parseInt(args[0]);
            return new CrossValidationDataGenerator(numFolds);
        }
        if (name.equals(boostrapName)) {
            int numTimes = Integer.parseInt(args[0]);
            double testSampleRatio = Double.parseDouble(args[1]);
            return new BoostrapDataGenerator(numTimes, testSampleRatio);
        }
        throw new Exception("Unknown name: " + name);
    }
}
