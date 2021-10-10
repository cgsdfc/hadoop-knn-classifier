package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class FsUtils {
    private static final String hdfsUri = "hdfs://hadoop100:8020";

    public static FileSystem getFileSystem() throws Exception {
        return FileSystem.get(new URI(hdfsUri), new Configuration(), "cong");
    }

    public static BufferedReader openTextFile(FileSystem fs, Path path) throws Exception {
        FSDataInputStream stream = fs.open(path);
        return new BufferedReader(new InputStreamReader(stream));
    }

    public static void writeLines(FileSystem fs, Path outPath, Iterable<String> lines) throws Exception {
        FSDataOutputStream stream = fs.create(outPath);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }

    public static void write(FileSystem fs, Path outPath, String string) throws Exception {
        Writer writer = new BufferedWriter(new OutputStreamWriter(fs.create(outPath)));
        writer.write(string);
        writer.close();
    }

    public static void remove(FileSystem fs, Path path) throws Exception {
        fs.delete(path, true);
    }
}
