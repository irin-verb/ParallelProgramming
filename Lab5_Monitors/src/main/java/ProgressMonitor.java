public class ProgressMonitor implements Runnable {

    private static final byte BAR_LENGTH = 20;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (PiCalculationProgressStorage.class) {
                try {
                    PiCalculationProgressStorage.class.wait();
                    logProgress();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

//        while (true) {
//            try {
//                logProgress();
//                Thread.sleep(500);
//                if (Thread.currentThread().isInterrupted()) {
//                    Thread.currentThread().interrupt();
//                    break;
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }

    }

    private void logProgress() throws InterruptedException {
        double[] progress = PiCalculationProgressStorage.getStorage();

        StringBuilder output = new StringBuilder("\r");

        double totalPersent = 0;
        for (int i = 0; i < progress.length; ++i) {
            totalPersent += progress[i];
            output.append(getProgressBar(progress[i], "Поток " + i));
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
        int koeff = getBarKoeff(progress);
        return String.format(title + ": [%s%s] %5.2f%%\t\t", getBar(koeff), getEmptySpace(koeff), progress);
    }

    private int getBarKoeff(double percent) { return (int) (percent / 100 * BAR_LENGTH);}
    private String getBar(int n) { return "=".repeat(n) + ">"; }
    private String getEmptySpace(int n) { return " ".repeat(BAR_LENGTH - n); }


}
