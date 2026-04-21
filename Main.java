package skillbarter;

import skillbarter.dao.DatabaseConnection;
import skillbarter.model.Notification;
import skillbarter.service.SkillBarterService;
import skillbarter.thread.NotificationQueue;
import skillbarter.thread.NotificationThread;
import skillbarter.thread.SessionReminderThread;
import skillbarter.ui.DashboardFrame;
import skillbarter.ui.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        NotificationQueue notificationQueue = new NotificationQueue();
        SkillBarterService service = new SkillBarterService(notificationQueue);
        NotificationThread notifRunnable = new NotificationThread(notificationQueue);
        Thread notifThread = new Thread(notifRunnable, "NotificationThread");
        notifThread.setDaemon(true);
        notifThread.start();
        SessionReminderThread reminderThread = new SessionReminderThread(notificationQueue);
        reminderThread.start();

        SwingUtilities.invokeLater(() -> {

            LoginFrame loginFrame = new LoginFrame(service);

            notifRunnable.addListener((Notification n) -> {
                for (java.awt.Window w : java.awt.Window.getWindows()) {
                    if (w instanceof DashboardFrame && w.isVisible()) {
                        ((DashboardFrame) w).appendNotification(n.getMessage());
                    }
                }
            });

            loginFrame.setVisible(true);
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            notifRunnable.stop();
            reminderThread.stopReminder();
            DatabaseConnection.closeConnection();
            System.out.println("[Main] Skillify shutdown complete.");
        }));
    }
}