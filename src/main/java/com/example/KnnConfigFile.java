package com.example;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.mapreduce.Job;

public class KnnConfigFile {

    public int K;
    public CarOwnerRecord testing_record;
    private static final String cacheFileBasename = "knnParamFile";

    public static void initialize(Job job, String basename) throws Exception {
        job.addCacheFile(new URI(basename + "#" + cacheFileBasename));
    }

    public KnnConfigFile() throws IOException {
        String knnParams = FileUtils.readFileToString(new File("./" + cacheFileBasename), Charset.defaultCharset());
        StringTokenizer st = new StringTokenizer(knnParams, ",");

        // 获取参数K和测试实例的字段。
        this.K = Integer.parseInt(st.nextToken());
        this.testing_record = new CarOwnerRecord(st, true);
    }

}
