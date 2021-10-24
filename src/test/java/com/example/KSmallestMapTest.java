package com.example;

import static org.junit.Assert.assertTrue;

import com.example.knn.KSmallestMap;

import org.junit.Before;
import org.junit.Test;

public class KSmallestMapTest {

    private static final int K = 5;
    private KSmallestMap knnMap;

    @Before
    public void setupKnnMap() {
        knnMap = new KSmallestMap(K);
    }

    @Test
    public void testPut() {
        final int numbers = K * 2;
        for (int i = 0; i < numbers; ++i) {
            knnMap.put((double) i, "");
            assertTrue(knnMap.size() <= K);
        }
    }
}
