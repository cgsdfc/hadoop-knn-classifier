package com.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

// 这个类负责配置并且运行一个KNN分类器的任务，它是原来的
// KnnClassifier 的main逻辑的封装。
public class KnnClassifier {

    private String configFile;
    private String testingFile;
    private String trainingFile;
    private String outputDir;
    private int id;

    private static final int numArgs = 4;
    private static final int trainingFileIndex = 0;
    private static final int outputDirIndex = 1;
    private static final int configFileIndex = 2;
    private static final int testingFileIndex = 3;

    public KnnClassifier(String configFile, String testingFile, //
            String trainingFile, String outputDir, int id) {
        this.configFile = configFile;
        this.testingFile = testingFile;
        this.trainingFile = trainingFile;
        this.outputDir = outputDir;
        this.id = id;
    }

    public static void main(String[] args) throws Exception {
        // 命令行参数有误。
        if (args.length != numArgs) {
            System.err.println("Usage: KnnClassifier <trainingFile> <outputDir> <configFile> <testingFile>");
            System.exit(2);
        }
        KnnClassifier job = new KnnClassifier(args[configFileIndex], args[testingFileIndex], //
                args[trainingFileIndex], args[outputDirIndex], 0);
        job.run();
    }

    // 主函数。调用 MapReduce 的 Job API 来配置本次运行的相关设定，并且提交任务。
    public void run() throws Exception {
        // 创建配置对象。
        Configuration conf = new Configuration();

        // 创建 Job 对象。
        Job job = Job.getInstance(conf, "KNN-" + Integer.toString(this.id));
        // 设置要运行的Jar包，即KnnClassifier类所在的Jar包。
        job.setJarByClass(KnnClassifier.class);

        // 把配置文件设定为 CacheFile，则后续各台服务器均可访问它的副本，从而减少小文件的传输开销。
        KnnConfigFile.initialize(job, this.configFile);
        KnnTestingDataset.initialize(job, this.testingFile);

        // 设置 MapReduce 任务的自定义类型。
        job.setMapperClass(KnnMapper.class);
        job.setReducerClass(KnnReducer.class);
        job.setNumReduceTasks(1); // 本项目只需要一个 Reducer 任务。

        // 设置输出的键值类型。
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(MapWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        // 设置输入文件（训练数据集）的路径和输出目录的路径。
        // 分类结果将作为一个文件保存在输出目录下。
        FileInputFormat.addInputPath(job, new Path(this.trainingFile));
        FileOutputFormat.setOutputPath(job, new Path(this.outputDir));

        // 等待作业执行完成并返回状态码。
        if (!job.waitForCompletion(true)) {
            throw new Exception("Job failed");
        }

    }
}
