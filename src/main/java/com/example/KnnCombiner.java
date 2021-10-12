package com.example;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class KnnCombiner extends Reducer<IntWritable, DoubleStringWritable, IntWritable, DoubleStringWritable> {
    private int K;

    // 从配置文件获取算法参数K。
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
            KnnConfigFile configFile = new KnnConfigFile();
            this.K = configFile.K;
        }
    }

    // 对于某个测试样本的所有候选，我们只保留距离最小的K个即可。
    @Override
    public void reduce(IntWritable key, Iterable<DoubleStringWritable> values, Context context)
            throws IOException, InterruptedException {
        KSmallestMap knnMap = new KSmallestMap(this.K);
        for (DoubleStringWritable value : values) {
            knnMap.put(value.getDoubleValue(), value.getStringValue());
        }
        for (Map.Entry<Double, String> entry : knnMap.entrySet()) {
            context.write(key, new DoubleStringWritable(entry.getKey(), entry.getValue()));
        }
    }
}
