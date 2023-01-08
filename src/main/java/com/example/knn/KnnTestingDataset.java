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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.example.dataset.KnnRecord;

import java.net.URI;

import org.apache.hadoop.mapreduce.Job;

public class KnnTestingDataset {

    private ArrayList<KnnRecord> records = new ArrayList<>();

    // By using this symbolic link to access the file, you don't need to know what
    // the original file name was.
    private static final String symlink = "knnTesingFile";

    public static void initialize(Job job, String testingFilename) throws Exception {
        job.addCacheFile(new URI(testingFilename + "#" + symlink));
    }

    public int size() {
        return records.size();
    }

    public KnnRecord get(int i) {
        return records.get(i);
    }

    public KnnTestingDataset(KnnConfigFile configFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("./" + symlink)));
        String line;
        while ((line = reader.readLine()) != null) {
            KnnRecord r = configFile.dataset.createRecord(line);
            records.add(r);
        }
    }
}
