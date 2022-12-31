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
