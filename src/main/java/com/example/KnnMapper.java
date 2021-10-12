package com.example;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// 自定义 Mapper 类型。
// 输入 Key 类型为 Object，实际上是一行文本的偏移量，但是我们不需要这个数据，所以设为 Object。
// 输入 Value 类型为 Text，即一行文本数据，这是我们关心的数据。
// 输出 Key 类型为 NullWritable，表示实际上我们没有输出 Key 数据。
// 输出 Value 类型为 DoubleString，这是我们自定义的数据类型，表示计算出来的距离和相应的标签。
public class KnnMapper extends Mapper<Object, Text, IntWritable, DoubleStringWritable> {

    private KnnConfigFile configFile;

    private KnnTestingDataset testingDataset;

    // 重写 Mapper 的setup方法，初始化本对象的一些数据。
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // 获取配置文件。
        if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
            this.configFile = new KnnConfigFile();
            this.testingDataset = new KnnTestingDataset(configFile);
        }
    }

    // 重写map方法，实现对训练数据的分布式处理。
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        KnnRecord trainingRecord = configFile.dataset.createRecord(value.toString());
        // 计算每个测试实例和训练实例的距离。
        for (int i = 0; i < testingDataset.size(); ++i) {
            // 计算训练实例和测试实例的距离。
            double distance = KnnRecord.computeDistance(testingDataset.get(i), trainingRecord);
            context.write(new IntWritable(i), new DoubleStringWritable(distance, trainingRecord.getLabel()));
        }
    }

}