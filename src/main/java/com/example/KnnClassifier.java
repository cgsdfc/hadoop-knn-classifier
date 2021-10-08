package com.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

// 驱动类，包裹 Mapper 和 Reducer 类，并实现 main 函数。
public class KnnClassifier {

    public static void registerKnownDatasets() {
        KnnDatasetFactory factory = KnnDatasetFactory.get();
        factory.registerDataset("iris", new IrisDataset());
        factory.registerDataset("car_owners", new CarOwnersDataset());
    }

    // 主函数。调用 MapReduce 的 Job API 来配置本次运行的相关设定，并且提交任务。
    public static void main(String[] args) throws Exception {
        registerKnownDatasets();
        
        // 创建配置对象。
        Configuration conf = new Configuration();

        // 命令行参数有误。
        if (args.length != 3) {
            System.err.println("Usage: KnnClassifier <in> <out> <parameter file>");
            System.exit(2);
        }

        // 创建 Job 对象。
        Job job = Job.getInstance(conf, "Find K-Nearest Neighbour");
        // 设置要运行的Jar包，即KnnClassifier类所在的Jar包。
        job.setJarByClass(KnnClassifier.class);
        // 把配置文件设定为 CacheFile，则后续各台服务器均可访问它的副本，从而减少小文件的传输开销。
        KnnConfigFile.initialize(job, args[2]);

        // 设置 MapReduce 任务的自定义类型。
        job.setMapperClass(KnnMapper.class);
        job.setReducerClass(KnnReducer.class);
        job.setNumReduceTasks(1); // 本项目只需要一个 Reducer 任务。

        // 设置输出的键值类型。
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(DoubleString.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        // 设置输入文件（训练数据集）的路径和输出目录的路径。
        // 分类结果将作为一个文件保存在输出目录下。
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 等待作业执行完成并返回状态码。
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
