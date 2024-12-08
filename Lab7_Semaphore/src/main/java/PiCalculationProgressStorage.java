import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PiCalculationProgressStorage {

    private static volatile double[] storage;
    private static final Lock lock = new ReentrantLock();
    private static final Condition isUpdated = lock.newCondition();

    public static double[] getStorage() {
        double[] localRef = storage;
        if (localRef == null) {
            lock.lock();
            try {
                localRef = storage;
                if (localRef == null) {
                    storage = localRef = new double[ParallelizedPiCalculator.NUM_THREADS];
                }
            } finally {
                lock.unlock();
            }
        }
        return localRef;
    }

    public static Lock getLock() {
        return lock;
    }

    public static Condition getIsUpdatedCondition() {
        return isUpdated;
    }

}
