package com.example;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

// 驱动类，包裹 Mapper 和 Reducer 类，并实现 main 函数。
public class KnnPattern {

    // 自定义的可序列化类型，保存了测试实例到一个训练实例的距离以及该训练实例的标签。
    public static class DoubleString implements WritableComparable<DoubleString> {
        // 测试实例（唯一）到某个训练实例的距离。
        private Double distance = 0.0;
        // 该训练实例的标签。
        private String model = null;

        public void set(Double lhs, String rhs) {
            distance = lhs;
            model = rhs;
        }

        public Double getDistance() {
            return distance;
        }

        public String getModel() {
            return model;
        }

        // 实现序列化的读操作。
        @Override
        public void readFields(DataInput in) throws IOException {
            distance = in.readDouble();
            model = in.readUTF();
        }

        // 实现序列化的写操作。
        @Override
        public void write(DataOutput out) throws IOException {
            out.writeDouble(distance);
            out.writeUTF(model);
        }

        // 实现比较操作。
        @Override
        public int compareTo(DoubleString o) {
            return (this.model).compareTo(o.model);
        }
    }

    // 自定义 Mapper 类型。
    // 输入 Key 类型为 Object，实际上是一行文本的偏移量，但是我们不需要这个数据，所以设为 Object。
    // 输入 Value 类型为 Text，即一行文本数据，这是我们关心的数据。
    // 输出 Key 类型为 NullWritable，表示实际上我们没有输出 Key 数据。
    // 输出 Value 类型为 DoubleString，这是我们自定义的数据类型，表示计算出来的距离和相应的标签。
    public static class KnnMapper extends Mapper<Object, Text, NullWritable, DoubleString> {
        // 保存最终计算结果。
        DoubleString distanceAndModel = new DoubleString();
        // 始终保存不多于K个键值对，用来对计算出的距离进行排序。
        TreeMap<Double, String> KnnMap = new TreeMap<Double, String>();

        // 算法参数K。
        int K;

        double normalisedSAge;
        double normalisedSIncome;
        String sStatus;
        String sGender;
        double normalisedSChildren;

        // The known ranges of the dataset, which can be hardcoded in for the purposes
        // of this example
        double minAge = 18;
        double maxAge = 77;
        double minIncome = 5000;
        double maxIncome = 67789;
        double minChildren = 0;
        double maxChildren = 5;

        // Takes a string and two double values. Converts string to a double and
        // normalises it to
        // a value in the range supplied to reurn a double between 0.0 and 1.0
        private double normalisedDouble(String n1, double minValue, double maxValue) {
            return (Double.parseDouble(n1) - minValue) / (maxValue - minValue);
        }

        // Takes two strings and simply compares then to return a double of 0.0
        // (non-identical) or 1.0 (identical).
        // This provides a way of evaluating a numerical distance between two nominal
        // values.
        private double nominalDistance(String t1, String t2) {
            if (t1.equals(t2)) {
                return 0;
            } else {
                return 1;
            }
        }

        // Takes a double and returns its squared value.
        private double squaredDistance(double n1) {
            return Math.pow(n1, 2);
        }

        // Takes ten pairs of values (three pairs of doubles and two of strings), finds
        // the difference between the members
        // of each pair (using nominalDistance() for strings) and returns the sum of the
        // squared differences as a double.
        private double totalSquaredDistance(double R1, double R2, String R3, String R4, double R5, double S1, double S2,
                String S3, String S4, double S5) {
            double ageDifference = S1 - R1;
            double incomeDifference = S2 - R2;
            double statusDifference = nominalDistance(S3, R3);
            double genderDifference = nominalDistance(S4, R4);
            double childrenDifference = S5 - R5;

            // The sum of squared distances is used rather than the euclidean distance
            // because taking the square root would not change the order.
            // Status and gender are not squared because they are always 0 or 1.
            return squaredDistance(ageDifference) + squaredDistance(incomeDifference) + statusDifference
                    + genderDifference + squaredDistance(childrenDifference);
        }

        // The @Override annotation causes the compiler to check if a method is actually
        // being overridden
        // (a warning would be produced in case of a typo or incorrectly matched
        // parameters)
        @Override
        // The setup() method is run once at the start of the mapper and is supplied
        // with MapReduce's
        // context object
        protected void setup(Context context) throws IOException, InterruptedException {
            if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
                // Read parameter file using alias established in main()
                String knnParams = FileUtils.readFileToString(new File("./knnParamFile"));
                StringTokenizer st = new StringTokenizer(knnParams, ",");

                // Using the variables declared earlier, values are assigned to K and to the
                // test dataset, S.
                // These values will remain unchanged throughout the mapper
                K = Integer.parseInt(st.nextToken());
                normalisedSAge = normalisedDouble(st.nextToken(), minAge, maxAge);
                normalisedSIncome = normalisedDouble(st.nextToken(), minIncome, maxIncome);
                sStatus = st.nextToken();
                sGender = st.nextToken();
                normalisedSChildren = normalisedDouble(st.nextToken(), minChildren, maxChildren);
            }
        }

        @Override
        // The map() method is run by MapReduce once for each row supplied as the input
        // data
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // Tokenize the input line (presented as 'value' by MapReduce) from the csv file
            // This is the training dataset, R
            String rLine = value.toString();
            StringTokenizer st = new StringTokenizer(rLine, ",");

            double normalisedRAge = normalisedDouble(st.nextToken(), minAge, maxAge);
            double normalisedRIncome = normalisedDouble(st.nextToken(), minIncome, maxIncome);
            String rStatus = st.nextToken();
            String rGender = st.nextToken();
            double normalisedRChildren = normalisedDouble(st.nextToken(), minChildren, maxChildren);
            String rModel = st.nextToken();

            // Using these row specific values and the unchanging S dataset values,
            // calculate a total squared
            // distance between each pair of corresponding values.
            double tDist = totalSquaredDistance(normalisedRAge, normalisedRIncome, rStatus, rGender,
                    normalisedRChildren, normalisedSAge, normalisedSIncome, sStatus, sGender, normalisedSChildren);

            // Add the total distance and corresponding car model for this row into the
            // TreeMap with distance
            // as key and model as value.
            KnnMap.put(tDist, rModel);
            // Only K distances are required, so if the TreeMap contains over K entries,
            // remove the last one
            // which will be the highest distance number.
            if (KnnMap.size() > K) {
                KnnMap.remove(KnnMap.lastKey());
            }
        }

        @Override
        // The cleanup() method is run once after map() has run for every row
        protected void cleanup(Context context) throws IOException, InterruptedException {
            // Loop through the K key:values in the TreeMap
            for (Map.Entry<Double, String> entry : KnnMap.entrySet()) {
                Double knnDist = entry.getKey();
                String knnModel = entry.getValue();
                // distanceAndModel is the instance of DoubleString declared aerlier
                distanceAndModel.set(knnDist, knnModel);
                // Write to context a NullWritable as key and distanceAndModel as value
                context.write(NullWritable.get(), distanceAndModel);
            }
        }
    }

    // The reducer class accepts the NullWritable and DoubleString objects just
    // supplied to context and
    // outputs a NullWritable and a Text object for the final classification.
    public static class KnnReducer extends Reducer<NullWritable, DoubleString, NullWritable, Text> {
        TreeMap<Double, String> KnnMap = new TreeMap<Double, String>();
        int K;

        @Override
        // setup() again is run before the main reduce() method
        protected void setup(Context context) throws IOException, InterruptedException {
            if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
                // Read parameter file using alias established in main()
                String knnParams = FileUtils.readFileToString(new File("./knnParamFile"));
                StringTokenizer st = new StringTokenizer(knnParams, ",");
                // Only K is needed from the parameter file by the reducer
                K = Integer.parseInt(st.nextToken());
            }
        }

        @Override
        // The reduce() method accepts the objects the mapper wrote to context: a
        // NullWritable and a DoubleString
        public void reduce(NullWritable key, Iterable<DoubleString> values, Context context)
                throws IOException, InterruptedException {
            // values are the K DoubleString objects which the mapper wrote to context
            // Loop through these
            for (DoubleString val : values) {
                String rModel = val.getModel();
                double tDist = val.getDistance();

                // Populate another TreeMap with the distance and model information extracted
                // from the
                // DoubleString objects and trim it to size K as before.
                KnnMap.put(tDist, rModel);
                if (KnnMap.size() > K) {
                    KnnMap.remove(KnnMap.lastKey());
                }
            }

            // This section determines which of the K values (models) in the TreeMap occurs
            // most frequently
            // by means of constructing an intermediate ArrayList and HashMap.

            // A List of all the values in the TreeMap.
            List<String> knnList = new ArrayList<String>(KnnMap.values());

            Map<String, Integer> freqMap = new HashMap<String, Integer>();

            // Add the members of the list to the HashMap as keys and the number of times
            // each occurs
            // (frequency) as values
            for (int i = 0; i < knnList.size(); i++) {
                Integer frequency = freqMap.get(knnList.get(i));
                if (frequency == null) {
                    freqMap.put(knnList.get(i), 1);
                } else {
                    freqMap.put(knnList.get(i), frequency + 1);
                }
            }

            // Examine the HashMap to determine which key (model) has the highest value
            // (frequency)
            String mostCommonModel = null;
            int maxFrequency = -1;
            for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
                if (entry.getValue() > maxFrequency) {
                    mostCommonModel = entry.getKey();
                    maxFrequency = entry.getValue();
                }
            }

            // Finally write to context another NullWritable as key and the most common
            // model just counted as value.
            context.write(NullWritable.get(), new Text(mostCommonModel)); // Use this line to produce a single
                                                                          // classification
            // context.write(NullWritable.get(), new Text(KnnMap.toString())); // Use this
            // line to see all K nearest neighbours and distances
        }
    }

    // 主函数。调用 MapReduce 的 Job API 来配置本次运行的相关设定，并且提交任务。
    public static void main(String[] args) throws Exception {
        // 创建配置对象。
        Configuration conf = new Configuration();

        if (args.length != 3) {
            System.err.println("Usage: KnnPattern <in> <out> <parameter file>");
            System.exit(2);
        }

        // 创建 Job 对象。
        Job job = Job.getInstance(conf, "Find K-Nearest Neighbour");
        // 设置要运行的Jar包，即KnnPattern类所在的Jar包。
        job.setJarByClass(KnnPattern.class);
        // 把配置文件设定为 CacheFile，则后续各台服务器均可访问它的副本，从而减少小文件的传输开销。
        job.addCacheFile(new URI(args[2] + "#knnParamFile"));

        // 设置 MapReduce 任务的自定义类型。
        job.setMapperClass(KnnMapper.class);
        job.setReducerClass(KnnReducer.class);
        job.setNumReduceTasks(1); // 本项目只需要一个 Reducer 任务。

        // 设置输出的键值类型。
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(DoubleString.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);

        // 设置输入文件（训练数据集）的路径和输出目录的路径。
        // 分类结果将作为一个文件保存在输出目录下。
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 等待作业执行完成并返回状态码。
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
