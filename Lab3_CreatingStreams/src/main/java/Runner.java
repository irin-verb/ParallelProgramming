
public class Runner {

    public static void main(String[] args) {
            PiCalculatorRunner piCalculatorRunner = new PiCalculatorRunner();
            Thread piCalculationThread = new Thread(piCalculatorRunner, "PiCalculationThread");
            piCalculationThread.start();
    }
}