package com.apollocare.dao;

import com.apollocare.model.User;
import com.apollocare.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data Access Object for the users table.
 * All database operations for User are here.
 * No business logic — just SQL via PreparedStatement.
 */
public class UserDAO {

    /**
     * Inserts a new user into the database.
     * The password field must already be BCrypt-hashed before calling this.
     */
    public void saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, phone, address, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getRole() != null ? user.getRole() : "CUSTOMER");

            ps.executeUpdate();
        }
    }

    /**
     * Finds a user by email address.
     * Used for login validation and duplicate-email check during registration.
     * Returns null if no user found.
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds a user by primary key.
     * Used to reload the user object from the database if needed.
     * Returns null if not found.
     */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Returns the total number of CUSTOMER accounts.
     * Used by the admin dashboard for statistics.
     */
    public int countCustomers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Maps a ResultSet row to a User object. */
    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setRole(rs.getString("role"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            user.setCreatedAt(ts.toLocalDateTime());
        }
        return user;
    }
}
