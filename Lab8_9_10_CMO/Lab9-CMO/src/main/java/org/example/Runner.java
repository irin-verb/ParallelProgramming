package org.example;

import org.example.Burse.Burse10;
import org.example.Burse.Burse11;
import org.example.Burse.Burse9;
import org.example.Burse.Currency.CurrencyPair;
import org.example.Burse.Currency.CurrencyType;
import org.example.Burse.IBurse;
import org.example.Burse.Notification.BurseCallback;
import org.example.Burse.Order.OrderType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
    private static Burse9 burse9;
    private static Burse10 burse10;
    private static Burse11 burse11;

    private static void initialize() {
        burse9 = new Burse9();
        burse10 = new Burse10();
        burse11 = new Burse11();
    }

    private static void close() {
        burse9.close();
        burse10.close();
        burse11.close();
    }

    public static void main(String[] args) throws InterruptedException {

        initialize();

        final int ORDERS_PER_CALLBACK = 10;
        final int CALLBACKS_COUNT = 10;

        BurseCallback[] callbacks = new BurseCallback[CALLBACKS_COUNT];
        for (int i = 0; i < CALLBACKS_COUNT; ++i)
            callbacks[i] = new BurseCallback();
//
//        double ordersCount1 = BurseTester.measureOrdersSentPerMs(burse9, callbacks, ORDERS_PER_CALLBACK);
//        for (int i = 0; i < CALLBACKS_COUNT; ++i)
//            callbacks[i] = new BurseCallback();
//
//        double ordersCount2 = BurseTester.measureOrdersSentPerMs(burse10, callbacks, ORDERS_PER_CALLBACK);
//        for (int i = 0; i < CALLBACKS_COUNT; ++i)
//            callbacks[i] = new BurseCallback();
//
//        double ordersCount3 = BurseTester.measureOrdersSentPerMs(burse11, callbacks, ORDERS_PER_CALLBACK);
//        for (int i = 0; i < CALLBACKS_COUNT; ++i)
//            callbacks[i] = new BurseCallback();
//
//        System.out.println("Биржа1 принимает " + String.format("%.2f заявок в мс", ordersCount1));
//        System.out.println("Биржа2 принимает " + String.format("%.2f заявок в мс", ordersCount2));
//        System.out.println("Биржа3 принимает " + String.format("%.2f заявок в мс", ordersCount3));

        double ordersCount1 = BurseTester.measureOrdersProcessedPerMs(burse9, callbacks, ORDERS_PER_CALLBACK);
        for (int i = 0; i < CALLBACKS_COUNT; ++i)
            callbacks[i] = new BurseCallback();

        double ordersCount2 = BurseTester.measureOrdersProcessedPerMs(burse10, callbacks, ORDERS_PER_CALLBACK);
        for (int i = 0; i < CALLBACKS_COUNT; ++i)
            callbacks[i] = new BurseCallback();

        double ordersCount3 = BurseTester.measureOrdersSentPerMs(burse11, callbacks, ORDERS_PER_CALLBACK);
        for (int i = 0; i < CALLBACKS_COUNT; ++i)
            callbacks[i] = new BurseCallback();

        System.out.println("Биржа1 обрабатывает " + String.format("%.2f заявок в мс", ordersCount1));
        System.out.println("Биржа2 обрабатывает " + String.format("%.2f заявок в мс", ordersCount2));
        System.out.println("Биржа3 обрабатывает " + String.format("%.2f заявок в мс", ordersCount3));

        close();





//        initialize();
//
//        final int THREADS_NUMBER = 50;
//        final int CALLBACKS_NUMBER = 20;
//        final int ORDERS_PER_CALLBACK = 3;
//
//        BurseCallback[] callbacks = new BurseCallback[CALLBACKS_NUMBER];
//        for (int i = 0; i < CALLBACKS_NUMBER; ++i)
//            callbacks[i] = new BurseCallback();
//
//        ExecutorService executor = Executors.newFixedThreadPool(THREADS_NUMBER);
//        CountDownLatch startLatch = new CountDownLatch(1);
//        CountDownLatch endLatch = new CountDownLatch(THREADS_NUMBER - 1);
//
//        for (int i = 0; i < THREADS_NUMBER; i++) {
//            int finalI = i;
//            executor.submit(() -> {
//                try {
//                    startLatch.await();
//                    for (int j = 0; j < callbacks.length; j++)
//                        for (int k = 0; k < ORDERS_PER_CALLBACK; k++) {
//                            BurseTester.processRandomOrder(burse9, callbacks[j]);
//                            System.out.println("поток" + finalI + " коллбек" + j + " заказ" + k);
//                        }
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                } finally {
//                    endLatch.countDown();
//                }
//            });
//        }
//        startLatch.countDown();
//        //endLatch.await();
//        Thread.sleep(1500);
//        executor.shutdown();
//        System.out.println(BurseTester.getGeneralMoney(callbacks));
//        System.out.println(BurseTester.getGeneralCount(callbacks));
//
//        close();






//        initialize();
//        try {
//            BurseCallback callback = new BurseCallback();
////            BurseTester.processRandomOrder(burse10, callback);
////            System.out.println(callback);
//            BurseTester.processBurseClosing(burse10, callback);
//            System.out.println(callback);
//        } catch (InterruptedException e) {
//        } finally {
//            close();
//        }


//        initialize();

////        burse10.addOrder(callback, new CurrencyPair(CurrencyType.EUR, CurrencyType.USD), OrderType.BUY, 1.4, 100);
////        Thread.sleep(1000);
//        //BurseTester.processRandomOrder(executor, burse10, callback);
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        initialize();
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        BurseCallback callback = new BurseCallback();
//        //BurseTester.sendRandomOrder(burse10, callback);
//        try {
//            executor.submit(() -> {
//                try {
//                    BurseTester.sendRandomOrder(burse10, callback);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//            callback.waitUpdated();
//        } catch (InterruptedException e) {
//        } finally {
//            System.out.println(callback.toString());
//            close();
//            executor.shutdown();
//        }
//        BurseTester.processRandomOrder(executor, burse9, callback);
//        System.out.println(callback.toString());
//        burse9.close();
//        burse10.close();
//        executor.shutdown();
    }

    private static void analyzeBurse(IBurse burse, int ind) throws InterruptedException {
        //System.out.println("Биржа" + ind + " принимает " + BurseTester.measureOrdersSentPerMs(burse) + " заявок в миллисекунду");
        //System.out.println("Биржа" + ind + " обрабатывает "+ BurseTester.measureOrdersProcessedPerMs(burse) + " заявок в миллисекунду");
        System.out.println();
        burse.close();
    }
}