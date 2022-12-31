package com.example.resampler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

//This class represents a dataset consisting of multiple records. Whether it's the original dataset, or
//the generated test data set is composed of this unit.
public class TextLineDataset {
    public ArrayList<String> data = new ArrayList<>();

    public TextLineDataset() {
    }

    public TextLineDataset(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            data.add(line);
        }
    }
}
