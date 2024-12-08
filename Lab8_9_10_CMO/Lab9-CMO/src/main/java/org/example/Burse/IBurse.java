package org.example.Burse;

import org.example.Burse.Notification.BurseCallback;
import org.example.Burse.Notification.IBurseCallback;
import org.example.Burse.Order.OrderType;
import org.example.Burse.Currency.CurrencyPair;

public interface IBurse {

    void addOrder(IBurseCallback callback, CurrencyPair currencyPair, OrderType orderType, double priceLimit, int quantity);
    void close();

}
