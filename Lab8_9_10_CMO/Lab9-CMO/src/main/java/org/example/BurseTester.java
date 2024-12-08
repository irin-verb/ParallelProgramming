package org.example;

import org.example.Burse.Currency.CurrencyPair;
import org.example.Burse.Currency.CurrencyType;
import org.example.Burse.IBurse;
import org.example.Burse.Notification.BurseCallback;
import org.example.Burse.Notification.IBurseCallback;
import org.example.Burse.Order.OrderType;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BurseTester {

    public static void sendRandomOrder(IBurse burse, IBurseCallback callback) {
        Random random = new Random();

        CurrencyType[] currencies = CurrencyType.values();
        CurrencyType baseCurrency = currencies[random.nextInt(currencies.length)];
        CurrencyType quoteCurrency;
        do {
            quoteCurrency = currencies[random.nextInt(currencies.length)];
        } while (quoteCurrency == baseCurrency);
        CurrencyPair pair = new CurrencyPair(baseCurrency, quoteCurrency);

        OrderType[] types = OrderType.values();
        OrderType type = types[random.nextInt(types.length)];

        int count = random.nextInt(500 + 1);
        double price = Math.round(random.nextDouble() * 1000 * 100.0) / 100.0;

        burse.addOrder(callback, pair, type, price, count);
    }

    public static void processOrder(IBurse burse, IBurseCallback callback, CurrencyPair currencyPair,
                                    OrderType orderType, double priceLimit, int quantity) throws InterruptedException {
        try {
            CountDownLatch latch = new CountDownLatch(1);

            new Thread(() -> {
                try {
                    callback.waitUpdated(1000, TimeUnit.MILLISECONDS);
                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }).start();

            new Thread(() ->  {
                burse.addOrder(callback, currencyPair, orderType, priceLimit, quantity);
            }).start();

            latch.await();
        }
        catch (RuntimeException ex) {
            throw new InterruptedException();
        }
    }

    public static void processRandomOrder(IBurse burse, IBurseCallback callback) throws InterruptedException {
        try {
            CountDownLatch latch = new CountDownLatch(1);

            new Thread(() -> {
                try {
                    callback.waitUpdated(1000, TimeUnit.MILLISECONDS);
                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }).start();

            new Thread(() ->  {
                BurseTester.sendRandomOrder(burse, callback);
            }).start();

            latch.await();
        }
        catch (RuntimeException ex) {
            throw new InterruptedException();
        }
    }

    public static void processBurseClosing(IBurse burse, IBurseCallback callback) throws InterruptedException {
        try {
            processRandomOrder(burse, callback);
            CountDownLatch latch = new CountDownLatch(1);

            new Thread(() -> {
                try {
                    callback.waitUpdated(1000, TimeUnit.MILLISECONDS);
                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }).start();

            new Thread(() ->  {
                burse.close();
            }).start();

            latch.await();
        }
        catch (RuntimeException ex) {
            throw new InterruptedException();
        }
    }

    public static int getGeneralCount(BurseCallback[] callbacks) {
        int count = 0;
        for (int i = 0; i < callbacks.length; i++) {
            count += callbacks[i].getGeneralCount();
        }
        return count;
    }

    public static double getGeneralMoney(BurseCallback[] callbacks) {
        double count = 0;
        for (int i = 0; i < callbacks.length; i++) {
            count += callbacks[i].getGeneralMoney();
        }
        return count;
    }

    public static double measureOrdersSentPerMs(IBurse burse, IBurseCallback[] callbacks, int ordersPerCallback) {
        double startTime = System.currentTimeMillis();

        for (int i = 0; i < callbacks.length; ++i)
            for (int j = 0; j < ordersPerCallback; ++j)
                sendRandomOrder(burse, callbacks[i]);

        double endTime = System.currentTimeMillis();
        double executionTime = (endTime - startTime);

        return (callbacks.length * ordersPerCallback / executionTime);
    }

    public static double measureOrdersProcessedPerMs(IBurse burse, IBurseCallback[] callbacks, int ordersPerCallback) throws InterruptedException {
        double startTime = System.currentTimeMillis();

        for (int i = 0; i < callbacks.length; ++i)
            for (int j = 0; j < ordersPerCallback; ++j)
                processRandomOrder(burse, callbacks[i]);

        double endTime = System.currentTimeMillis();
        double executionTime = (endTime - startTime);

        return (callbacks.length * ordersPerCallback / executionTime);
    }

    public static void processRandomOrdersByThreads(IBurse burse, IBurseCallback[] callbacks, int ordersPerCallback, int threadsNumber) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadsNumber);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadsNumber - 1);

        for (int i = 0; i < threadsNumber; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < callbacks.length; j++)
                        for (int k = 0; k < ordersPerCallback; k++)
                            BurseTester.processRandomOrder(burse, callbacks[j]);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        endLatch.await();
        //Thread.sleep(1500);
        executor.shutdown();
    }

}
