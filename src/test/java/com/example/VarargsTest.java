package com.example;

import org.junit.Test;

public class VarargsTest {

    private static void print(String... args) {
        for (String s : args) {
            System.out.println(s);
        }
    }

    @Test
    public void test() {
        String[] args = new String[] { "a", "b", "c" };
        print(args);
    }
}
