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

package com.example.knn;

import java.util.Collection;
import java.util.TreeMap;
import java.util.Set;
import java.util.Map.Entry;

//Implements an ordered collection that can only hold at most k smallest elements. 
// What is actually saved is the distance between the training sample and the test sample and its label.
public class KSmallestMap {
    private TreeMap<Double, String> data;
    private int K;

    public KSmallestMap(int K) {
        this.K = K;
        this.data = new TreeMap<Double, String>();
    }

    public void put(Double key, String value) {
        data.put(key, value);
        // Take care that there are no more than k entries.
        if (data.size() > K) {
            data.remove(data.lastKey());
        }
    }

    public int size() {
        return data.size();
    }

    public Collection<String> values() {
        return data.values();
    }

    public Set<Entry<Double, String>> entrySet() {
        return data.entrySet();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("distance\tmodel\n");
        for (Entry<Double, String> item : entrySet()) {
            sb.append(item.getKey());
            sb.append('\t');
            sb.append(item.getValue());
            sb.append('\n');
        }
        return sb.toString();
    }
}
