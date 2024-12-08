public class Runner {

    public static void main(String[] args) {
        long eps = (long) 12e8;
        ParallelizedPiCalculator piCalculator = new ParallelizedPiCalculator(eps);
        Thread thread = new Thread(piCalculator, "MainPiCalculator");
        thread.start();
        
//        Thread thread_2 = new Thread(piCalculator, "MainPiCalculator");
    }
}