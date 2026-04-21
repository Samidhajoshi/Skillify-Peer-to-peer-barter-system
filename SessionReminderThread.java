package skillbarter.thread;

import skillbarter.model.Notification;

public class SessionReminderThread extends Thread {

    private final NotificationQueue notificationQueue;
    private volatile boolean running = true;
    private static final long CHECK_INTERVAL_MS = 30_000; // 30 seconds

    public SessionReminderThread(NotificationQueue notificationQueue) {
        super("SessionReminderThread");
        this.notificationQueue = notificationQueue;
        setDaemon(true);
    }

    public void stopReminder() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        System.out.println("[SessionReminderThread] Started. Checking every 30 seconds.");
        while (running && !isInterrupted()) {
            try {
                checkUpcomingSessions();
                Thread.sleep(CHECK_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("[SessionReminderThread] Stopped.");
    }

    private void checkUpcomingSessions() {
        // In a real system this would query the DB and push reminders.
        // For now just log to confirm thread is alive.
        System.out.println("[SessionReminderThread] Checking upcoming sessions...");
    }
}
