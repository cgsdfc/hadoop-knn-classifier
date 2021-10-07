package com.example;

import java.util.StringTokenizer;

public class CarOwnerRecord {
    // 保存从配置文件中解析出来的测试实例的字段。
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

    public CarOwnerRecord(String str) {
        StringTokenizer st = new StringTokenizer(str, ",");

        // 解析各个字段的数据。
        this.age = minMaxNormalize(nextDouble(st), minAge, maxAge);
        this.income = minMaxNormalize(nextDouble(st), minIncome, maxIncome);
        this.status = st.nextToken();
        this.gender = st.nextToken();
        this.children = minMaxNormalize(nextDouble(st), minChildren, maxChildren);
        this.model = st.nextToken();
    }

}
