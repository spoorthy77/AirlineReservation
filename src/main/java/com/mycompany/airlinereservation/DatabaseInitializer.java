package com.mycompany.airlinereservation;

import java.sql.Connection;
import java.sql.Statement;

/**
 * DatabaseInitializer.java - Ensures all required tables exist
 * This runs once at application startup
 */
public class DatabaseInitializer {
    
    public static void initializeDatabase() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("❌ DatabaseInitializer: Connection failed!");
                return;
            }
            
            System.out.println("🔧 DatabaseInitializer: Initializing database...");
            
            try (Statement stmt = conn.createStatement()) {
                // Drop and recreate booking table to ensure correct schema
                try {
                    stmt.execute("DROP TABLE IF EXISTS booking");
                    System.out.println("🔄 Dropped old booking table (if it existed)");
                } catch (Exception e) {
                    // Ignore - table might not exist
                }
                
                // Create airlines table
                String createAirlines = "CREATE TABLE IF NOT EXISTS airlines (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "airline_name VARCHAR(255) NOT NULL UNIQUE," +
                        "rating DECIMAL(2,1) DEFAULT NULL," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                stmt.execute(createAirlines);
                System.out.println("✅ Airlines table ready");
                
                // Create flight table
                String createFlight = "CREATE TABLE IF NOT EXISTS flight (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "flight_code VARCHAR(50) NOT NULL UNIQUE," +
                        "flight_name VARCHAR(255)," +
                        "source VARCHAR(100) NOT NULL," +
                        "destination VARCHAR(100) NOT NULL," +
                        "airline_id INT NOT NULL," +
                        "departure_time DATETIME," +
                        "arrival_time DATETIME," +
                        "price INT," +
                        "seats_available INT DEFAULT 100," +
                        "total_seats INT DEFAULT 100," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (airline_id) REFERENCES airlines(id) ON DELETE CASCADE)";
                stmt.execute(createFlight);
                System.out.println("✅ Flight table ready");
                
                // Create ticket table
                String createTicket = "CREATE TABLE IF NOT EXISTS ticket (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "pnr VARCHAR(50) NOT NULL UNIQUE," +
                        "flight_code VARCHAR(50) NOT NULL," +
                        "username VARCHAR(100) NOT NULL," +
                        "customer_aadhar VARCHAR(12)," +
                        "customer_name VARCHAR(255)," +
                        "nationality VARCHAR(100)," +
                        "address TEXT," +
                        "gender VARCHAR(20)," +
                        "source VARCHAR(100)," +
                        "destination VARCHAR(100)," +
                        "flight_name VARCHAR(255)," +
                        "date_of_travel DATE," +
                        "passenger_name VARCHAR(255)," +
                        "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (flight_code) REFERENCES flight(flight_code) ON DELETE CASCADE," +
                        "INDEX (username))";
                stmt.execute(createTicket);
                System.out.println("✅ Ticket table ready");
                
                // Create booking table
                String createBooking = "CREATE TABLE IF NOT EXISTS booking (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "pnr VARCHAR(50) NOT NULL UNIQUE," +
                        "username VARCHAR(100) NOT NULL," +
                        "flight_code VARCHAR(50) NOT NULL," +
                        "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "date_of_travel DATE NOT NULL," +
                        "class VARCHAR(50) NOT NULL," +
                        "aadhaar VARCHAR(12) NOT NULL," +
                        "status VARCHAR(50) DEFAULT 'Confirmed'," +
                        "FOREIGN KEY (flight_code) REFERENCES flight(flight_code) ON DELETE CASCADE)";
                stmt.execute(createBooking);
                System.out.println("✅ Booking table ready");
                
                // Create payments table
                String createPayments = "CREATE TABLE IF NOT EXISTS payments (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "pnr VARCHAR(50) NOT NULL," +
                        "amount DOUBLE NOT NULL," +
                        "payment_method VARCHAR(100)," +
                        "payment_status VARCHAR(50)," +
                        "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "INDEX (pnr))";
                stmt.execute(createPayments);
                System.out.println("✅ Payments table ready");
                
                // Create boarding_pass table
                String createBoardingPass = "CREATE TABLE IF NOT EXISTS boarding_pass (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "pnr VARCHAR(50) NOT NULL UNIQUE," +
                        "passenger_name VARCHAR(255)," +
                        "flight_code VARCHAR(50)," +
                        "boarding_time VARCHAR(50)," +
                        "gate_number VARCHAR(10)," +
                        "seat_number VARCHAR(10)," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                stmt.execute(createBoardingPass);
                System.out.println("✅ Boarding Pass table ready");
                
                System.out.println("✅ DatabaseInitializer: Database initialized successfully!");
                
            } catch (Exception e) {
                System.out.println("⚠️ DatabaseInitializer: Some tables may already exist - " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ DatabaseInitializer: Failed - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
