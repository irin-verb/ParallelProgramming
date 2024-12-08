import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ProgressMonitor implements Runnable {

    private static final byte BAR_LENGTH = 4;

    @Override
    public void run() {
        Lock lock = PiCalculationProgressStorage.getLock();
        Condition progressUpdated = PiCalculationProgressStorage.getIsUpdatedCondition();

        while (!Thread.currentThread().isInterrupted()) {
            lock.lock();
            try {
                progressUpdated.await();
                logProgress();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                lock.unlock();
            }
        }
    }

    private void logProgress() throws InterruptedException {
        double[] progress = PiCalculationProgressStorage.getStorage();

        StringBuilder output = new StringBuilder("\r");

        double totalPersent = 0;
        for (int i = 0; i < progress.length; ++i) {
            totalPersent += progress[i];
            output.append(getProgressBar(progress[i], "Поток" + (i+1)));
        }

        totalPersent = totalPersent / progress.length;
        output.append(getProgressBar(totalPersent, "Итого"));

        System.out.print(output.toString());

        if (totalPersent == 100) {
            System.out.println();
            throw new InterruptedException();
        }
    }

    private String getProgressBar(double progress, String title) {
        return String.format(title + ":%5.2f%%\t", progress);
    }

}
