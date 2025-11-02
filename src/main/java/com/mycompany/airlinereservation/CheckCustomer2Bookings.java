package com.mycompany.airlinereservation;

import java.sql.*;

/**
 * Check specific customer bookings
 */
public class CheckCustomer2Bookings {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005";
    
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            System.out.println("🔍 Checking customer2 Bookings:\n");
            
            String sql = "SELECT t.pnr, t.flight_code, t.flight_name, t.source, t.destination, t.date_of_travel, p.amount "
                + "FROM ticket t "
                + "LEFT JOIN payments p ON t.pnr = p.pnr "
                + "WHERE t.username = 'customer2' "
                + "ORDER BY t.date_of_travel DESC";
            
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                try (ResultSet rs = pst.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        System.out.println("Booking " + count + ":");
                        System.out.println("  PNR: " + rs.getString("pnr"));
                        System.out.println("  Flight: " + rs.getString("flight_code") + " - " + rs.getString("flight_name"));
                        System.out.println("  Route: " + rs.getString("source") + " -> " + rs.getString("destination"));
                        System.out.println("  Date: " + rs.getDate("date_of_travel"));
                        System.out.println("  Amount: " + rs.getDouble("amount"));
                        System.out.println();
                    }
                    
                    if (count == 0) {
                        System.out.println("No bookings found for customer2");
                    } else {
                        System.out.println("Total: " + count + " bookings");
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
