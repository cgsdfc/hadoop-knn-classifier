package com.example.knn;

import org.apache.hadoop.io.Writable;

public class DoubleStringWritable implements Writable {

    private double doubleValue = 0;
    private String stringValue = "";

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public DoubleStringWritable(double doubleValue, String stringValue) {
        this.doubleValue = doubleValue;
        this.stringValue = stringValue;
    }

    public DoubleStringWritable() {
    }

    public void write(java.io.DataOutput output) throws java.io.IOException {
        output.writeDouble(doubleValue);
        output.writeUTF(stringValue);
    }

    public void readFields(java.io.DataInput input) throws java.io.IOException {
        this.doubleValue = input.readDouble();
        this.stringValue = input.readUTF();
    }
}
