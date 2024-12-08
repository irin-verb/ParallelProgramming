package org.example.Burse;

import org.example.Burse.Currency.CurrencyPair;
import org.example.Burse.Notification.BurseCallback;
import org.example.Burse.Notification.BurseNotifier;
import org.example.Burse.Notification.IBurseCallback;
import org.example.Burse.Order.Order;
import org.example.Burse.Order.OrderPriceAscComparator;
import org.example.Burse.Order.OrderPriceDecComparator;
import org.example.Burse.Order.OrderType;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class Burse9 implements IBurse {

        private final ReentrantLock lock = new ReentrantLock();
        private final Map<CurrencyPair, PriorityQueue<Order>> sellOrderBook = new ConcurrentHashMap<>();
        private final Map<CurrencyPair, PriorityQueue<Order>> buyOrderBook = new ConcurrentHashMap<>();

        @Override
        public void addOrder(IBurseCallback callback, CurrencyPair currencyPair, OrderType orderType, double priceLimit, int quantity) {
        lock.lock();
        try {
            Order order = new Order(callback, orderType, currencyPair, priceLimit, quantity);
            addOrderToBook(order);

            Optional<Order> matchingOrder = findMatchingOrder(order);
            if (!matchingOrder.isEmpty()) {
                Order siblingOrder = matchingOrder.get();
                executeTrade(order, siblingOrder);
            }
            callback.notifyUpdated();
        }
        finally {
            lock.unlock();
        }
    }

        private void addOrderToBook(Order order) {
        boolean isBuying = order.getType() == OrderType.BUY;
        Map<CurrencyPair, PriorityQueue<Order>> map = isBuying ? buyOrderBook : sellOrderBook;

        if (!map.containsKey(order.getPair())) {
            Comparator<Order> comparator = isBuying ? new OrderPriceDecComparator() : new OrderPriceAscComparator();
            map.put(order.getPair(), new PriorityQueue<>(comparator));
        }
        map.get(order.getPair()).add(order);

        BurseNotifier.notifyOrderPlaced(order);
    }

        private Optional<Order> findMatchingOrder(Order order) {
        PriorityQueue<Order> queue = (order.getType() == OrderType.BUY ? sellOrderBook : buyOrderBook).get(order.getPair());
        if (queue != null && !queue.isEmpty()) {
            for (Order matchingOrder : queue)
                if (canExecute(order, matchingOrder))
                    return Optional.of(matchingOrder);
        }
        return Optional.empty();
    }

        private boolean canExecute(Order order, Order matchingOrder) {
        return order.getType() == OrderType.BUY
                // покупатель готов купить по цене равной или ниже, чем минимальная цена продавца
                ? order.getPrice() >= matchingOrder.getPrice()
                // продавец готов продать по цене равной или выше, чем максимальная цена покупателя
                : order.getPrice() <= matchingOrder.getPrice();
    }

        private void executeTrade(Order order, Order matchingOrder) {
        lock.lock();
        try {
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
        finally {
            lock.unlock();
        }
    }

        private void removeOrder(Order order) {
        PriorityQueue<Order> queue  = (order.getType() == OrderType.BUY ? buyOrderBook : sellOrderBook).get(order.getPair());
        queue.remove(order);
    }

        @Override
        public void close() {
        lock.lock();
        try {
            notifyOrderCancelled(sellOrderBook);
            notifyOrderCancelled(buyOrderBook);
            sellOrderBook.clear();
            buyOrderBook.clear();
        }
        finally {
            lock.unlock();
        }
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
