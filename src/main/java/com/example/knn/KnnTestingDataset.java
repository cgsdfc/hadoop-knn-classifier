package com.example.knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.example.dataset.KnnRecord;

import java.net.URI;

import org.apache.hadoop.mapreduce.Job;

public class KnnTestingDataset {

    private ArrayList<KnnRecord> records = new ArrayList<>();

    // 用这个符号链接来访问文件，就不需要知道原来的文件名是什么了。
    private static final String symlink = "knnTesingFile";

    public static void initialize(Job job, String testingFilename) throws Exception {
        job.addCacheFile(new URI(testingFilename + "#" + symlink));
    }

    public int size() {
        return records.size();
    }

    public KnnRecord get(int i) {
        return records.get(i);
    }

    public KnnTestingDataset(KnnConfigFile configFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("./" + symlink)));
        String line;
        while ((line = reader.readLine()) != null) {
            KnnRecord r = configFile.dataset.createRecord(line);
            records.add(r);
        }
    }
}
