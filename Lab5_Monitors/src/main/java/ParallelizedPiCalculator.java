public class ParallelizedPiCalculator implements Runnable {

    public static final byte NUM_THREADS = 2;

    private long iterations;
    private Thread[] threads;

    public ParallelizedPiCalculator(long iterations) { this.iterations = iterations; }


    @java.lang.Override
    public void run() {
        System.out.println("Поток " + Thread.currentThread().getName() + " начат");
        int iterationsPerThread = (int) (iterations / NUM_THREADS);
        threads = new Thread[NUM_THREADS];
        for (byte i = 0; i < NUM_THREADS; ++i) {
            threads[i] = new Thread(new PiCalculator(i, iterationsPerThread), "PiCalculator" + i);
            threads[i].start();
        }

        Thread progressMonitor = new Thread(new ProgressMonitor(), "ProgressMonitor");
        progressMonitor.start();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                interruptAllThreads();
                progressMonitor.interrupt();
                Thread.currentThread().interrupt();
                System.out.println("Поток " + Thread.currentThread().getName() + " был прерван");
            }
        }
        progressMonitor.interrupt();

        long passedTotal = 0;
        long[] results = PiCalculationResultStorage.getStorage();
        for (long result: results) {
            passedTotal += result;
        }
        double piNumber =  (((double) passedTotal / iterations) * 4.0);
        System.out.println("Вычисленное число Пи: " + piNumber);
        System.out.println("Поток " + Thread.currentThread().getName() + " завершен");
    }


    private void interruptAllThreads() {
        for (Thread thread : threads) {
            if (thread != null) {
                thread.interrupt();
            }
        }
    }

}
