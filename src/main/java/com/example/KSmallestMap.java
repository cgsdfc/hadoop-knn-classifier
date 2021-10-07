package com.example;

import java.util.Collection;
import java.util.TreeMap;
import java.util.Set;
import java.util.Map.Entry;

// 实现了一个最多只能保存K个最小元素的有序集合。实际上保存的是训练样本和测试样本的距离及其标签。
public class KSmallestMap {
    private TreeMap<Double, String> data;
    private int K;

    public KSmallestMap(int K) {
        this.K = K;
        this.data = new TreeMap<Double, String>();
    }

    public void put(Double key, String value) {
        data.put(key, value);
        // 注意不超过K个条目。
        if (data.size() > K) {
            data.remove(data.lastKey());
        }
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
