package com.example;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

// 自定义的可序列化类型，保存了测试实例到一个训练实例的距离以及该训练实例的标签。
public class DoubleString implements WritableComparable<DoubleString> {
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