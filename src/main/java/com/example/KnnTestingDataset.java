package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class KnnTestingDataset {

    private ArrayList<KnnRecord> records;

    public int size() {
        return records.size();
    }

    public KnnRecord get(int i) {
        return records.get(i);
    }

    public KnnTestingDataset(KnnConfigFile configFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(configFile.testingFile)));
        String line;
        while ((line = reader.readLine()) != null) {
            KnnRecord r = configFile.dataset.createRecord(line, true);
            records.add(r);
        }
    }
}
