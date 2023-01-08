// MIT License
// 
// Copyright (c) 2021 Cong Feng
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

package com.example.experiment;

import java.util.Arrays;

import com.example.knn.KnnConfigData;
import com.example.resampler.EvalDataGenerator;
import com.example.resampler.EvalDataGeneratorFactory;
import com.example.resampler.ResampleInfo;
import com.example.utils.FsUtils;
import com.example.utils.LogUtils;

import org.apache.hadoop.fs.Path;

// This class is the encapsulation of KnnEvaluator, providing a more advanced interface.
public class KnnExperiment {

    private static final int numArgs = 3;
    private static final int configFileIndex = 0;
    private static final int trainingFileIndex = 1;
    private static final int resampleMethodIndex = 2;
    private static final String tag = "KnnExperiment";

    private KnnExpConfigData configData;

    public static class KnnExpConfigData {
        public int K;
        public DatasetInfo dsInfo = new DatasetInfo();
        public ResampleInfo rsInfo = new ResampleInfo();
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
