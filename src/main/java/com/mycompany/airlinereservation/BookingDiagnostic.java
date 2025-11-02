package com.mycompany.airlinereservation;

import java.sql.*;

/**
 * Diagnostic tool to check booking data in the database
 * Run this to verify tickets are being saved correctly
 */
public class BookingDiagnostic {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005";
    
    public static void main(String[] args) {
        System.out.println("🔍 Starting Booking Diagnostic...\n");
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // Check ticket table
            System.out.println("📋 TICKET TABLE CONTENTS:");
            String ticketSql = "SELECT pnr, username, flight_code, customer_name, date_of_travel, booking_date FROM ticket ORDER BY booking_date DESC LIMIT 10";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(ticketSql)) {
                if (!rs.next()) {
                    System.out.println("❌ NO TICKETS FOUND!");
                } else {
                    do {
                        System.out.println("  PNR: " + rs.getString("pnr") + 
                                         " | Username: " + rs.getString("username") + 
                                         " | Flight: " + rs.getString("flight_code") + 
                                         " | Name: " + rs.getString("customer_name") +
                                         " | Date: " + rs.getDate("date_of_travel") +
                                         " | Booked: " + rs.getTimestamp("booking_date"));
                    } while (rs.next());
                }
            }
            
            System.out.println("\n📦 BOOKING TABLE CONTENTS:");
            String bookingSql = "SELECT pnr, username, flight_code, date_of_travel, status FROM booking ORDER BY booking_date DESC LIMIT 10";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(bookingSql)) {
                if (!rs.next()) {
                    System.out.println("❌ NO BOOKINGS FOUND!");
                } else {
                    do {
                        System.out.println("  PNR: " + rs.getString("pnr") + 
                                         " | Username: " + rs.getString("username") + 
                                         " | Flight: " + rs.getString("flight_code") + 
                                         " | Date: " + rs.getDate("date_of_travel") +
                                         " | Status: " + rs.getString("status"));
                    } while (rs.next());
                }
            }
            
            System.out.println("\n💳 PAYMENTS TABLE CONTENTS:");
            String paymentSql = "SELECT pnr, amount, payment_status FROM payments ORDER BY transaction_date DESC LIMIT 10";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(paymentSql)) {
                if (!rs.next()) {
                    System.out.println("❌ NO PAYMENTS FOUND!");
                } else {
                    do {
                        System.out.println("  PNR: " + rs.getString("pnr") + 
                                         " | Amount: " + rs.getDouble("amount") + 
                                         " | Status: " + rs.getString("payment_status"));
                    } while (rs.next());
                }
            }
            
            System.out.println("\n✈️ FLIGHTS TABLE (Seat Status):");
            String flightSql = "SELECT flight_code, flight_name, seats_available, total_seats FROM flight LIMIT 5";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(flightSql)) {
                if (!rs.next()) {
                    System.out.println("❌ NO FLIGHTS FOUND!");
                } else {
                    do {
                        System.out.println("  " + rs.getString("flight_code") + " (" + rs.getString("flight_name") + "): " + 
                                         rs.getInt("seats_available") + "/" + rs.getInt("total_seats") + " seats available");
                    } while (rs.next());
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
