package skillbarter.thread;

import skillbarter.model.Notification;
import java.util.*;
import java.util.function.Consumer;

public class NotificationThread implements Runnable {

    private final NotificationQueue queue;
    private volatile boolean running = true;
    private final List<Consumer<Notification>> listeners = new ArrayList<>();

    public NotificationThread(NotificationQueue queue) {
        this.queue = queue;
    }

    public void addListener(Consumer<Notification> listener) {
        listeners.add(listener);
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("[NotificationThread] Started.");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Notification notification = queue.consume();
                if (notification != null) {
                    for (Consumer<Notification> listener : listeners) {
                        listener.accept(notification);
                    }
                }
            } catch (Exception e) {
                if (running) {
                    System.err.println("[NotificationThread] Error: " + e.getMessage());
                }
            }
        }
        System.out.println("[NotificationThread] Stopped.");
    }
}
