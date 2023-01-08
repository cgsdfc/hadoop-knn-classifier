// MIT License
// 
// Copyright (c) 2021 Cong Feng
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

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
