package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

// 自定义 Mapper 类型。
// 输入 Key 类型为 Object，实际上是一行文本的偏移量，但是我们不需要这个数据，所以设为 Object。
// 输入 Value 类型为 Text，即一行文本数据，这是我们关心的数据。
// 输出 Key 类型为 NullWritable，表示实际上我们没有输出 Key 数据。
// 输出 Value 类型为 DoubleString，这是我们自定义的数据类型，表示计算出来的距离和相应的标签。
public class KnnMapper extends Mapper<Object, Text, IntWritable, MapWritable> {
    // 始终保存不多于K个键值对，用来对计算出的距离进行排序。
    private ArrayList<KSmallestMap> KnnMap = new ArrayList<KSmallestMap>();

    private KnnConfigFile configFile;

    private KnnTestingDataset testingDataset;

    // 重写 Mapper 的setup方法，初始化本对象的一些数据。
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // 获取配置文件。
        if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
            this.configFile = new KnnConfigFile();
            this.testingDataset = new KnnTestingDataset(configFile);
            for (int i = 0; i < testingDataset.size(); ++i) {
                this.KnnMap.add(new KSmallestMap(configFile.K));
            }
        }
    }

    // 重写map方法，实现对训练数据的分布式处理。
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        KnnRecord trainingRecord = configFile.dataset.createRecord(value.toString());
        // 计算每个测试实例和训练实例的距离。
        for (int i = 0; i < testingDataset.size(); ++i) {
            // 计算训练实例和测试实例的距离。
            double tDist = KnnRecord.computeDistance(testingDataset.get(i), trainingRecord);
            // 更新距离最小的不超过K个实例的记录。
            KnnMap.get(i).put(tDist, trainingRecord.getLabel());
        }
    }

    // 在map调用结束后，会调用cleanup方法，我们在这里把保存在knnMap中的数据写入Context中。
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // 遍历knnMap，把数据导出到context。
        for (int i = 0; i < testingDataset.size(); ++i) {
            // 导出每个测试实例的K邻域。
            MapWritable map = new MapWritable();
            for (Map.Entry<Double, String> entry : KnnMap.get(i).entrySet()) {
                Double knnDist = entry.getKey();
                String knnLabel = entry.getValue();
                map.put(new DoubleWritable(knnDist), new Text(knnLabel));
            }
            // 每个测试实例的id是i，对应的K邻域是map。
            context.write(new IntWritable(i), map);
        }
    }
}