package com.example;

import java.util.StringTokenizer;

public class IrisDataset implements KnnDataset {

    @Override
    public KnnRecord createRecord(String string, boolean is_testing) {
        return new IrisRecord(string, is_testing);
    }

    public static class IrisRecord implements KnnRecord {
        private static final int feature_number = 4;

        private double[] features = new double[feature_number];
        private int label = -1;

        public IrisRecord(String string, boolean is_testing) {
            StringTokenizer st = new StringTokenizer(string, ",");
            for (int i = 0; i < feature_number; ++i) {
                features[i] = Double.parseDouble(st.nextToken());
            }
            if (is_testing)
                return;
            label = Integer.parseInt(st.nextToken());
        }

        private static double computeDistance(IrisRecord a, IrisRecord b) {
            double ans = 0;
            for (int i = 0; i < feature_number; ++i) {
                ans += Math.pow(a.features[i] - b.features[i], 2);
            }
            return ans;
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
