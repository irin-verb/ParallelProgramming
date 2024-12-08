public class PiCalculationResultStorage {

    private static volatile long[] storage;

    public static long[] getStorage() {
        long[] localRef = storage;
        if (localRef == null) {
            synchronized (PiCalculationResultStorage.class) {
                localRef = storage;
                if (localRef == null) {
                    storage = localRef = new long[ParallelizedPiCalculator.NUM_THREADS];
                    System.out.println("Хранилище данных было создано");
                }
            }
        }
        return localRef;
    }

}
