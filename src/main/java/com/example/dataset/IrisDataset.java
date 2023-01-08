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

package com.example.dataset;

import java.util.StringTokenizer;

import com.example.utils.DataUtils;

public class IrisDataset implements KnnDataset {

    @Override
    public KnnRecord createRecord(String string) {
        return new IrisRecord(string);
    }

    @Override
    public double distance(KnnRecord _a, KnnRecord _b) {
        if (!(_a instanceof IrisRecord) && !(_b instanceof IrisRecord)) {
            return invalidDistance;
        }
        IrisRecord a = (IrisRecord) _a;
        IrisRecord b = (IrisRecord) _b;
        return DataUtils.squaredEuclideDistance(a.features, b.features);
    }

    public static class IrisRecord implements KnnRecord {
        private static final int featureNumber = 4;

        private double[] features = new double[featureNumber];
        private int label = -1;

        public IrisRecord(String string) {
            StringTokenizer st = new StringTokenizer(string, ",");
            for (int i = 0; i < featureNumber; ++i) {
                features[i] = Double.parseDouble(st.nextToken());
            }
            label = Integer.parseInt(st.nextToken());
        }

        @Override
        public String getLabel() {
            return Integer.toString(label);
        }
    }
}
