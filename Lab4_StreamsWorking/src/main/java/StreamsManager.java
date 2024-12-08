import java.util.ArrayList;
import java.util.List;

public class StreamsManager {
    private static int ID = 0;
    private static List<Thread> threads = new ArrayList<>();

    public static void start(int eps) {
        PiCalculator piCalculator = new PiCalculator(eps);
        Thread thread = new Thread(piCalculator, String.valueOf(ID));
        threads.add(thread);
        thread.start();
        ID++;
    }

    public static void await(int id) {
        System.out.println("Ожидание завершения потока " + id);
        try {
            threads.get(id).join();
        }
        catch (InterruptedException e) {
            threads.get(id).interrupt();
        }
    }

    public static void stop(int id) {
        threads.get(id).interrupt();
    }

    public static void killThemAll() {
        for (Thread thr : threads) {
            thr.interrupt();
        }
    }
}
