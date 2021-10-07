package com.example;

import com.example.CarOwnerRecord;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.mapreduce.Job;

public class ConfigureFile {

    public int K;
    public CarOwnerRecord record;

    public static void initialize(Job job, String basename) throws Exception {
        job.addCacheFile(new URI(basename + "#knnParamFile"));
    }

    public ConfigureFile() throws Exception {
        String knnParams = FileUtils.readFileToString(new File("./knnParamFile"), Charset.defaultCharset());
        StringTokenizer st = new StringTokenizer(knnParams, ",");

        // 获取参数K和测试实例的字段。
        this.K = Integer.parseInt(st.nextToken());
        this.record = new CarOwnerRecord(st);
    }

}
