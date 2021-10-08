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
        public String ds;
        public String test;
    }

    public int K;
    public KnnRecord testing_record;
    public KnnDataset dataset;
    public String testingFile;

    private static final String cacheFileBasename = "knnParamFile";

    public static void initialize(Job job, String basename, String testing_dataset_path) throws Exception {
        job.addCacheFile(new URI(basename + "#" + cacheFileBasename));
        job.addCacheArchive(new URI(basename + "#" + testing_dataset_path));
    }

    public KnnConfigFile() throws IOException {
        String knnParams = FileUtils.readFileToString(new File("./" + cacheFileBasename), Charset.defaultCharset());
        Gson gson = new Gson();
        ConfigData data = gson.fromJson(knnParams, ConfigData.class);
        this.K = data.k;
        String dataset_name = data.ds.toLowerCase();
        this.dataset = KnnDatasetFactory.get().getDataset(dataset_name);
        this.testing_record = this.dataset.createRecord(data.test, true);
    }
}
