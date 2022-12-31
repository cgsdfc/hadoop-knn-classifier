// MIT License
// 
// Copyright (c) 2021 cgsdfc
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.example.resampler;

import java.util.Random;

// This class is responsible for instantiating EvalDataGenerator objects.
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
