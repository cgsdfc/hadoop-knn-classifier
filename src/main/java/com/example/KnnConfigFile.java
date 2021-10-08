package com.example;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.mapreduce.Job;

public class KnnConfigFile {

    private static class ConfigData {
        public String dataset_name;
        public int K;
        public String testing_record;
    }

    public int K;
    public KnnRecord testing_record;
    public KnnDataset dataset;

    private static final String cacheFileBasename = "knnParamFile";

    public static void initialize(Job job, String basename) throws Exception {
        job.addCacheFile(new URI(basename + "#" + cacheFileBasename));
    }

    public KnnConfigFile() throws IOException {
        String knnParams = FileUtils.readFileToString(new File("./" + cacheFileBasename), Charset.defaultCharset());
        Gson gson = new Gson();
        ConfigData data = gson.fromJson(knnParams, ConfigData.class);
        this.K = data.K;
        String dataset_name = data.dataset_name.toLowerCase();
        this.dataset = KnnDatasetFactory.get().getDataset(dataset_name);
        this.testing_record = this.dataset.createRecord(data.testing_record, true);
    }
}
