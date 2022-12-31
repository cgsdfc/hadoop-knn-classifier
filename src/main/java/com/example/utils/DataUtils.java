// MIT License
// 
// Copyright (c) 2021 cgsdfc
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

package com.example.utils;

public class DataUtils {

    // Convert the input string to a floating point number,
    // and regularize it with the input maximum and minimum values.
    // The value range after regularization is 0-1.
    public static double minMaxNormalize(double n1, double minValue, double maxValue) {
        return (n1 - minValue) / (maxValue - minValue);
    }

    // Calculate the distance between two discrete variables.
    // Here we simply define distance as whether two variables are equal or not.
    // The distance generated by this definition is consistent
    // with the distance generated by the above regularization in the value range.
    public static double nominalDistance(String t1, String t2) {
        if (t1.equals(t2)) {
            return 0;
        } else {
            return 1;
        }
    }

    public static double sumOfSquares(double... args) {
        double ans = 0;
        for (double val : args) {
            ans += Math.pow(val, 2);
        }
        return ans;
    }

    public static double squaredEuclideDistance(double[] v1, double[] v2) {
        double[] diff = new double[v1.length];
        for (int i = 0; i < diff.length; ++i) {
            diff[i] = v1[i] - v2[i];
        }
        return sumOfSquares(diff);
    }

    public static double[] computeMeanAndStd(Iterable<Double> data) {
        double mean = 0;
        double std = 0;
        int length = 0;
        for (double x : data) {
            mean += x;
            length++;
        }
        mean /= length;
        for (double x : data) {
            std += Math.pow(x - mean, 2);
        }
        std /= length - 1;
        std = Math.pow(std, 0.5);
        return new double[] { mean, std };
    }
}
