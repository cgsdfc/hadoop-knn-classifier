// MIT License
// 
// Copyright (c) 2021 cgsdfc
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.utils.FsUtils;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

//Customize the Reducer type, summarize the data generated by Mapper, and export the final classification results.
//The strategy used here is the same as that of Mapper, which is to use TreeMap to save K-neighborhoods
//(K instances with the smallest distance).
//But in the end, we want to generate a classification result from the K-neighborhood,
//that is, the label with the largest proportion is used as the final classification label.
//Output Key type: In order to increase readability, we use a string to describe the output content,
//so Text is used as the type.
//Output Value type: Indicates the classification result.
public class KnnReducer extends Reducer<IntWritable, DoubleStringWritable, NullWritable, Text> {
    // Save k neighborhoods.
    private ArrayList<KSmallestMap> KnnMap = new ArrayList<KSmallestMap>();
    private KnnTestingDataset testingDataset;

    // Get the algorithm parameter k from the configuration file.
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

    private static void reduceOneTestingRecord(KSmallestMap oneKnnMap, Iterable<DoubleStringWritable> values) {
        for (DoubleStringWritable value : values) {
            oneKnnMap.put(value.getDoubleValue(), value.getStringValue());
        }
    }

    // Summarize all Values ​​under the same Key. Note that our Mapper only
    // generates a Key value, a singleton of NullWritable,
    // Therefore, all Values ​​will be aggregated together. So we only need one
    // Reducer to process all the data.
    @Override
    public void reduce(IntWritable key, Iterable<DoubleStringWritable> values, Context context)
            throws IOException, InterruptedException {
        reduceOneTestingRecord(this.KnnMap.get(key.get()), values);
    }

    private static String predicteOneTestingRecord(KSmallestMap oneKnnMap) {
        // Count the number of times for each tag.
        Map<String, Integer> freqMap = new HashMap<String, Integer>();
        for (String label : oneKnnMap.values()) {
            Integer frequency = freqMap.get(label);
            if (frequency == null) {
                freqMap.put(label, 1);
            } else {
                freqMap.put(label, frequency + 1);
            }
        }
        // Find the most frequent tags.
        String mostCommonLabel = null; // The final prediction result.
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
        String jsonString = FsUtils.toJsonString(data);
        context.write(NullWritable.get(), new Text(jsonString));
    }
}