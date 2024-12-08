package org.example.Burse.Order;

public class OrderEvent {
    private Order order;
    private boolean isCloseEvent;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean isCloseEvent() {
        return isCloseEvent;
    }

    public void setCloseEvent(boolean closeEvent) {
        this.isCloseEvent = closeEvent;
        this.order = null;
    }

}