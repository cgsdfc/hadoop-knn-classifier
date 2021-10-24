package com.example.knn;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import com.example.dataset.KnnDataset;
import com.example.dataset.KnnDatasetFactory;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.mapreduce.Job;

public class KnnConfigFile {

    public int K;
    public KnnDataset dataset;
    public String testingFile;

    private static final String symlink = "knnParamsFile";

    public static void initialize(Job job, String configFilename) throws Exception {
        job.addCacheFile(new URI(configFilename + "#" + symlink));
    }

    public KnnConfigFile() throws IOException {
        String knnParams = FileUtils.readFileToString(new File("./" + symlink), //
                Charset.defaultCharset());
        Gson gson = new Gson();
        KnnConfigData data = gson.fromJson(knnParams, KnnConfigData.class);
        this.K = data.k;
        String datasetName = data.ds.toLowerCase();
        this.dataset = KnnDatasetFactory.get().getDataset(datasetName);
    }
}
