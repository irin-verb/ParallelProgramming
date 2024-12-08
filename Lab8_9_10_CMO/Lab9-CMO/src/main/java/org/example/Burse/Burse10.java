package org.example.Burse;

import org.example.Burse.Notification.BurseCallback;
import org.example.Burse.Notification.IBurseCallback;
import org.example.Burse.Order.Order;
import org.example.Burse.Order.OrderType;
import org.example.Burse.Currency.CurrencyPair;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Burse10 implements IBurse {

    private ExecutorService ordersProcessor;
    private LinkedBlockingQueue<Order> processingOrders;

    public Burse10() {
        processingOrders = new LinkedBlockingQueue<>();
        ordersProcessor = Executors.newSingleThreadExecutor();
        ordersProcessor.submit(new Burse10OrdersProcessor(processingOrders));
    }

    @Override
    public void addOrder(IBurseCallback callback, CurrencyPair currencyPair, OrderType orderType, double priceLimit, int quantity) {
        Order order = new Order(callback, orderType, currencyPair, priceLimit, quantity);
        processingOrders.add(order);
    }

    @Override
    public void close() {
        ordersProcessor.shutdownNow();
        processingOrders.clear();
    }

}
