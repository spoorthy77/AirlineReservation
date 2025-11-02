package com.mycompany.airlinereservation;

import java.sql.*;

/**
 * Enhanced diagnostic to understand the booking issue
 */
public class BookingDiagnosticV2 {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005";
    
    public static void main(String[] args) {
        System.out.println("🔍 Enhanced Booking Diagnostic V2\n");
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // Test SessionManager
            System.out.println("🔐 TESTING SESSION MANAGER:");
            System.out.println("  Current user: " + SessionManager.getInstance().getCurrentUser());
            System.out.println("  Is logged in: " + SessionManager.getInstance().isUserLoggedIn());
            
            // Simulate login
            System.out.println("\n  Simulating login for 'testuser'...");
            SessionManager.getInstance().setCurrentUser("testuser");
            System.out.println("  Current user after login: " + SessionManager.getInstance().getCurrentUser());
            
            // Check for any tickets with NULL username
            System.out.println("\n📋 TICKETS WITH NULL USERNAME:");
            String nullUserSql = "SELECT COUNT(*) as count FROM ticket WHERE username IS NULL";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(nullUserSql)) {
                if (rs.next()) {
                    System.out.println("  Count: " + rs.getInt("count") + " tickets have NULL username");
                }
            }
            
            // Check all unique usernames in ticket table
            System.out.println("\n📋 UNIQUE USERNAMES IN TICKET TABLE:");
            String uniqueUserSql = "SELECT DISTINCT username FROM ticket";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(uniqueUserSql)) {
                int count = 0;
                while (rs.next()) {
                    String username = rs.getString("username");
                    count++;
                    System.out.println("  " + count + ". Username: '" + username + "'");
                }
                if (count == 0) {
                    System.out.println("  No usernames found (all might be NULL)");
                }
            }
            
            // Check booking table status
            System.out.println("\n📦 BOOKING TABLE:");
            String bookingSql = "SELECT COUNT(*) as count FROM booking";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(bookingSql)) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    if (count == 0) {
                        System.out.println("  ❌ NO BOOKINGS IN TABLE! (This is the problem)");
                    } else {
                        System.out.println("  ✅ " + count + " bookings found");
                    }
                }
            }
            
            // Show sample ticket
            System.out.println("\n📋 SAMPLE LATEST TICKET:");
            String sampleSql = "SELECT pnr, username, flight_code, customer_name FROM ticket ORDER BY booking_date DESC LIMIT 1";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sampleSql)) {
                if (rs.next()) {
                    System.out.println("  PNR: " + rs.getString("pnr"));
                    System.out.println("  Username: " + rs.getString("username"));
                    System.out.println("  Flight Code: " + rs.getString("flight_code"));
                    System.out.println("  Customer Name: " + rs.getString("customer_name"));
                } else {
                    System.out.println("  No tickets found!");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
