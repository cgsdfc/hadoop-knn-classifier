package com.example.resampler;

// This class is responsible for generating evaluation datasets and 
// writing each generated dataset to a sink.
public interface EvalDataGenerator {

    /// This class represents one of the generated test datasets.
    // A test may require multiple test datasets.
    public static class EvalDataset {
        public TextLineDataset training = new TextLineDataset();
        public TextLineDataset testing = new TextLineDataset();
    }

    // This class is used to receive the generated test dataset.
    public static interface EvalDatasetSink {
        public void receive(EvalDataset dataset) throws Exception;
    }

    // This method is used to generate all test data sets and write them into the
    // sink.
    public void generate(TextLineDataset originDataset, EvalDatasetSink sink) throws Exception;

    // Returns a string describing the test method.
    public String getSpecs();
}
