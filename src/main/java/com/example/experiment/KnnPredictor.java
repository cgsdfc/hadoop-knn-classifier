package com.example.experiment;

import com.example.knn.KnnClassifier;
import com.example.knn.KnnConfigData;
import com.example.knn.ResultJsonData;
import com.example.utils.FsUtils;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

//This is a wrapper around KnnClassifier.
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

        // Run the knn classifier task.
        KnnClassifier job = new KnnClassifier(//
                configFilePath.toString(), //
                testingDatasetPath.toString(), //
                trainingDatasetPath.toString(), outputDir.toString(), 0);

        job.runWithRetry();

        // Get the result file.
        Path resultFile = null;
        for (FileStatus f : fileSystem.listStatus(outputDir)) {
            if (f.getLen() > 0) {
                resultFile = f.getPath();
                break;
            }
        }

        // Get the accuracy data from the result file.
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
