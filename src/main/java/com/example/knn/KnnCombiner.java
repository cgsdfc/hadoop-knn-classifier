package com.example.knn;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class KnnCombiner extends Reducer<IntWritable, DoubleStringWritable, IntWritable, DoubleStringWritable> {
    private int K;

    // Get the algorithm parameter k from the configuration file.
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
            KnnConfigFile configFile = new KnnConfigFile();
            this.K = configFile.K;
        }
    }

    // For all candidates of a test sample, we only keep the k with the smallest
    // distance.
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
