package org.example.Burse.Order;

import org.example.Burse.Currency.CurrencyPair;
import org.example.Burse.Notification.BurseCallback;
import org.example.Burse.Notification.IBurseCallback;

import java.util.concurrent.atomic.AtomicInteger;

public class Order {

    private final IBurseCallback callback;
    private final OrderType type;
    private final CurrencyPair pair;
    private final double price;
    private final AtomicInteger quantity;

    public Order(IBurseCallback callback, OrderType type, CurrencyPair pair, double priceLimit, int quantity) {
        this.callback = callback;
        this.type = type;
        this.pair = pair;
        this.price = priceLimit;
        this.quantity = new AtomicInteger(quantity);
    }

    public IBurseCallback getCallback() { return callback; }

    public OrderType getType() {
        return type;
    }

    public CurrencyPair getPair() { return pair; }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void decreaseQuantity(int amount) {
         if (amount <= this.quantity.get())
             quantity.addAndGet(-amount);
    }

    public boolean isFilled() {
        return quantity.get() == 0;
    }

    @Override
    public String toString() {
        return type.toString() + " за " + price + " " + pair.toString()
                + (type == OrderType.BUY ? " и меньше, " : " и больше, ")
                + quantity  + " шт" ;

    }
}
