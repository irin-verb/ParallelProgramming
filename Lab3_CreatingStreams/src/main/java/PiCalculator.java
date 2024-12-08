import java.util.Random;
public class PiCalculator {
    private long error;

    PiCalculator(long error) {
        this.error = error;
    }
    PiCalculator() { this.error = (long) 1e3; }

    public double calculateByMonteCarloMethod() {
        double x, y;
        long passed = 0;
        Random rnd = new Random();

        for(long i = 0; i < error; ++i) {
            x = rnd.nextDouble();
            y = rnd.nextDouble();
            if((x * x + y * y) < 1.0)
                passed++;
            reportCalculationProgress(i);
        }
        reportCalculationProgress();

        return (((double) passed / error) * 4.0);
    }

    private void reportCalculationProgress() {
        System.out.printf("Прогресс: 100%%\n");
    }

    private void reportCalculationProgress(long stepsDone) {
        long progresssStep = error / 20;
        if (stepsDone % progresssStep == 0 && stepsDone > 0) {
            double progress = (double) stepsDone / error * 100;
            System.out.printf("Прогресс: %.2f%%\n" , progress);
        }
    }

    public void setError(long error) {
        this.error = error;
    }

    public long getError() {
        return error;
    }
}
