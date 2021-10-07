package com.example;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// 自定义 Mapper 类型。
// 输入 Key 类型为 Object，实际上是一行文本的偏移量，但是我们不需要这个数据，所以设为 Object。
// 输入 Value 类型为 Text，即一行文本数据，这是我们关心的数据。
// 输出 Key 类型为 NullWritable，表示实际上我们没有输出 Key 数据。
// 输出 Value 类型为 DoubleString，这是我们自定义的数据类型，表示计算出来的距离和相应的标签。
public class KnnMapper extends Mapper<Object, Text, NullWritable, DoubleString> {
    // 保存最终计算结果。
    DoubleString distanceAndModel = new DoubleString();
    // 始终保存不多于K个键值对，用来对计算出的距离进行排序。
    TreeMap<Double, String> KnnMap = new TreeMap<Double, String>();

    KnnConfigFile configureFile;

    // 重写 Mapper 的setup方法，初始化本对象的一些数据。
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // 获取配置文件。
        if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
            this.configureFile = new KnnConfigFile();
        }
    }

    // 重写map方法，实现对训练数据的分布式处理。
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        // 对一行csv数据进行解析。训练数据记为r，测试数据记为t，作为变量的前缀以示区分。
        CarOwnerRecord training_record = new CarOwnerRecord(value.toString());

        // 计算训练实例和测试实例的距离。
        double tDist = CarOwnerRecord.computeDistance(training_record, configureFile.testing_record);

        // 更新距离最小的不超过K个实例的记录。
        KnnMap.put(tDist, training_record.model);
        // 最多保存K个记录。
        if (KnnMap.size() > configureFile.K) {
            KnnMap.remove(KnnMap.lastKey());
        }
    }

    // 在map调用结束后，会调用cleanup方法，我们在这里把保存在knnMap中的数据写入Context中。
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // 遍历knnMap，把数据导出到context。
        for (Map.Entry<Double, String> entry : KnnMap.entrySet()) {
            Double knnDist = entry.getKey();
            String knnModel = entry.getValue();
            // 因为是把数据交给hadoop，必须要能够序列化，所以必须使用DoubleString类型。
            distanceAndModel.set(knnDist, knnModel);
            // key为空，所以NullWritable是一个占位符。
            context.write(NullWritable.get(), distanceAndModel);
        }
    }
}