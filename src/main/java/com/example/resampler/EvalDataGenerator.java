// MIT License
// 
// Copyright (c) 2021 Cong Feng
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

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
