package com.example;

// 这个类负责生成评估数据集，并且把每个生成的数据集写入一个sink里。
public interface EvalDataGenerator {

    // 这个类表示生成的其中一个测试数据集。一次测试可能需要多个测试数据集。
    public static class EvalDataset {
        public TextLineDataset training = new TextLineDataset();
        public TextLineDataset testing = new TextLineDataset();
    }

    // 这个类用来接收生成的测试数据集。
    public static interface EvalDatasetSink {
        public void receive(EvalDataset dataset) throws Exception;
    }

    // 这个方法用来生成所有测试数据集，并且写入sink中。
    public void generate(TextLineDataset originDataset, EvalDatasetSink sink) throws Exception;

    // 返回测试方法的描述的字符串。
    public String getSpecs();
}
