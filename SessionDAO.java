package skillbarter.dao;

import skillbarter.model.Session;

import java.sql.*;
import java.util.*;

public class SessionDAO {

    public boolean createSession(Session session) {
        String sql = "INSERT INTO sessions (user1_id, user2_id, skill, scheduled_time, meeting_link, status, one_way, user1_rating, user2_rating) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, session.getUser1Id());
            ps.setInt(2, session.getUser2Id());
            ps.setString(3, session.getSkill());
            ps.setString(4, session.getScheduledTime());
            ps.setString(5, session.getMeetingLink());
            ps.setString(6, session.getStatus());
            ps.setInt(7, session.isOneWay() ? 1 : 0);
            ps.setInt(8, session.getUser1Rating());
            ps.setInt(9, session.getUser2Rating());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) session.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[SessionDAO] Create error: " + e.getMessage());
        }
        return false;
    }

    public List<Session> getSessionsByUser(int userId) {
        List<Session> list = new ArrayList<>();
        String sql = "SELECT * FROM sessions WHERE user1_id = ? OR user2_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[SessionDAO] GetByUser error: " + e.getMessage());
        }
        return list;
    }

    public Session getSessionById(int id) {
        String sql = "SELECT * FROM sessions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[SessionDAO] GetById error: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStatus(int sessionId, String status) {
        String sql = "UPDATE sessions SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SessionDAO] UpdateStatus error: " + e.getMessage());
            return false;
        }
    }

    public boolean saveUser1Rating(int sessionId, int rating) {
        String sql = "UPDATE sessions SET user1_rating = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rating);
            ps.setInt(2, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SessionDAO] SaveUser1Rating error: " + e.getMessage());
            return false;
        }
    }

    public boolean saveUser2Rating(int sessionId, int rating) {
        String sql = "UPDATE sessions SET user2_rating = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rating);
            ps.setInt(2, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SessionDAO] SaveUser2Rating error: " + e.getMessage());
            return false;
        }
    }

    private Session mapRow(ResultSet rs) throws SQLException {
        Session s = new Session();
        s.setId(rs.getInt("id"));
        s.setUser1Id(rs.getInt("user1_id"));
        s.setUser2Id(rs.getInt("user2_id"));
        s.setSkill(rs.getString("skill"));
        s.setScheduledTime(rs.getString("scheduled_time"));
        s.setMeetingLink(rs.getString("meeting_link"));
        s.setStatus(rs.getString("status"));
        s.setOneWay(rs.getInt("one_way") == 1);
        s.setUser1Rating(rs.getInt("user1_rating"));
        s.setUser2Rating(rs.getInt("user2_rating"));
        return s;
    }
}