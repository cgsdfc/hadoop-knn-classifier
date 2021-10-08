package com.example;

import java.util.StringTokenizer;

public class CarOwnersDataset implements KnnDataset {
    @Override
    public KnnRecord createRecord(String string, boolean is_testing) {
        return new CarOwnerRecord(string, is_testing);
    }

    public static class CarOwnerRecord implements KnnRecord {
        public double age;
        public double income;
        public String status;
        public String gender;
        public double children;
        public String model;

        // 有些字段有最大和最小值，可以利用它们把相应的字段正则化。
        // 正则化可以使总的距离不受某个字段的大小的影响。
        public static final double minAge = 18;
        public static final double maxAge = 77;
        public static final double minIncome = 5000;
        public static final double maxIncome = 67789;
        public static final double minChildren = 0;
        public static final double maxChildren = 5;

        // 把输入的字符串转化为浮点数，并以输入的最大最小值对其进行正则化。
        // 正则化后的取值范围为0-1。
        private static double minMaxNormalize(double n1, double minValue, double maxValue) {
            return (n1 - minValue) / (maxValue - minValue);
        }

        private static double nextDouble(StringTokenizer st) {
            return Double.parseDouble(st.nextToken());
        }

        public CarOwnerRecord(StringTokenizer st, boolean testing) {
            // 解析各个字段的数据。
            this.age = minMaxNormalize(nextDouble(st), minAge, maxAge);
            this.income = minMaxNormalize(nextDouble(st), minIncome, maxIncome);
            this.status = st.nextToken();
            this.gender = st.nextToken();
            this.children = minMaxNormalize(nextDouble(st), minChildren, maxChildren);
            if (testing) {
                this.model = null;
            } else {
                this.model = st.nextToken();
            }
        }

        public CarOwnerRecord(String str, boolean testing) {
            this(new StringTokenizer(str, ","), testing);
        }

        // 计算两个离散型变量的距离。这里我们简单把距离定义为两个变量是否相等。
        // 这个定义所产生的距离与上述的正则化产生的距离在值域上是一致的。
        private static double nominalDistance(String t1, String t2) {
            if (t1.equals(t2)) {
                return 0;
            } else {
                return 1;
            }
        }

        // 计算一个距离的平方值。
        private static double squaredDistance(double n1) {
            return Math.pow(n1, 2);
        }

        public static double computeDistance(CarOwnerRecord a, CarOwnerRecord b) {
            double ageDifference = a.age - b.age;
            double incomeDifference = a.income - b.income;
            double statusDifference = nominalDistance(a.status, b.status);
            double genderDifference = nominalDistance(a.gender, b.gender);
            double childrenDifference = a.children - b.children;
            // 不需要开平方根，因为它不会改变值的顺序关系。
            return squaredDistance(ageDifference) + squaredDistance(incomeDifference) //
                    + statusDifference + genderDifference + squaredDistance(childrenDifference);
        }

        @Override
        public double distance(KnnRecord other) {
            if (other instanceof CarOwnerRecord) {
                return computeDistance(this, (CarOwnerRecord) other);
            }
            return KnnRecord.invalidDistance;
        }

        @Override
        public String getLabel() {
            return model;
        }
    }
}
