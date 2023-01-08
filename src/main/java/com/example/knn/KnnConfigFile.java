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

package com.example.knn;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import com.example.dataset.KnnDataset;
import com.example.dataset.KnnDatasetFactory;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.mapreduce.Job;

public class KnnConfigFile {

    public int K;
    public KnnDataset dataset;
    public String testingFile;

    private static final String symlink = "knnParamsFile";

    public static void initialize(Job job, String configFilename) throws Exception {
        job.addCacheFile(new URI(configFilename + "#" + symlink));
    }

    public KnnConfigFile() throws IOException {
        String knnParams = FileUtils.readFileToString(new File("./" + symlink), //
                Charset.defaultCharset());
        Gson gson = new Gson();
        KnnConfigData data = gson.fromJson(knnParams, KnnConfigData.class);
        this.K = data.k;
        String datasetName = data.ds.toLowerCase();
        this.dataset = KnnDatasetFactory.get().getDataset(datasetName);
    }
}
