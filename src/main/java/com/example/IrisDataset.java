package com.example;

import java.util.StringTokenizer;

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
