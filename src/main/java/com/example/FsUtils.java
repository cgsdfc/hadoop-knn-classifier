package com.example;

import java.io.BufferedReader;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FsUtils {
    private static final String hdfsUri = "hdfs://hadoop100:8020";

    public static FileSystem getFileSystem() throws Exception {
        return FileSystem.get(new URI(hdfsUri), new Configuration());
    }

    public static BufferedReader readTextFile(FileSystem fs, Path path) throws Exception {
        FSDataInputStream stream = fs.open(path);
        return new BufferedReader(new InputStreamReader(stream));
    }
}
