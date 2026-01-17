-- ============================================================================
-- AIRLINE RESERVATION SYSTEM - COMPLETE DATABASE SCHEMA
-- ============================================================================
-- This script creates a complete database matching the AirlineReservation 
-- Java project structure (DatabaseInitializer.java & DataSeeder.java)
-- Run this in MySQL to set up a fresh database.
-- ============================================================================

-- Drop existing database and create fresh
DROP DATABASE IF EXISTS airline_db;
CREATE DATABASE airline_db;
USE airline_db;

-- ============================================================================
-- TABLE 1: USERS (from DataSeeder.java)
-- Stores user accounts (admin, staff, customers)
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,           -- Stores BCrypt hashed passwords
  role VARCHAR(20) NOT NULL,                -- 'Admin', 'Staff', 'Customer'
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- TABLE 2: AIRLINES (from DatabaseInitializer.java line 30-35)
-- Stores airline company information
-- ============================================================================
CREATE TABLE IF NOT EXISTS airlines (
  id INT AUTO_INCREMENT PRIMARY KEY,
  airline_name VARCHAR(255) NOT NULL UNIQUE,
  rating DECIMAL(2,1) DEFAULT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- TABLE 3: FLIGHT (from DatabaseInitializer.java line 38-52)
-- Stores flight information with airline reference
-- ============================================================================
CREATE TABLE IF NOT EXISTS flight (
  id INT AUTO_INCREMENT PRIMARY KEY,
  flight_code VARCHAR(50) NOT NULL UNIQUE,
  flight_name VARCHAR(255),
  source VARCHAR(100) NOT NULL,
  destination VARCHAR(100) NOT NULL,
  airline_id INT NOT NULL,
  departure_time DATETIME,
  arrival_time DATETIME,
  price INT,
  seats_available INT DEFAULT 100,
  total_seats INT DEFAULT 100,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (airline_id) REFERENCES airlines(id) ON DELETE CASCADE
);

-- ============================================================================
-- TABLE 4: TICKET (from DatabaseInitializer.java line 55-75)
-- Stores detailed ticket information with customer details
-- NOTE: Created BEFORE booking table (no FK to users to avoid dependency issues)
-- ============================================================================
CREATE TABLE IF NOT EXISTS ticket (
  id INT AUTO_INCREMENT PRIMARY KEY,
  pnr VARCHAR(50) NOT NULL UNIQUE,
  flight_code VARCHAR(50) NOT NULL,
  username VARCHAR(100) NOT NULL,
  customer_aadhar VARCHAR(12),
  customer_name VARCHAR(255),
  nationality VARCHAR(100),
  address TEXT,
  gender VARCHAR(20),
  source VARCHAR(100),
  destination VARCHAR(100),
  flight_name VARCHAR(255),
  date_of_travel DATE,
  passenger_name VARCHAR(255),
  booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (flight_code) REFERENCES flight(flight_code) ON DELETE CASCADE,
  INDEX (username)
);

-- ============================================================================
-- TABLE 5: BOOKING (from DatabaseInitializer.java line 78-90)
-- Stores flight booking/reservation information
-- ============================================================================
CREATE TABLE IF NOT EXISTS booking (
  id INT AUTO_INCREMENT PRIMARY KEY,
  pnr VARCHAR(50) NOT NULL UNIQUE,
  username VARCHAR(100) NOT NULL,
  flight_code VARCHAR(50) NOT NULL,
  booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  date_of_travel DATE NOT NULL,
  class VARCHAR(50) NOT NULL,
  aadhaar VARCHAR(12) NOT NULL,
  status VARCHAR(50) DEFAULT 'Confirmed',
  FOREIGN KEY (flight_code) REFERENCES flight(flight_code) ON DELETE CASCADE
);

-- ============================================================================
-- TABLE 6: PAYMENTS (from DatabaseInitializer.java line 93-101)
-- Stores payment transaction records
-- ============================================================================
CREATE TABLE IF NOT EXISTS payments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  pnr VARCHAR(50) NOT NULL,
  amount DOUBLE NOT NULL,
  payment_method VARCHAR(100),
  payment_status VARCHAR(50),
  transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX (pnr)
);

-- ============================================================================
-- TABLE 7: BOARDING_PASS (from DatabaseInitializer.java line 104-113)
-- Stores generated boarding pass information
-- ============================================================================
CREATE TABLE IF NOT EXISTS boarding_pass (
  id INT AUTO_INCREMENT PRIMARY KEY,
  pnr VARCHAR(50) NOT NULL UNIQUE,
  passenger_name VARCHAR(255),
  flight_code VARCHAR(50),
  boarding_time VARCHAR(50),
  gate_number VARCHAR(10),
  seat_number VARCHAR(10),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- TABLE 8: BOOKINGS (from init_users.sql - Legacy simple booking table)
-- Simple bookings table for quick inserts (used by ViewFlightDetails.java)
-- ============================================================================
CREATE TABLE IF NOT EXISTS bookings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  flight_code VARCHAR(50) NOT NULL,
  booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  amount DOUBLE NOT NULL
);

-- ============================================================================
-- TABLE 9: CUSTOMER (Optional - for customer management)
-- Stores customer personal information
-- ============================================================================
CREATE TABLE IF NOT EXISTS customer (
  customer_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  nationality VARCHAR(100),
  phone VARCHAR(20),
  address TEXT,
  aadhar_no VARCHAR(12) UNIQUE,
  gender VARCHAR(20),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- SAMPLE DATA INSERTION
-- ============================================================================

-- Insert default admin user (password: admin123, will be hashed by Java app)


-- Insert sample staff user


-- Insert sample customer users
INSERT INTO users (username, password, role)
VALUES 
  ('john_doe', 'password123', 'Customer'),
  ('jane_smith', 'password123', 'Customer'),
  ('raj_kumar', 'password123', 'Customer')
ON DUPLICATE KEY UPDATE role = 'Customer';

-- Insert sample airlines
INSERT INTO airlines (airline_name, rating) VALUES
  ('Air India', 4.2),
  ('SpiceJet', 3.8),
  ('IndiGo', 4.5),
  ('Vistara', 4.6),
  ('GoAir', 3.5),
  ('AirAsia India', 3.9)
ON DUPLICATE KEY UPDATE rating = VALUES(rating);

-- Insert sample flights
INSERT INTO flight (flight_code, flight_name, source, destination, airline_id, departure_time, arrival_time, price, seats_available, total_seats) VALUES
  ('AI302', 'Air India Express', 'Delhi', 'Mumbai', 1, '2026-01-20 06:00:00', '2026-01-20 08:15:00', 5500, 95, 100),
  ('AI404', 'Air India Flight', 'Mumbai', 'Bangalore', 1, '2026-01-20 10:30:00', '2026-01-20 12:00:00', 4800, 100, 100),
  ('SG101', 'SpiceJet Red', 'Delhi', 'Kolkata', 2, '2026-01-20 07:00:00', '2026-01-20 09:30:00', 4200, 100, 100),
  ('SG202', 'SpiceJet Express', 'Bangalore', 'Chennai', 2, '2026-01-20 14:00:00', '2026-01-20 15:00:00', 3200, 100, 100),
  ('6E301', 'IndiGo Flight', 'Mumbai', 'Delhi', 3, '2026-01-20 08:00:00', '2026-01-20 10:15:00', 4500, 100, 100),
  ('6E502', 'IndiGo Connect', 'Chennai', 'Hyderabad', 3, '2026-01-20 16:00:00', '2026-01-20 17:15:00', 3800, 100, 100),
  ('UK805', 'Vistara Premium', 'Delhi', 'Bangalore', 4, '2026-01-20 09:00:00', '2026-01-20 11:45:00', 7500, 100, 100),
  ('UK123', 'Vistara Business', 'Mumbai', 'Kolkata', 4, '2026-01-20 11:00:00', '2026-01-20 13:30:00', 6800, 100, 100),
  ('G8401', 'GoAir Value', 'Hyderabad', 'Mumbai', 5, '2026-01-20 12:00:00', '2026-01-20 13:30:00', 3500, 100, 100),
  ('I5701', 'AirAsia Fun', 'Kolkata', 'Bangalore', 6, '2026-01-20 15:00:00', '2026-01-20 17:45:00', 4100, 100, 100)
ON DUPLICATE KEY UPDATE 
  price = VALUES(price),
  seats_available = VALUES(seats_available);

-- Insert sample customers (no email column in Java schema)
INSERT INTO customer (name, nationality, phone, address, aadhar_no, gender) VALUES
  ('John Doe', 'Indian', '9876543210', '123 Main St, Delhi', '123456789012', 'Male'),
  ('Jane Smith', 'Indian', '9876543211', '456 Park Ave, Mumbai', '234567890123', 'Female'),
  ('Raj Kumar', 'Indian', '9876543212', '789 MG Road, Bangalore', '345678901234', 'Male'),
  ('Priya Sharma', 'Indian', '9876543213', '101 Lake View, Chennai', '456789012345', 'Female'),
  ('Amit Patel', 'Indian', '9876543214', '202 Hill Road, Pune', '567890123456', 'Male')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================================================
-- SUCCESS MESSAGE
-- ============================================================================
SELECT '✅ Airline Reservation Database Created Successfully!' AS Status;
SELECT COUNT(*) AS 'Total Airlines' FROM airlines;
SELECT COUNT(*) AS 'Total Flights' FROM flight;
SELECT COUNT(*) AS 'Total Users' FROM users;
SELECT COUNT(*) AS 'Total Customers' FROM customer;
SELECT COUNT(*) AS 'Total Customers' FROM customer;
