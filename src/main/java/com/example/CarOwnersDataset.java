package com.example;

import java.util.StringTokenizer;
import static com.example.DataUtils.minMaxNormalize;

public class CarOwnersDataset implements KnnDataset {
    @Override
    public KnnRecord createRecord(String string) {
        return new CarOwnerRecord(string);
    }

    @Override
    public double distance(KnnRecord _a, KnnRecord _b) {
        if (!(_a instanceof CarOwnerRecord) && !(_b instanceof CarOwnerRecord)) {
            return invalidDistance;
        }
        CarOwnerRecord a = (CarOwnerRecord) _a;
        CarOwnerRecord b = (CarOwnerRecord) _b;
        return DataUtils.sumOfSquares(a.age - b.age, //
                a.income - b.income, //
                DataUtils.nominalDistance(a.status, b.status), //
                DataUtils.nominalDistance(a.gender, b.gender), //
                a.children - b.children);
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

        private static double nextDouble(StringTokenizer st) {
            return Double.parseDouble(st.nextToken());
        }

        public CarOwnerRecord(StringTokenizer st) {
            // 解析各个字段的数据。
            this.age = minMaxNormalize(nextDouble(st), minAge, maxAge);
            this.income = minMaxNormalize(nextDouble(st), minIncome, maxIncome);
            this.status = st.nextToken();
            this.gender = st.nextToken();
            this.children = minMaxNormalize(nextDouble(st), minChildren, maxChildren);
            this.model = st.nextToken();
        }

        public CarOwnerRecord(String str) {
            this(new StringTokenizer(str, ","));
        }

        @Override
        public String getLabel() {
            return model;
        }
    }
}
