package org.example.Burse.Notification;

import org.example.Burse.Order.Order;

public class BurseNotifier {

    public static void notifyOrderPartiallyExecuted(Order order, int count, double price) {
        order.getCallback().addNote(BurseNotificationType.PARTIAL_COMPLETED, count, price, order.toString());
    }

    public static void notifyOrderFullyExecuted(Order order, int count, double price) {
        order.getCallback().addNote(BurseNotificationType.FULLY_COMPLETED, count, price, order.toString());
    }

    public static void notifyOrderPlaced(Order order) {
        order.getCallback().addNote(BurseNotificationType.PLACED, 0, 0, order.toString());
    }

    public static void notifyOrderCancelled(Order order) {
        order.getCallback().addNote(BurseNotificationType.CANCELLED, 0,0, order.toString());
    }

}
