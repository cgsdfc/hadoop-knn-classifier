package com.example;

import java.util.StringTokenizer;

public class IrisDataset implements KnnDataset {

    @Override
    public KnnRecord createRecord(String string) {
        return new IrisRecord(string);
    }

    public static class IrisRecord implements KnnRecord {
        private static final int featureNumber = 4;
        // private static final String[] labelNames = { "setosa", "versicolor", "virginica" };

        private double[] features = new double[featureNumber];
        private int label = -1;

        public IrisRecord(String string ) {
            StringTokenizer st = new StringTokenizer(string, ",");
            for (int i = 0; i < featureNumber; ++i) {
                features[i] = Double.parseDouble(st.nextToken());
            }
            label = Integer.parseInt(st.nextToken());
        }

        private static double computeDistance(IrisRecord a, IrisRecord b) {
            return DataUtils.squaredEuclideDistance(a.features, b.features);
        }

        @Override
        public double distance(KnnRecord other) {
            if (other instanceof IrisRecord) {
                return computeDistance(this, (IrisRecord) other);
            }
            return KnnRecord.invalidDistance;
        }

        @Override
        public String getLabel() {
            return Integer.toString(label);
        }
    }
}
