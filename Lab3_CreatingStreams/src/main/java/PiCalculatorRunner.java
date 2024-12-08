import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PiCalculatorRunner implements Runnable {
    @java.lang.Override
    public void run() {
        List<Long> errorList = new ArrayList<>(Arrays.asList((long) 1e8, (long) 2e8, (long) 4e8));
        PiCalculator calculator = new PiCalculator();

        for (long err : errorList) {

            calculator.setError(err);

            long startTime = System.currentTimeMillis();

            double piNumber = calculator.calculateByMonteCarloMethod();

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            System.out.println("Погрешность: " + err);
            System.out.println("Число Пи : " + piNumber);
            System.out.println("Время выполнения (с) :" + executionTime + "\n");
        }
    }
}
