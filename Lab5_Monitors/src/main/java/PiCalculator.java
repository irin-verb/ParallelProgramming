import java.util.Random;

public class PiCalculator implements Runnable {

    private long iterations;
    private byte threadIndex;

    public PiCalculator(byte threadIndex, long iterations) {
        this.iterations = iterations;
        this.threadIndex = threadIndex;
    }

    @Override
    public void run() {
        System.out.println("Подпоток " + Thread.currentThread().getName() + " начат");
        try {
            calculate();
            Thread.sleep(500);
            System.out.println("Подпоток " + Thread.currentThread().getName() + " завершен");
        }
        catch (InterruptedException exception) {
            System.out.println("Подпоток " + Thread.currentThread().getName() + " был прерван");
            Thread.currentThread().interrupt();
        }
    }

    private void calculate() throws InterruptedException {
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
        updateResultStorage(passed);
    }

    private void updateResultStorage(long result) {
        long[] results = PiCalculationResultStorage.getStorage();
        synchronized (PiCalculationResultStorage.class) {
            results[threadIndex] = result;
        }
    }

    private void updateProgressStorage(long stepsDone) {
        long progressStep = iterations / 20;
        if (stepsDone % progressStep == 0 && stepsDone > 0) {
            double progress = (double) stepsDone/ iterations * 100;
            double[] progresses = PiCalculationProgressStorage.getStorage();
            synchronized (PiCalculationProgressStorage.class) {
                progresses[threadIndex] = progress;
                PiCalculationProgressStorage.class.notify();
            }
        }
    }


}
