package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;

// 自定义 Reducer 类型，把 Mapper 产生的数据进行汇总，导出最后的分类结果。
// 这里用的策略和 Mapper 是一样的，就是用 TreeMap 保存K-邻域（K个距离最小的实例）。
// 但是在最后，我们要从K-邻域中产生一个分类结果，那就是要把占比最大的标签作为最终的分类标签。
// 输出 Key 类型：为了增加可读性，我们用一个字符串来说明输出的内容，所以用Text作为类型。
// 输出 Value 类型：表示的是分类的结果。
public class KnnReducer extends Reducer<IntWritable, MapWritable, NullWritable, Text> {
    // 保存K-邻域。
    private ArrayList<KSmallestMap> KnnMap = new ArrayList<KSmallestMap>();
    private KnnTestingDataset testingDataset;

    private static class ResultJsonData {
        public double accuracy = 0;
        public ArrayList<String> predictions = new ArrayList<String>();
    }

    // 从配置文件获取算法参数K。
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
            KnnConfigFile configFile = new KnnConfigFile();
            this.testingDataset = new KnnTestingDataset(configFile);
            for (int i = 0; i < testingDataset.size(); ++i) {
                KnnMap.add(new KSmallestMap(configFile.K));
            }
        }
    }

    private static void reduceOneTestingRecord(KSmallestMap oneKnnMap, Iterable<MapWritable> values) {
        for (MapWritable oneMap : values) {
            for (Map.Entry<Writable, Writable> distanceAndLabel : oneMap.entrySet()) {
                DoubleWritable dist = (DoubleWritable) distanceAndLabel.getKey();
                Text label = (Text) distanceAndLabel.getValue();
                oneKnnMap.put(dist.get(), label.toString());
            }
        }
    }

    // 对同一个Key下的所有Value进行汇总。注意，我们的Mapper只产生了一个Key值，即NullWritable的单例，
    // 所以，所有的Value都会被汇总到一起。所以我们只需要一个Reducer即可处理全部数据。
    @Override
    public void reduce(IntWritable key, Iterable<MapWritable> values, Context context)
            throws IOException, InterruptedException {
        reduceOneTestingRecord(this.KnnMap.get(key.get()), values);
    }

    private static String predicteOneTestingRecord(KSmallestMap oneKnnMap) {
        // 统计每个标签的次数。
        Map<String, Integer> freqMap = new HashMap<String, Integer>();
        for (String label : oneKnnMap.values()) {
            Integer frequency = freqMap.get(label);
            if (frequency == null) {
                freqMap.put(label, 1);
            } else {
                freqMap.put(label, frequency + 1);
            }
        }
        // 找出频次最大的标签。
        String mostCommonLabel = null; // 最终预测结果。
        int maxFrequency = -1;
        for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                mostCommonLabel = entry.getKey();
                maxFrequency = entry.getValue();
            }
        }
        return mostCommonLabel;
    }

    private ResultJsonData generateResult() {
        ResultJsonData data = new ResultJsonData();
        for (int i = 0; i < testingDataset.size(); ++i) {
            String label = predicteOneTestingRecord(KnnMap.get(i));
            data.predictions.add(label);
            String groundTruth = testingDataset.get(i).getLabel();
            data.accuracy += (label.equals(groundTruth) ? 1 : 0);
        }
        data.accuracy /= testingDataset.size();
        return data;
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        ResultJsonData data = generateResult();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(data);
        context.write(NullWritable.get(), new Text(jsonString));
    }
}