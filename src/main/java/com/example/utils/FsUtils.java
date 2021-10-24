package com.example.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public static void writeInJsonFormatHDFS(FileSystem fs, Path outPath, Object object) throws Exception {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fs.create(outPath)));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(object, writer);
        writer.close();
    }

    public static void writeInJsonFormatLocal(String outPath, Object object) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outPath)));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(object, writer);
        writer.close();
    }

    public static <T> T readFromJsonFormatLocal(String path, Class<T> cls) throws Exception {
        Gson gson = new Gson();
        Reader reader = new BufferedReader(new FileReader(path));
        return gson.fromJson(reader, cls);
    }

    public static <T> T readFromJsonFormatHDFS(FileSystem fs, String path, Class<T> cls) throws Exception {
        Gson gson = new Gson();
        return gson.fromJson(openTextFile(fs, new Path(path)), cls);
    }

    public static String toJsonString(Object object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(object);
    }
}
