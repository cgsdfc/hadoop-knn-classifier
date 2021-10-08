package com.example;

public class IrisDataset implements KnnDataset {

    @Override
    public KnnRecord createRecord(String string, boolean is_testing) {
        return new IrisRecord();
    }

    public static class IrisRecord implements KnnRecord {
        private double[] features;

        @Override
        public double distance(KnnRecord other) {
            if (other instanceof IrisDataset) {
                return 0;
            }
            return KnnRecord.invalidDistance;
        }

        @Override
        public String getLabel() {
            return "";
        }
    }
}
