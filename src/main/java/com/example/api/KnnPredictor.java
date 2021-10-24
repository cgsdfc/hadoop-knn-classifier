package com.example.api;

import com.example.knn.KnnClassifier;
import com.example.knn.KnnConfigData;
import com.example.knn.ResultJsonData;
import com.example.utils.FsUtils;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

// 这是对 KnnClassifier 的封装。
public class KnnPredictor {
    private int K;
    private FileSystem fileSystem;

    private static final Path predictorHome = new Path("/knn-pred/");
    private static final Path outputDir = new Path(predictorHome, "result/");
    private static final Path configFilePath = new Path(predictorHome, "config.json");

    public KnnPredictor(int K) {
        this.K = K;
    }

    private void setup(String ds) throws Exception {
        fileSystem = FsUtils.getFileSystem();
        FsUtils.remove(fileSystem, outputDir);

        KnnConfigData data = new KnnConfigData();
        data.k = this.K;
        data.ds = ds;
        FsUtils.writeInJsonFormatHDFS(fileSystem, configFilePath, data);
    }

    private ResultJsonData run(DatasetInfo dsInfo) throws Exception {
        final String testingDatasetPath = dsInfo.testingFile;
        final String trainingDatasetPath = dsInfo.trainingFile;

        // 运行knn分类器任务。
        KnnClassifier job = new KnnClassifier(//
                configFilePath.toString(), //
                testingDatasetPath.toString(), //
                trainingDatasetPath.toString(), outputDir.toString(), 0);

        job.runWithRetry();

        // 获取结果文件。
        Path resultFile = null;
        for (FileStatus f : fileSystem.listStatus(outputDir)) {
            if (f.getLen() > 0) {
                resultFile = f.getPath();
                break;
            }
        }

        // 从结果文件中获取准确率数据。
        return FsUtils.readFromJsonFormatHDFS(fileSystem, resultFile.toString(), ResultJsonData.class);
    }

    public ResultJsonData predict(DatasetInfo dsInfo) throws Exception {
        setup(dsInfo.datasetName);
        ResultJsonData result = run(dsInfo);
        cleanup();
        return result;
    }

    private void cleanup() throws Exception {
        fileSystem.close();
    }
}
