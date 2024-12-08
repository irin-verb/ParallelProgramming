package org.example.Burse.Order;

import java.util.Comparator;

public class OrderPriceDecComparator implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        return Double.compare(o2.getPrice(), o1.getPrice());
    }

}
