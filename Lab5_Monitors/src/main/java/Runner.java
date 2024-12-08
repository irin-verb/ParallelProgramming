public class Runner {

    public static void main(String[] args) {
        long eps = (long) 6e8;
        ParallelizedPiCalculator piCalculator = new ParallelizedPiCalculator(eps);
        Thread thread = new Thread(piCalculator, "MainPiCalculator");
        thread.start();
    }
}