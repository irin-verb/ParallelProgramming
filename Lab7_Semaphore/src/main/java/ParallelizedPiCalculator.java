import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParallelizedPiCalculator implements Runnable {

    public static final byte NUM_THREADS = 4;
    private static final int MAX_SIMULTANEOUS_TASKS = 2;


    private long iterations;
    private ExecutorService executor;
    private List<Callable<Long>> tasks;
    private List<Future<Long>> futures;
    private Thread progressMonitor;
    private Semaphore semaphore;
    private CountDownLatch latch;

    public ParallelizedPiCalculator(long iterations) {
        this.iterations = iterations;
        this.executor = Executors.newFixedThreadPool(NUM_THREADS);
        this.tasks = new ArrayList<Callable<Long>>();
        this.futures = new ArrayList<>();
        this.progressMonitor = new Thread(new ProgressMonitor(), "ProgressMonitor");
        this.semaphore = new Semaphore(MAX_SIMULTANEOUS_TASKS, true);
        this.latch = new CountDownLatch(NUM_THREADS);
    }


    @java.lang.Override
    public void run() {

        System.out.println("Поток " + Thread.currentThread().getName() + " начат");
        int iterationsPerThread = (int) (iterations / NUM_THREADS);

        tasks = IntStream.range(0, NUM_THREADS)
                .mapToObj(i -> new PiCalculator((byte) i, iterationsPerThread, semaphore, latch))
                .collect(Collectors.toList());

        progressMonitor.start();

        long passedTotal = 0;
        long allTasksEndTime = 0;

        try {
            futures = executor.invokeAll(tasks);
            latch.await();
            allTasksEndTime = System.currentTimeMillis();

            for (Future<Long> future: futures) {
                passedTotal += future.get();
            }

            System.out.println();
            for (int i = 0; i < NUM_THREADS; ++i) {
                long finishTime = ((PiCalculator) tasks.get(i)).getFinishTime();
                long delay = allTasksEndTime - finishTime;
                System.out.println("Задача " + (i+1) + " завершилась на " + delay + " мс раньше конца всех вычислений.");
            }

            double piNumber =  (((double) passedTotal / iterations) * 4.0);
            System.out.println("Вычисленное число Пи: " + piNumber);
            System.out.println("Поток " + Thread.currentThread().getName() + " завершен");
        }
        catch (InterruptedException e) {
            for (Future<Long> future: futures) {
                if (!future.isDone()) {
                    future.cancel(true);
                }
            }
            System.out.println("Поток " + Thread.currentThread().getName() + " был прерван");
        } catch (ExecutionException e) {
            System.out.println("Поток вычислений прервался с ошибкой в процессе вычисления");
        } finally {
            progressMonitor.interrupt();
            executor.shutdown();
        }

    }

}
