package org.example.Burse.Notification;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public interface IBurseCallback {
    public void addNote(BurseNotificationType action, int count, double price, String message);
    public BurseNotification getNote(int index);
    public void waitUpdated() throws InterruptedException;
    public void waitUpdated(int count, TimeUnit timeUnit) throws InterruptedException;
    public void notifyUpdated();
    @Override
    public String toString();
}
