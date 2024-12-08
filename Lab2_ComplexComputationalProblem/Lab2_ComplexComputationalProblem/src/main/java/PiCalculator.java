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
        for(int i = 0; i < error; ++i) {
            x = rnd.nextDouble();
            y = rnd.nextDouble();
            if((x * x + y * y) < 1.0)
                passed++;
        }
        return (((double) passed / error) * 4.0);
    }

    public void setError(long error) {
        this.error = error;
    }

    public long getError() {
        return error;
    }
}
