package com.mycompany.airlinereservation;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/airline_db"; // your DB name
    private static final String USER = "root";  // replace if different
    private static final String PASSWORD = "Spoorthi@2005";  // replace with your MySQL password

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
}
