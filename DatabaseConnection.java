package skillbarter.dao;

import java.sql.*;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:skillify.db";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                Statement st = connection.createStatement();
                st.execute("PRAGMA foreign_keys = ON");
                System.out.println("[DB] Connected to SQLite database.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC Driver not found. Add sqlite-jdbc.jar to classpath.", e);
            }
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    age INTEGER DEFAULT 0,
                    skill_offered TEXT,
                    skill_wanted TEXT NOT NULL,
                    user_type TEXT NOT NULL DEFAULT 'LEARNER',
                    points INTEGER DEFAULT 100,
                    total_rating_sum INTEGER DEFAULT 0,
                    total_ratings INTEGER DEFAULT 0,
                    badge TEXT DEFAULT 'NONE'
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS skill_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sender_id INTEGER NOT NULL,
                    sender_name TEXT NOT NULL,
                    receiver_id INTEGER NOT NULL,
                    receiver_name TEXT NOT NULL,
                    skill_wanted TEXT NOT NULL,
                    skill_offered TEXT,
                    comment TEXT,
                    status TEXT DEFAULT 'PENDING',
                    one_way INTEGER DEFAULT 0,
                    FOREIGN KEY (sender_id) REFERENCES users(id),
                    FOREIGN KEY (receiver_id) REFERENCES users(id)
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user1_id INTEGER NOT NULL,
                    user2_id INTEGER NOT NULL,
                    skill TEXT NOT NULL,
                    scheduled_time TEXT,
                    meeting_link TEXT,
                    status TEXT DEFAULT 'SCHEDULED',
                    one_way INTEGER DEFAULT 0,
                    user1_rating INTEGER DEFAULT 0,
                    user2_rating INTEGER DEFAULT 0,
                    FOREIGN KEY (user1_id) REFERENCES users(id),
                    FOREIGN KEY (user2_id) REFERENCES users(id)
                )
            """);

            System.out.println("[DB] Tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to initialize: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}