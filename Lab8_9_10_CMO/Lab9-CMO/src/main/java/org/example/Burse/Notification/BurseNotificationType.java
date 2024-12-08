package org.example.Burse.Notification;

public enum BurseNotificationType {
    PLACED("размещен на бирже"),
    CANCELLED("отменен"),
    PARTIAL_COMPLETED("частично выполнен"),
    FULLY_COMPLETED("полностью выполнен");

    private final String displayName;

    BurseNotificationType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }

}
