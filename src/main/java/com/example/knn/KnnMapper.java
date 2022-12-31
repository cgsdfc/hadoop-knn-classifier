package com.example.knn;

import java.io.IOException;

import com.example.dataset.KnnRecord;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//Custom Mapper types.
//The input Key type is Object, which is actually the offset of a line of text, but we don’t need this data, so set it to Object.
//The input Value type is Text, that is, a line of text data, which is the data we care about.
//The output Key type is NullWritable, which means that we do not actually output Key data.
//The output Value type is DoubleString, which is our custom data type, representing the calculated distance and corresponding label.
public class KnnMapper extends Mapper<Object, Text, IntWritable, DoubleStringWritable> {

    private KnnConfigFile configFile;

    private KnnTestingDataset testingDataset;

    // Override the setup method of Mapper to initialize some data of this object.
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        // Get configuration files.
        if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
            this.configFile = new KnnConfigFile();
            this.testingDataset = new KnnTestingDataset(configFile);
        }
    }

    // Rewrite the map method to implement distributed processing of training data.
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        KnnRecord trainingRecord = configFile.dataset.createRecord(value.toString());
        // Calculate the distance between each test instance and training instance.
        for (int i = 0; i < testingDataset.size(); ++i) {
            // Calculate the distance between the training instance and the test instance.
            double distance = configFile.dataset.distance(testingDataset.get(i), trainingRecord);
            context.write(new IntWritable(i), new DoubleStringWritable(distance, trainingRecord.getLabel()));
        }
    }

}