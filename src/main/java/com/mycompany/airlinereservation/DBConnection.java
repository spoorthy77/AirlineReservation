package com.mycompany.airlinereservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    // Database credentials - Replace these with your actual values
    // For security, consider using environment variables or a configuration file
    private static final String URL = "jdbc:mysql://localhost:3306/airline_db"; // your DB name
    private static final String USER = "root";  // replace if different
    private static final String PASSWORD = "Spoorthi@2005";  // CHANGE THIS: replace with your MySQL password

    // ✅ This is the method all your other files (BookFlight, JourneyDetails, etc.) will use
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully!");
        } catch (Exception e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
        }
        return conn;
    }

    // Method to create the database if it doesn't exist
    public static void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // SQL statement to create the database
            String sql = "CREATE DATABASE IF NOT EXISTS airline_db";
            
            // Execute the SQL statement
            stmt.executeUpdate(sql);
            System.out.println("✅ Database created or already exists.");
            
        } catch (SQLException e) {
            System.out.println("❌ Error creating database: " + e.getMessage());
        }
    }
}
