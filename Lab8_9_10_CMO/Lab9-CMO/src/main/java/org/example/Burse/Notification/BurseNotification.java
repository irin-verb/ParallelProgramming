package org.example.Burse.Notification;

public class BurseNotification {

    private final BurseNotificationType type;
    private final int count;
    private final double price;
    private final int money;
    private final String message;

    public BurseNotification(BurseNotificationType type, int count, double price, String message) {
        this.type = type;
        this.count = count;
        this.price = price;
        this.money = (int)(count * price);
        this.message = message;
    }

    public BurseNotification(BurseNotification note) {
        this.type = note.type;
        this.count = note.count;
        this.price = note.price;
        this.money = note.money;
        this.message = note.message;
    }

    public BurseNotificationType getType() { return type; }
    public int getCount() { return count; }
    public double getPrice() { return price; }
    public int getMoney() { return money; }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Ордер" + " [" + message + "]");
        result.append(" " + type.toString());
        if (type == BurseNotificationType.FULLY_COMPLETED || type == BurseNotificationType.PARTIAL_COMPLETED) {
            result.append(", " + Math.abs(count) + " шт за цену " + price + ", итого " + money);
        }
        return result.toString();
    }

}
