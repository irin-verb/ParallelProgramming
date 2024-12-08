package org.example.Burse.Order;

public enum OrderType {

    BUY("покупка"),
    SELL("продажа");

    private final String displayName;
    OrderType(String displayName) {
        this.displayName = displayName;
    }
    @Override
    public String toString() {
        return displayName;
    }

}
