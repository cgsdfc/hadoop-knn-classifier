package com.example;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.mapreduce.Job;

public class KnnConfigFile {

    private static class ConfigData {
        // 注意：字段顺序要和配置文件的顺序一致。并且名字也要一致。
        public int k;
        public String ds; // name of the dataset.F
    }

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
        ConfigData data = gson.fromJson(knnParams, ConfigData.class);
        this.K = data.k;
        String datasetName = data.ds.toLowerCase();
        this.dataset = KnnDatasetFactory.get().getDataset(datasetName);
    }
}
