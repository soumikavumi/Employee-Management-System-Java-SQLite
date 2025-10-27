package com.soumika.employees.dao;

import com.soumika.employees.db.Database;
import com.soumika.employees.model.User;

import java.sql.*;

public class UserDAO {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, role FROM users WHERE username = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                    );
                }
                return null;
            }
        }
    }

    public boolean createUser(String username, String passwordHash, String role) throws SQLException {
        String sql = "INSERT INTO users(username, password_hash, role) VALUES(?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role == null ? "USER" : role);
            return ps.executeUpdate() == 1;
        }
    }
}
