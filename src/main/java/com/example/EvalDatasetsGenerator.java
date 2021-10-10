package com.example;

import java.util.ArrayList;

// 这个类负责生成评估数据集，并且把每个生成的数据集写入一个sink里。
public interface EvalDatasetsGenerator {

    // 这个类表示一个由多个记录组成的数据集。无论是原始的数据集，还是
    // 生成的测试数据集，都是由这个单位组成的。
    public static class Dataset {
        public ArrayList<String> data;
    }

    // 这个类表示生成的其中一个测试数据集。一次测试可能需要多个测试数据集。
    public static class EvalDataset {
        public Dataset training;
        public Dataset testing;
    }

    // 这个类用来接收生成的测试数据集。
    public static interface EvalDatasetSink {
        public void receive(EvalDataset datasets);
    }

    // 这个方法用来生成所有测试数据集，并且写入sink中。
    public void generate(Dataset originDataset, EvalDatasetSink sink);
}
