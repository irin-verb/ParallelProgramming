public class PiCalculationProgressStorage {

    private static volatile double[] storage;

    public static double[] getStorage() {
        double[] localRef = storage;
        if (localRef == null) {
            synchronized (PiCalculationProgressStorage.class) {
                localRef = storage;
                if (localRef == null) {
                    storage = localRef = new double[ParallelizedPiCalculator.NUM_THREADS];
                }
            }
        }
        return localRef;
    }

}
