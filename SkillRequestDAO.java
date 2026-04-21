package skillbarter.dao;

import skillbarter.model.SkillRequest;

import java.sql.*;
import java.util.*;

public class SkillRequestDAO {

    public boolean addRequest(SkillRequest req) {
        String sql = "INSERT INTO skill_requests " +
                     "(sender_id, sender_name, receiver_id, receiver_name, skill_wanted, skill_offered, comment, status, one_way) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, req.getSenderId());
            ps.setString(2, req.getSenderName());
            ps.setInt(3, req.getReceiverId());
            ps.setString(4, req.getReceiverName());
            ps.setString(5, req.getSkillWanted());
            ps.setString(6, req.getSkillOffered());
            ps.setString(7, req.getComment());
            ps.setString(8, req.getStatus());
            ps.setInt(9, req.isOneWay() ? 1 : 0);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) req.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[RequestDAO] Add error: " + e.getMessage());
        }
        return false;
    }
    public List<SkillRequest> getRequestsReceivedBy(int userId) {
        List<SkillRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM skill_requests WHERE receiver_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RequestDAO] GetReceived error: " + e.getMessage());
        }
        return list;
    }
    public List<SkillRequest> getRequestsSentBy(int userId) {
        List<SkillRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM skill_requests WHERE sender_id = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RequestDAO] GetSent error: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStatus(int requestId, String status) {
        String sql = "UPDATE skill_requests SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RequestDAO] UpdateStatus error: " + e.getMessage());
            return false;
        }
    }

    public SkillRequest getRequestById(int id) {
        String sql = "SELECT * FROM skill_requests WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[RequestDAO] GetById error: " + e.getMessage());
        }
        return null;
    }

    private SkillRequest mapRow(ResultSet rs) throws SQLException {
        SkillRequest r = new SkillRequest();
        r.setId(rs.getInt("id"));
        r.setSenderId(rs.getInt("sender_id"));
        r.setSenderName(rs.getString("sender_name"));
        r.setReceiverId(rs.getInt("receiver_id"));
        r.setReceiverName(rs.getString("receiver_name"));
        r.setSkillWanted(rs.getString("skill_wanted"));
        r.setSkillOffered(rs.getString("skill_offered"));
        r.setComment(rs.getString("comment"));
        r.setStatus(rs.getString("status"));
        r.setOneWay(rs.getInt("one_way") == 1);
        return r;
    }
}