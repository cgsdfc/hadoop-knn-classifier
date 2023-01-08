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

import static com.example.utils.DataUtils.minMaxNormalize;

import java.util.StringTokenizer;

import com.example.utils.DataUtils;

public class CarOwnersDataset implements KnnDataset {
    @Override
    public KnnRecord createRecord(String string) {
        return new CarOwnerRecord(string);
    }

    @Override
    public double distance(KnnRecord _a, KnnRecord _b) {
        if (!(_a instanceof CarOwnerRecord) && !(_b instanceof CarOwnerRecord)) {
            return invalidDistance;
        }
        CarOwnerRecord a = (CarOwnerRecord) _a;
        CarOwnerRecord b = (CarOwnerRecord) _b;
        return DataUtils.sumOfSquares(a.age - b.age, //
                a.income - b.income, //
                DataUtils.nominalDistance(a.status, b.status), //
                DataUtils.nominalDistance(a.gender, b.gender), //
                a.children - b.children);
    }

    public static class CarOwnerRecord implements KnnRecord {
        public double age;
        public double income;
        public String status;
        public String gender;
        public double children;
        public String model;

        // Some fields have maximum and minimum values, which can be used to regularize
        // the corresponding fields.
        // Regularization can make the total distance independent of the size of a
        // field.
        public static final double minAge = 18;
        public static final double maxAge = 77;
        public static final double minIncome = 5000;
        public static final double maxIncome = 67789;
        public static final double minChildren = 0;
        public static final double maxChildren = 5;

        private static double nextDouble(StringTokenizer st) {
            return Double.parseDouble(st.nextToken());
        }

        public CarOwnerRecord(StringTokenizer st) {
            // Parse the data of each field.
            this.age = minMaxNormalize(nextDouble(st), minAge, maxAge);
            this.income = minMaxNormalize(nextDouble(st), minIncome, maxIncome);
            this.status = st.nextToken();
            this.gender = st.nextToken();
            this.children = minMaxNormalize(nextDouble(st), minChildren, maxChildren);
            this.model = st.nextToken();
        }

        public CarOwnerRecord(String str) {
            this(new StringTokenizer(str, ","));
        }

        @Override
        public String getLabel() {
            return model;
        }
    }
}
