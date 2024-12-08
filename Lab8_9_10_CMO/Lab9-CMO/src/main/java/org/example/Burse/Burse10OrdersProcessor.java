package org.example.Burse;

import org.example.Burse.Currency.CurrencyPair;
import org.example.Burse.Notification.BurseNotifier;
import org.example.Burse.Order.Order;
import org.example.Burse.Order.OrderPriceAscComparator;
import org.example.Burse.Order.OrderPriceDecComparator;
import org.example.Burse.Order.OrderType;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class Burse10OrdersProcessor implements Runnable {

    private LinkedBlockingQueue<Order> orders;

    private final Map<CurrencyPair, PriorityQueue<Order>> sellOrderBook = new HashMap<>();
    private final Map<CurrencyPair, PriorityQueue<Order>> buyOrderBook = new HashMap<>();


    public Burse10OrdersProcessor(LinkedBlockingQueue<Order> orders) {
        this.orders = orders;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                Order order = orders.take();
                processOrder(order);
            }
            catch (InterruptedException exception) {
                close();
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processOrder(Order order) {
        addOrderToBook(order);

        Order matchingOrder = findMatchingOrder(order);
        if (matchingOrder != null) {
            Order siblingOrder = matchingOrder;
            executeTrade(order, siblingOrder);
        }
        order.getCallback().notifyUpdated();
    }

    private void addOrderToBook(Order order) {
        boolean isBuying = order.getType() == OrderType.BUY;
        Map<CurrencyPair, PriorityQueue<Order>> map = isBuying ? buyOrderBook : sellOrderBook;
        Comparator<Order> comparator = isBuying ? new OrderPriceDecComparator() : new OrderPriceAscComparator();
        map.putIfAbsent(order.getPair(), new PriorityQueue<>(comparator));

        PriorityQueue<Order> orders = map.get(order.getPair());
        orders.add(order);

        BurseNotifier.notifyOrderPlaced(order);
    }

    private Order findMatchingOrder(Order order) {
        PriorityQueue<Order> list = (order.getType() == OrderType.BUY ? sellOrderBook : buyOrderBook).get(order.getPair());
        if (list != null && !list.isEmpty()) {
            for (Order matchingOrder : list)
                if (canExecute(order, matchingOrder))
                    return matchingOrder;
        }
        return null;
    }

    private boolean canExecute(Order order, Order matchingOrder) {
        return order.getType() == OrderType.BUY
                // покупатель готов купить по цене равной или ниже, чем минимальная цена продавца
                ? order.getPrice() >= matchingOrder.getPrice()
                // продавец готов продать по цене равной или выше, чем максимальная цена покупателя
                : order.getPrice() <= matchingOrder.getPrice();
    }

    private void executeTrade(Order order, Order matchingOrder) {
        // объем сделки
        int tradeQuantity = Math.min(order.getQuantity(), matchingOrder.getQuantity());
        double tradePrice = (order.getPrice() + matchingOrder.getPrice()) / 2;

        order.decreaseQuantity(tradeQuantity);
        matchingOrder.decreaseQuantity(tradeQuantity);

        Consumer<Order> processOrder = ord -> {
            int count = ord.getType() == OrderType.BUY ? tradeQuantity : -tradeQuantity;
            if (ord.isFilled()) {
                BurseNotifier.notifyOrderFullyExecuted(ord, count, tradePrice);
                removeOrder(ord);
            } else {
                BurseNotifier.notifyOrderPartiallyExecuted(ord, count, tradePrice);
            }
        };

        processOrder.accept(order);
        processOrder.accept(matchingOrder);
    }

    private void removeOrder(Order order) {
        PriorityQueue<Order> list  = (order.getType() == OrderType.BUY ? buyOrderBook : sellOrderBook).get(order.getPair());
        list.remove(order);
    }

    private void close() {
        notifyOrderCancelled(sellOrderBook);
        notifyOrderCancelled(buyOrderBook);
        sellOrderBook.clear();
        buyOrderBook.clear();
    }

    private void notifyOrderCancelled(Map<CurrencyPair, PriorityQueue<Order>> orderBook) {
        for (Map.Entry<CurrencyPair, PriorityQueue<Order>> entry : orderBook.entrySet()) {
            for (Order order : entry.getValue()) {
                BurseNotifier.notifyOrderCancelled(order);
                order.getCallback().notifyUpdated();
            }
        }
    }

}
