package org.example.Burse.Notification;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class BurseCallback implements IBurseCallback {
    private ConcurrentLinkedQueue<BurseNotification> notifications;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition updated = lock.newCondition();

    public BurseCallback() { notifications = new ConcurrentLinkedQueue<>(); }

    @Override
    public void addNote(BurseNotificationType action, int count, double price, String message) {
        BurseNotification notification = new BurseNotification(action, count, price, message);
        notifications.add(notification);
    }

    @Override
    public BurseNotification getNote(int index) {
        BurseNotification note = null;
        Iterator<BurseNotification> iterator = notifications.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            BurseNotification current = iterator.next();
            if (i == index) {
                note = current;
                break;
            }
            i++;
        }
        if (note == null) return null;
        return new BurseNotification(note);
    }

    @Override
    public void waitUpdated() throws InterruptedException {
        lock.lock();
        try {
            updated.await();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void waitUpdated(int count, TimeUnit timeUnit) throws InterruptedException {
        lock.lock();
        try {
            updated.await(count, timeUnit);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void notifyUpdated() {
        lock.lock();
        try {
            updated.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getGeneralCount() {
        return notifications.stream()
                .mapToInt(BurseNotification::getCount)
                .sum();
    }

    public int getGeneralMoney() {
        return notifications.stream()
                .mapToInt(BurseNotification::getMoney)
                .sum();
    }

    @Override
    public String toString() {
        return notifications.stream()
                .map(BurseNotification::toString)
                .collect(Collectors.joining("\n"," _________________________________\n" + "[                                 ]\n","\n[_________________________________]\n"));
    }

}
