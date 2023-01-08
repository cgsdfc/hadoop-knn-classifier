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

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;

import com.example.knn.KnnClassifier;
import com.example.knn.KnnConfigData;
import com.example.knn.ResultJsonData;
import com.example.resampler.EvalDataGenerator;
import com.example.resampler.TextLineDataset;
import com.example.resampler.EvalDataGenerator.EvalDataset;
import com.example.resampler.EvalDataGenerator.EvalDatasetSink;
import com.example.utils.DataUtils;
import com.example.utils.FsUtils;
import com.example.utils.LogUtils;
import com.google.gson.Gson;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

// This class is responsible for the preparation of input files, recycling of output files, running jobs, etc.
public class KnnEvaluator {

    private FileSystem fileSystem;
    private Path originalDatasetPath;
    private TextLineDataset originalDataset;
    private EvalDataGenerator generator;
    private KnnConfigData configData;
    private ArrayList<Double> jobResults;
    private int jobCount = 0;

    private static final Path evaluatorHome = new Path("/knn-eval/");
    private static final Path trainingDatasetPath = new Path(evaluatorHome, "training.csv");
    private static final Path testingDatasetPath = new Path(evaluatorHome, "testing.csv");
    private static final Path outputDir = new Path(evaluatorHome, "result/");
    private static final Path configFilePath = new Path(evaluatorHome, "config.json");
    private static final Path evaluationResultPath = new Path(evaluatorHome, "eval-result.json");
    private static final String tag = "KnnEvaluator";

    public KnnEvaluator(EvalDataGenerator generator, KnnConfigData configData, Path originalDatasetPath)
            throws Exception {
        this.generator = generator;
        this.configData = configData;
        this.originalDatasetPath = originalDatasetPath;
    }

    public EvaluationResult doEvaluation() throws Exception {
        setup();
        LogUtils.info(tag, "resample method: %s", this.generator.getSpecs());

        this.generator.generate(this.originalDataset, new EvalDatasetSink() {
            @Override
            public void receive(EvalDataset dataset) throws Exception {
                KnnEvaluator.this.onReceiveEvalDataset(dataset);
            }
        });
        EvaluationResult result = generateResult();
        cleanup();
        return result;
    }

    private EvaluationResult generateResult() throws Exception {
        double[] meanAndStd = DataUtils.computeMeanAndStd(jobResults);
        EvaluationResult result = new EvaluationResult();
        result.mean = meanAndStd[0];
        result.std = meanAndStd[1];
        result.K = configData.k;
        result.datasetName = configData.ds;
        result.resampleMethod = this.generator.getSpecs();
        FsUtils.writeInJsonFormatHDFS(fileSystem, evaluationResultPath, result);
        return result;
    }

    private void setup() throws Exception {
        fileSystem = FsUtils.getFileSystem();
        jobResults = new ArrayList<>();
        jobCount = 0;
        FsUtils.remove(fileSystem, evaluatorHome);
        fileSystem.mkdirs(evaluatorHome);
        writeConfigFile();
        readOriginalDataset();
    }

    private void cleanup() throws Exception {
        fileSystem.close();
    }

    // Write configuration file.
    private void writeConfigFile() throws Exception {
        String configString = FsUtils.toJsonString(this.configData);
        FsUtils.write(fileSystem, configFilePath, configString);
    }

    // Read in the raw dataset.
    private void readOriginalDataset() throws Exception {
        BufferedReader reader = FsUtils.openTextFile(this.fileSystem, this.originalDatasetPath);
        this.originalDataset = new TextLineDataset(reader);
    }

    private void onReceiveEvalDataset(EvalDataset dataset) throws Exception {
        LogUtils.info(tag, "recv %d testing, %d training from generator", dataset.testing.data.size(),
                dataset.training.data.size());

        // Write test data to hdfs. overwrite original file
        FsUtils.writeLines(fileSystem, trainingDatasetPath, dataset.training.data);
        FsUtils.writeLines(fileSystem, testingDatasetPath, dataset.testing.data);
        FsUtils.remove(fileSystem, outputDir);

        // Run the knn classifier task.
        KnnClassifier job = new KnnClassifier(//
                configFilePath.toString(), //
                testingDatasetPath.toString(), //
                trainingDatasetPath.toString(), outputDir.toString(), jobCount++);

        LogUtils.info(tag, "start job %s on dataset %s", job.getJobName(), configData.ds);
        job.runWithRetry();
        LogUtils.info(tag, "done");

        // Get the result file.
        Path resultFile = null;
        for (FileStatus f : fileSystem.listStatus(outputDir)) {
            if (f.getLen() > 0) {
                resultFile = f.getPath();
                break;
            }
        }
        if (resultFile == null) {
            throw new Exception("Job succeeded but result file not found");
        }

        // Get the accuracy data from the result file.
        Gson gson = new Gson();
        Reader reader = FsUtils.openTextFile(fileSystem, resultFile);
        ResultJsonData jsonData = gson.fromJson(reader, ResultJsonData.class);
        jobResults.add(jsonData.accuracy);
    }

}
