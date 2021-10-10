package com.example;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;

import com.example.EvalDatasetsGenerator.EvalDataset;
import com.example.EvalDatasetsGenerator.EvalDatasetSink;
import com.google.gson.Gson;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

// 这个类负责输入文件的准备，输出文件的回收，作业的运行等。
public class KnnEvaluator {

    private FileSystem fileSystem;
    private Path originalDatasetPath;
    private TextLineDataset originalDataset;
    private EvalDatasetsGenerator generator;
    private KnnConfigData configData;
    private ArrayList<Double> jobResults;
    private int jobCount = 0;

    private static final Path evaluatorHome = new Path("/knn-eval/");
    private static final Path trainingDatasetPath = new Path(evaluatorHome, "training.csv");
    private static final Path testingDatasetPath = new Path(evaluatorHome, "testing.csv");
    private static final Path outputDir = new Path(evaluatorHome, "result/");
    private static final Path configFilePath = new Path(evaluatorHome, "config.json");

    public KnnEvaluator(EvalDatasetsGenerator generator, KnnConfigData configData) throws Exception {
        this.generator = generator;
        this.fileSystem = FsUtils.getFileSystem();
        this.configData = configData;
    }

    public EvaluationResult doEvaluation() throws Exception {
        setup();
        this.generator.generate(this.originalDataset, new EvalDatasetSink() {
            @Override
            public void receive(EvalDataset dataset) throws Exception {
                KnnEvaluator.this.onReceiveEvalDataset(dataset);
            }
        });
        cleanup();
        double[] meanAndStd = DataUtils.computeMeanAndStd(jobResults);
        EvaluationResult result = new EvaluationResult();
        result.mean = meanAndStd[0];
        result.std = meanAndStd[1];
        return result;
    }

    private void setup() throws Exception {
        jobResults = new ArrayList<>();
        jobCount = 0;
        fileSystem.mkdirs(evaluatorHome);
        writeConfigFile();
        readOriginalDataset();
    }

    private void cleanup() throws Exception {
        fileSystem.close();
    }

    // 写入配置文件。
    private void writeConfigFile() throws Exception {
        Gson gson = new Gson();
        String configString = gson.toJson(this.configData);
        FsUtils.write(fileSystem, outputDir, configString);
    }

    // 读入原始数据集。
    private void readOriginalDataset() throws Exception {
        BufferedReader reader = FsUtils.openTextFile(this.fileSystem, this.originalDatasetPath);
        this.originalDataset = new TextLineDataset(reader);
    }

    private void onReceiveEvalDataset(EvalDataset dataset) throws Exception {
        // 把测试数据写入hdfs。覆盖原来文件
        FsUtils.writeLines(fileSystem, trainingDatasetPath, dataset.training.data);
        FsUtils.writeLines(fileSystem, testingDatasetPath, dataset.testing.data);
        FsUtils.remove(fileSystem, outputDir);

        // 运行knn分类器任务。
        KnnClassifierJob job = new KnnClassifierJob(//
                configFilePath.toString(), //
                testingDatasetPath.toString(), //
                trainingDatasetPath.toString(), outputDir.toString(), jobCount++);
        job.run();

        // 获取结果文件。
        Path resultFile = null;
        for (FileStatus f : fileSystem.listStatus(outputDir)) {
            if (f.getLen() > 0) {
                resultFile = f.getPath();
                break;
            }
        }

        // 从结果文件中获取准确率数据。
        Gson gson = new Gson();
        Reader reader = FsUtils.openTextFile(fileSystem, resultFile);
        ResultJsonData jsonData = gson.fromJson(reader, ResultJsonData.class);
        jobResults.add(jsonData.accuracy);
    }

}
