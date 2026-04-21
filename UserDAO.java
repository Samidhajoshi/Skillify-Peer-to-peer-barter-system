package skillbarter.dao;

import skillbarter.model.User;
import skillbarter.model.UserType;

import java.sql.*;
import java.util.*;

public class UserDAO {

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, age, skill_offered, skill_wanted, " +
                     "user_type, points, total_rating_sum, total_ratings, badge) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getAge());
            ps.setString(5, user.getSkillOffered());
            ps.setString(6, user.getSkillWanted());
            ps.setString(7, user.getUserType().name());
            ps.setInt(8, user.getPoints());
            ps.setInt(9, user.getTotalRatingSum());
            ps.setInt(10, user.getTotalRatings());
            ps.setString(11, user.getBadge());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) user.setId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Register error: " + e.getMessage());
        }
        return false;
    }

    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[UserDAO] Login error: " + e.getMessage());
        }
        return null;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[UserDAO] Get by ID error: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY (CAST(total_rating_sum AS REAL) / CASE WHEN total_ratings = 0 THEN 1 ELSE total_ratings END) DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] Get all error: " + e.getMessage());
        }
        return list;
    }

    /**
     * Search barter users where skillOffered matches the skill the current user wants
     * AND skillWanted matches the skill the current user offers.
     * For learners searching for teachers: just match skill_offered.
     * Results sorted by average rating descending.
     */
    public List<User> searchBarterUsers(String skillIWant, String skillIOffer) {
        List<User> list = new ArrayList<>();
        String sql;
        PreparedStatement ps;
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (skillIOffer == null || skillIOffer.isBlank()) {
                // LEARNER: just find someone who offers the skill
                sql = "SELECT * FROM users " +
                      "WHERE skill_offered LIKE ? " +
                      "AND user_type = 'BARTER_USER' " +
                      "ORDER BY (CAST(total_rating_sum AS REAL) / CASE WHEN total_ratings = 0 THEN 1 ELSE total_ratings END) DESC";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + skillIWant + "%");
            } else {
                // BARTER_USER: find someone who wants what I offer AND offers what I want
                sql = "SELECT * FROM users " +
                      "WHERE skill_offered LIKE ? " +
                      "AND skill_wanted LIKE ? " +
                      "AND user_type = 'BARTER_USER' " +
                      "ORDER BY (CAST(total_rating_sum AS REAL) / CASE WHEN total_ratings = 0 THEN 1 ELSE total_ratings END) DESC";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + skillIWant + "%");
                ps.setString(2, "%" + skillIOffer + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] Search error: " + e.getMessage());
        }
        return list;
    }

    public boolean updatePoints(int userId, int newPoints) {
        String sql = "UPDATE users SET points = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newPoints);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Update points error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add a rating to a user and recalculate badge.
     * Badge rules: avg > 40 = BRONZE, avg > 50 = SILVER, avg > 70 = GOLD
     */
    public boolean addRating(int userId, int rating) {
        String selectSql = "SELECT total_rating_sum, total_ratings FROM users WHERE id = ?";
        String updateSql = "UPDATE users SET total_rating_sum = ?, total_ratings = ?, badge = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement sel = conn.prepareStatement(selectSql);
             PreparedStatement upd = conn.prepareStatement(updateSql)) {

            sel.setInt(1, userId);
            ResultSet rs = sel.executeQuery();
            if (rs.next()) {
                int sum = rs.getInt("total_rating_sum") + rating;
                int count = rs.getInt("total_ratings") + 1;
                double avg = (double) sum / count;

                String badge;
                if (avg > 70) badge = "GOLD";
                else if (avg > 50) badge = "SILVER";
                else if (avg > 40) badge = "BRONZE";
                else badge = "NONE";

                upd.setInt(1, sum);
                upd.setInt(2, count);
                upd.setString(3, badge);
                upd.setInt(4, userId);
                return upd.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Add rating error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Upgrade a LEARNER to BARTER_USER after they have a skill to offer.
     */
    public boolean upgradeToBarterUser(int userId, String skillOffered) {
        String sql = "UPDATE users SET user_type = 'BARTER_USER', skill_offered = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, skillOffered);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Upgrade error: " + e.getMessage());
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setAge(rs.getInt("age"));
        u.setSkillOffered(rs.getString("skill_offered"));
        u.setSkillWanted(rs.getString("skill_wanted"));
        u.setUserType(UserType.valueOf(rs.getString("user_type")));
        u.setPoints(rs.getInt("points"));
        u.setTotalRatingSum(rs.getInt("total_rating_sum"));
        u.setTotalRatings(rs.getInt("total_ratings"));
        u.setBadge(rs.getString("badge"));
        return u;
    }
}