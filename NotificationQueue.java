package skillbarter.thread;

import skillbarter.model.Notification;
import java.util.*;

public class NotificationQueue {

    private final Queue<Notification> queue = new LinkedList<>();
    private static final int MAX_SIZE = 50;

    public synchronized void produce(Notification notification) {
        while (queue.size() >= MAX_SIZE) {
            try {
                System.out.println("[Queue] Full. Producer waiting...");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        queue.add(notification);
        System.out.println("[Queue] Notification added: " + notification.getMessage());
        notifyAll();
    }

    public synchronized Notification consume() {
        while (queue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        Notification n = queue.poll();
        notifyAll();
        return n;
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized int size() {
        return queue.size();
    }
}
