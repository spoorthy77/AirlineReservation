package com.mycompany.airlinereservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Seeds initial data into the database at application startup.
 * Ensures the `users` table exists and that an Admin account is present.
 */
public class DataSeeder {

    // The admin password chosen now. Change if you want a different default.
    // NOTE: This value will be printed in console when seeding so the operator knows it.
    public static final String DEFAULT_ADMIN_USERNAME = "admin";
    // Changed to a simpler default for development/testing so you can login easily.
    // The value will be hashed before being stored in the database.
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123"; // development default

    public static void seedAdmin() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("DataSeeder: DB connection is null; skipping seeding.");
                return;
            }

            // Ensure table exists (simple DDL). This mirrors init_users.sql but is safe to run.
            String createTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(100) NOT NULL UNIQUE,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "role VARCHAR(20) NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            try (PreparedStatement ps = conn.prepareStatement(createTable)) {
                ps.execute();
            }

            // Ensure password column is wide enough for BCrypt hashes
            try {
                conn.createStatement().execute("ALTER TABLE users MODIFY password VARCHAR(255) NOT NULL");
                System.out.println("DataSeeder: Ensured password column is VARCHAR(255).");
            } catch (SQLException alterEx) {
                // If ALTER fails (for example, table doesn't exist yet), ignore — createTable will handle it.
                // But log in case of unexpected failures.
                System.out.println("DataSeeder: ALTER TABLE attempt message: " + alterEx.getMessage());
            }

            // Check if admin exists
            String selectSql = "SELECT id, password FROM users WHERE username = ? LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, DEFAULT_ADMIN_USERNAME);
                try (ResultSet rs = ps.executeQuery()) {
                    String hashed = PasswordUtils.hashPassword(DEFAULT_ADMIN_PASSWORD);
                    if (rs.next()) {
                        // Update password and role to ensure admin has correct role
                        int id = rs.getInt("id");
                        String updateSql = "UPDATE users SET password = ?, role = 'Admin' WHERE id = ?";
                        try (PreparedStatement ups = conn.prepareStatement(updateSql)) {
                            ups.setString(1, hashed);
                            ups.setInt(2, id);
                            ups.executeUpdate();
                            System.out.println("DataSeeder: Updated existing admin user with new hashed password.");
                        }
                    } else {
                        // Insert admin
                        String insertSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'Admin')";
                        try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                            ins.setString(1, DEFAULT_ADMIN_USERNAME);
                            ins.setString(2, hashed);
                            ins.executeUpdate();
                            System.out.println("DataSeeder: Inserted admin user.");
                        }
                    }
                    System.out.println("DataSeeder: Admin username='" + DEFAULT_ADMIN_USERNAME + "' password='" + DEFAULT_ADMIN_PASSWORD + "' (hashed in DB)");
                }
            }

        } catch (SQLException ex) {
            System.err.println("DataSeeder: SQL error during seeding: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
