package com.example;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

// 自定义 Reducer 类型，把 Mapper 产生的数据进行汇总，导出最后的分类结果。
// 这里用的策略和 Mapper 是一样的，就是用 TreeMap 保存K-邻域（K个距离最小的实例）。
// 但是在最后，我们要从K-邻域中产生一个分类结果，那就是要把占比最大的标签作为最终的分类标签。
// 输出 Key 类型：为了增加可读性，我们用一个字符串来说明输出的内容，所以用Text作为类型。
// 输出 Value 类型：表示的是分类的结果。
public class KnnReducer extends Reducer<NullWritable, DoubleString, Text, Text> {
    // 保存K-邻域。
    private KSmallestMap KnnMap;

    // 从配置文件获取算法参数K。
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
            KnnConfigFile configFile = new KnnConfigFile();
            this.KnnMap = new KSmallestMap(configFile.K);
        }
    }

    // 对同一个Key下的所有Value进行汇总。注意，我们的Mapper只产生了一个Key值，即NullWritable的单例，
    // 所以，所有的Value都会被汇总到一起。所以我们只需要一个Reducer即可处理全部数据。
    @Override
    public void reduce(NullWritable key, Iterable<DoubleString> values, Context context)
            throws IOException, InterruptedException {
        // 把所有距离-标签数据放入TreeMap中进行排序。
        for (DoubleString val : values) {
            String rModel = val.getModel();
            double tDist = val.getDistance();
            KnnMap.put(tDist, rModel);
        }

        // 完成N-领域的构建后，我们要找出出现次数最多的那个标签，作为我们最终预测结果。

        List<String> knnList = new ArrayList<String>(KnnMap.values());
        Map<String, Integer> freqMap = new HashMap<String, Integer>();

        // 统计每个标签（车辆型号）的频次。
        for (int i = 0; i < knnList.size(); i++) {
            Integer frequency = freqMap.get(knnList.get(i));
            if (frequency == null) {
                freqMap.put(knnList.get(i), 1);
            } else {
                freqMap.put(knnList.get(i), frequency + 1);
            }
        }
        // 找出频次最大的标签。
        String mostCommonModel = null; // 最终预测结果。
        int maxFrequency = -1;
        for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                mostCommonModel = entry.getKey();
                maxFrequency = entry.getValue();
            }
        }
        // 输出分类结果和K-邻域。
        context.write(new Text("Result: "), new Text(mostCommonModel));
        context.write(new Text("K-Nearest-Neighbours:\n"), new Text(KnnMap.toString()));
    }
}