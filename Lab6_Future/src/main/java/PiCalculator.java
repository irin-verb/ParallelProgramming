import java.util.Random;
import java.util.concurrent.Callable;

public class PiCalculator implements Callable<Long> {

    private long iterations;
    private byte threadIndex;

    public PiCalculator(byte threadIndex, long iterations) {
        this.iterations = iterations;
        this.threadIndex = threadIndex;
    }

    @Override
    public Long call() throws Exception {
        System.out.println("Подпоток " + Thread.currentThread().getName() + " начат");
        try {
            long result = calculate();
            System.out.println("Подпоток " + Thread.currentThread().getName() + " завершен");
            return result;
        }
        catch (InterruptedException exception) {
            System.out.println("Подпоток " + Thread.currentThread().getName() + " был прерван");
            Thread.currentThread().interrupt();
            return null;
        }
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
            progresses[threadIndex] = progress;
            synchronized (PiCalculationProgressStorage.class) {
                PiCalculationProgressStorage.class.notify();
            }
        }
    }


}
