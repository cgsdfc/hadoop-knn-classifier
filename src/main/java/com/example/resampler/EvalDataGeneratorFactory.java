package com.example.resampler;

import java.util.Random;

// 这个类负责实例化 EvalDataGenerator 对象。
public class EvalDataGeneratorFactory {

    static final int noSeed = -1;

    public static final String boostrapName = "boostrap";
    public static final String crossValidateName = "cv";

    public static EvalDataGenerator create(ResampleInfo rsInfo) throws Exception {
        String name = rsInfo.resampleMethod;
        int seed = rsInfo.seed;
        String[] args = rsInfo.resampleParams;

        Random random = seed == noSeed ? new Random() : new Random(seed);
        if (name.equals(crossValidateName)) {
            int numFolds = Integer.parseInt(args[0]);
            return new CrossValidationDataGenerator(numFolds, random);
        }
        if (name.equals(boostrapName)) {
            int numTimes = Integer.parseInt(args[0]);
            double testSampleRatio = Double.parseDouble(args[1]);
            return new BoostrapDataGenerator(numTimes, testSampleRatio, random);
        }
        throw new Exception("Unknown name: " + name);
    }
}
