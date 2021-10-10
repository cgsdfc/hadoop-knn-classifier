package com.example;

import com.example.EvalDatasetsGenerator.EvalDataset;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BufferedFSInputStream;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import static com.example.EvalDatasetsGenerator.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// 这个类负责输入文件的准备，输出文件的回收，作业的运行等。
public class KnnEvaluator {
    private FileSystem fileSystem;
    private Path originalDatasetPath;
    private TextLineDataset originalDataset;
    private EvalDatasetsGenerator generator;

    // 评估结果，准确率的平均值和标准差。
    public static class EvaluationResult {
        public double mean;
        public double std;
    }

    public KnnEvaluator(EvalDatasetsGenerator generator) throws Exception {
        this.generator = generator;
        this.fileSystem = FsUtils.getFileSystem();
    }

    public EvaluationResult doEvaluation() throws Exception {
        setup();
        this.generator.generate(this.originalDataset, new EvalDatasetSink() {
            @Override
            public void receive(EvalDataset dataset) {
                KnnEvaluator.this.receiveEvalDataset(dataset);
            }
        });
        EvaluationResult result = getResult();
        cleanup();
        return result;
    }

    private void setup() throws Exception {
        readOriginalDataset();
    }

    private void cleanup() throws Exception {
        fileSystem.close();
    }

    private EvaluationResult getResult() {
        return null;
    }

    // 读入原始数据集。
    private void readOriginalDataset() throws Exception {
        BufferedReader reader = FsUtils.readTextFile(this.fileSystem, this.originalDatasetPath);
        this.originalDataset = new TextLineDataset(reader);
    }

    private void receiveEvalDataset(EvalDataset dataset) {

    }

}
