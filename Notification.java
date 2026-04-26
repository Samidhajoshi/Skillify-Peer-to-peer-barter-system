package skillbarter.model;

public class Notification {
    private int userId;
    private String message;
    private long timestamp;

    public Notification(int userId, String message) {
        this.userId = userId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public int getUserId() { return userId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[Notification for user " + userId + "]: " + message;
    }
}