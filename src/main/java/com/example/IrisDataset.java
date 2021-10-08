package com.example;

import java.util.StringTokenizer;

public class IrisDataset implements KnnDataset {

    @Override
    public KnnRecord createRecord(String string, boolean is_testing) {
        return new IrisRecord(string, is_testing);
    }

    public static class IrisRecord implements KnnRecord {
        private static final int featureNumber = 4;
        private static final String[] labelNames = { "setosa", "versicolor", "virginica" };

        private double[] features = new double[featureNumber];
        private int label = -1;

        public IrisRecord(String string, boolean is_testing) {
            StringTokenizer st = new StringTokenizer(string, ",");
            for (int i = 0; i < featureNumber; ++i) {
                features[i] = Double.parseDouble(st.nextToken());
            }
            if (is_testing)
                return;
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
            return labelNames[this.label];
        }
    }
}
