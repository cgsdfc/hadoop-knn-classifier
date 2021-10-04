package com.example;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
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

        // 保存从配置文件中解析出来的测试实例的字段。
        double normalisedSAge;
        double normalisedSIncome;
        String sStatus;
        String sGender;
        double normalisedSChildren;

        // 有些字段有最大和最小值，可以利用它们把相应的字段正则化。
        // 正则化可以使总的距离不受某个字段的大小的影响。
        double minAge = 18;
        double maxAge = 77;
        double minIncome = 5000;
        double maxIncome = 67789;
        double minChildren = 0;
        double maxChildren = 5;

        // 把输入的字符串转化为浮点数，并以输入的最大最小值对其进行正则化。
        // 正则化后的取值范围为0-1。
        private double normalisedDouble(String n1, double minValue, double maxValue) {
            return (Double.parseDouble(n1) - minValue) / (maxValue - minValue);
        }

        // 计算两个离散型变量的距离。这里我们简单把距离定义为两个变量是否相等。
        // 这个定义所产生的距离与上述的正则化产生的距离在值域上是一致的。
        private double nominalDistance(String t1, String t2) {
            if (t1.equals(t2)) {
                return 0;
            } else {
                return 1;
            }
        }

        // 计算一个距离的平方值。
        private double squaredDistance(double n1) {
            return Math.pow(n1, 2);
        }

        // 输入两个实例的数据，其中第一个实例的数据为 R1-R5，第二个实例的数据为 S1-S5，
        // 计算两个实例的对应属性的距离的平方和。
        // 对于离散型变量，计算它们的 nominalDistance，对于连续型变量，计算它们的差值。
        // 此方法返回的是两个实例的距离的平方（综合考虑了所有的属性）。
        private double totalSquaredDistance(//
                double R1, double R2, String R3, String R4, double R5, //
                double S1, double S2, String S3, String S4, double S5) {
            double ageDifference = S1 - R1;
            double incomeDifference = S2 - R2;
            double statusDifference = nominalDistance(S3, R3);
            double genderDifference = nominalDistance(S4, R4);
            double childrenDifference = S5 - R5;
            // 不需要开平方根，因为它不会改变值的顺序关系。
            return squaredDistance(ageDifference) + squaredDistance(incomeDifference) //
                    + statusDifference + genderDifference + squaredDistance(childrenDifference);
        }

        // 重写 Mapper 的setup方法，初始化本对象的一些数据。
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            // 获取配置文件。
            if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
                String knnParams = FileUtils.readFileToString(new File("./knnParamFile"), Charset.defaultCharset());
                StringTokenizer st = new StringTokenizer(knnParams, ",");

                // 获取参数K和测试实例的字段。
                K = Integer.parseInt(st.nextToken());
                normalisedSAge = normalisedDouble(st.nextToken(), minAge, maxAge);
                normalisedSIncome = normalisedDouble(st.nextToken(), minIncome, maxIncome);
                sStatus = st.nextToken();
                sGender = st.nextToken();
                normalisedSChildren = normalisedDouble(st.nextToken(), minChildren, maxChildren);
            }
        }

        // 重写map方法，实现对训练数据的分布式处理。
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // 对一行csv数据进行解析。训练数据记为r，测试数据记为t，作为变量的前缀以示区分。
            String rLine = value.toString();
            StringTokenizer st = new StringTokenizer(rLine, ",");

            // 解析各个字段的数据。
            double normalisedRAge = normalisedDouble(st.nextToken(), minAge, maxAge);
            double normalisedRIncome = normalisedDouble(st.nextToken(), minIncome, maxIncome);
            String rStatus = st.nextToken();
            String rGender = st.nextToken();
            double normalisedRChildren = normalisedDouble(st.nextToken(), minChildren, maxChildren);
            String rModel = st.nextToken();

            // 计算训练实例和测试实例的距离。
            double tDist = totalSquaredDistance(//
                    normalisedRAge, normalisedRIncome, rStatus, rGender, normalisedRChildren, // 训练数据。
                    normalisedSAge, normalisedSIncome, sStatus, sGender, normalisedSChildren); // 测试数据。

            // 更新距离最小的不超过K个实例的记录。
            KnnMap.put(tDist, rModel);
            // 最多保存K个记录。
            if (KnnMap.size() > K) {
                KnnMap.remove(KnnMap.lastKey());
            }
        }

        // 在map调用结束后，会调用cleanup方法，我们在这里把保存在knnMap中的数据写入Context中。
        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            // 遍历knnMap，把数据导出到context。
            for (Map.Entry<Double, String> entry : KnnMap.entrySet()) {
                Double knnDist = entry.getKey();
                String knnModel = entry.getValue();
                // 因为是把数据交给hadoop，必须要能够序列化，所以必须使用DoubleString类型。
                distanceAndModel.set(knnDist, knnModel);
                // key为空，所以NullWritable是一个占位符。
                context.write(NullWritable.get(), distanceAndModel);
            }
        }
    }

    // 自定义 Reducer 类型，把 Mapper 产生的数据进行汇总，导出最后的分类结果。
    // 这里用的策略和 Mapper 是一样的，就是用 TreeMap 保存K-邻域（K个距离最小的实例）。
    // 但是在最后，我们要从K-邻域中产生一个分类结果，那就是要把占比最大的标签作为最终的分类标签。
    // 输出 Key 类型：为了增加可读性，我们用一个字符串来说明输出的内容，所以用Text作为类型。
    // 输出 Value 类型：表示的是分类的结果。
    public static class KnnReducer extends Reducer<NullWritable, DoubleString, Text, Text> {
        // 保存K-邻域。
        TreeMap<Double, String> KnnMap = new TreeMap<Double, String>();
        // 算法参数K。
        int K;

        // 从配置文件获取算法参数K。
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
                // 读入配置文件。
                String knnParams = FileUtils.readFileToString(new File("./knnParamFile"), Charset.defaultCharset());
                StringTokenizer st = new StringTokenizer(knnParams, ",");
                // 获取第一个字段即可。
                K = Integer.parseInt(st.nextToken());
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
                // 注意不超过K个条目。
                if (KnnMap.size() > K) {
                    KnnMap.remove(KnnMap.lastKey());
                }
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
            context.write(new Text("K-Nearest-Neighbours: "), new Text(KnnMap.toString()));
        }
    }

    // 主函数。调用 MapReduce 的 Job API 来配置本次运行的相关设定，并且提交任务。
    public static void main(String[] args) throws Exception {
        // 创建配置对象。
        Configuration conf = new Configuration();
        
        // 命令行参数有误。
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
