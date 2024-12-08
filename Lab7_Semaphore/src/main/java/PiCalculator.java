import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class PiCalculator implements Callable<Long> {

    private long iterations;
    private byte threadIndex;
    private Semaphore semaphore;
    private CountDownLatch latch;
    private long finishTime;

    public PiCalculator(byte threadIndex, long iterations, Semaphore semaphore, CountDownLatch latch) {
        this.iterations = iterations;
        this.threadIndex = threadIndex;
        this.semaphore = semaphore;
        this.latch = latch;
    }

    @Override
    public Long call() throws Exception {
        try {
            semaphore.acquire();
            System.out.println("Подпоток " + Thread.currentThread().getName() + " начат");

            long result = calculate();
            finishTime = System.currentTimeMillis();

            System.out.println("Подпоток " + Thread.currentThread().getName() + " завершен");
            return result;
        }
        catch (InterruptedException exception) {
            System.out.println("Подпоток " + Thread.currentThread().getName() + " был прерван");
            Thread.currentThread().interrupt();
            return null;
        }
        finally {
            latch.countDown();
            semaphore.release();
        }
    }

    public long getFinishTime() {
        return finishTime;
    }

    private long calculate() throws InterruptedException {
        double x, y;
        long passed = 0;
        Random rnd = new Random();
        for (long i = 0; i < iterations; ++i) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            x = rnd.nextDouble();
            y = rnd.nextDouble();
            if((x * x + y * y) < 1.0)
                passed++;
            updateProgressStorage(i);
        }
        updateProgressStorage(iterations);
        return passed;
    }


    private void updateProgressStorage(long stepsDone) {
        long progressStep = iterations / 20;
        if (stepsDone % progressStep == 0 && stepsDone > 0) {
            double progress = (double) stepsDone/ iterations * 100;
            double[] progresses = PiCalculationProgressStorage.getStorage();

            Lock lock = PiCalculationProgressStorage.getLock();
            lock.lock();
            try {
                progresses[threadIndex] = progress;
                PiCalculationProgressStorage.getIsUpdatedCondition().signal();
            } finally {
                lock.unlock();
            }

            progresses[threadIndex] = progress;
            synchronized (PiCalculationProgressStorage.class) {
                PiCalculationProgressStorage.class.notify();
            }
        }
    }


}
