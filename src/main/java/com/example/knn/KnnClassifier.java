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

import com.example.utils.FsUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

//This class is responsible for configuring and running a KNN classifier task, which is the original
//Encapsulation of the main logic of KnnClassifier.
public class KnnClassifier {

    private String configFile;
    private String testingFile;
    private String trainingFile;
    private String outputDir;
    private FileSystem fs; // Used to delete the output directory.
    private int id;

    private static final int numArgs = 4;
    private static final int trainingFileIndex = 0;
    private static final int outputDirIndex = 1;
    private static final int configFileIndex = 2;
    private static final int testingFileIndex = 3;

    private static final int defaultMaxRetry = 3;
    private static final int defaultSleepSeconds = 2;

    public KnnClassifier(String configFile, String testingFile, //
            String trainingFile, String outputDir, int id) throws Exception {
        this.configFile = configFile;
        this.testingFile = testingFile;
        this.trainingFile = trainingFile;
        this.outputDir = outputDir;
        this.id = id;
        this.fs = FsUtils.getFileSystem();
    }

    public static void main(String[] args) throws Exception {
        // The command line parameter is wrong.
        if (args.length != numArgs) {
            System.err.println("Usage: KnnClassifier <trainingFile> <outputDir> <configFile> <testingFile>");
            System.exit(2);
        }
        KnnClassifier job = new KnnClassifier(args[configFileIndex], args[testingFileIndex], //
                args[trainingFileIndex], args[outputDirIndex], 0);
        job.run();
    }

    public String getJobName() {
        return "KNN-" + Integer.toString(this.id);
    }

    public void runWithRetry() throws Exception {
        runWithRetry(defaultMaxRetry, defaultSleepSeconds);
    }

    public void runWithRetry(int maxRetry, int sleepSeconds) throws Exception {
        int count = 0;
        while (true) {
            try {
                run();
                return;
            } catch (Exception e) {
                if (count > maxRetry) {
                    throw new Exception(String.format("Job %s failed with %d retries\n", getJobName(), maxRetry), e);
                }
                ++count;
                Thread.sleep(sleepSeconds * 1000);
            }
        }
    }

    // main function. Call the Job API of MapReduce to
    // configure the relevant settings of this run and submit the task.
    public void run() throws Exception {
        FsUtils.remove(fs, new Path(this.outputDir));

        // Create a configuration object.
        Configuration conf = new Configuration();

        // Create a Job object.
        Job job = Job.getInstance(conf, getJobName());
        // Set the jar package to be run, that is, the jar package where the knn
        // classifier class is located.
        job.setJarByClass(KnnClassifier.class);

        // If the configuration file is set to CacheFile, each subsequent server can
        // access its copy, thereby reducing the transmission overhead of small files.
        KnnConfigFile.initialize(job, this.configFile);
        KnnTestingDataset.initialize(job, this.testingFile);

        // Set a custom type of MapReduce job.
        job.setMapperClass(KnnMapper.class);
        job.setReducerClass(KnnReducer.class);
        job.setCombinerClass(KnnCombiner.class);
        job.setNumReduceTasks(1); // This project requires only one Reducer task.

        // Sets the output key-value type.
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(DoubleStringWritable.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        // Set the path to the input file (training dataset) and the path to the output
        // directory.
        // The classification result will be saved as a file in the output directory.
        FileInputFormat.addInputPath(job, new Path(this.trainingFile));
        FileOutputFormat.setOutputPath(job, new Path(this.outputDir));

        // Wait for the job execution to complete and return a status code.
        boolean ok = job.waitForCompletion(true);
        if (!ok) {
            throw new Exception(String.format("Job %s failed", getJobName()));
        }
    }
}
