package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

// 这个类表示一个由多个记录组成的数据集。无论是原始的数据集，还是
// 生成的测试数据集，都是由这个单位组成的。
public class TextLineDataset {
    public ArrayList<String> data = new ArrayList<>();

    public TextLineDataset() {}
    
    public TextLineDataset(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            data.add(line);
        }
    }
}
