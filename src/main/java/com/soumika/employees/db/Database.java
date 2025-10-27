package com.soumika.employees.db;

import java.sql.*;
import java.nio.file.*;
import java.io.InputStream;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:employee.db";

    static {
        init();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void init() {
        try (Connection conn = getConnection()) {
            // run schema.sql
            try (InputStream in = Database.class.getClassLoader().getResourceAsStream("schema.sql")) {
                if (in != null) {
                    String sql = new String(in.readAllBytes());
                    try (Statement st = conn.createStatement()) { st.executeUpdate(sql); }
                }
            }
            // ensure admin user exists
            String check = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
            try (PreparedStatement ps = conn.prepareStatement(check);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String ins = "INSERT INTO users(username, password_hash, role) VALUES(?,?,?)";
                    try (PreparedStatement insPs = conn.prepareStatement(ins)) {
                        insPs.setString(1, "admin");
                        insPs.setString(2, com.soumika.employees.util.PasswordUtil.sha256("admin123"));
                        insPs.setString(3, "ADMIN");
                        insPs.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("DB init error: " + e.getMessage());
        }
    }
}
