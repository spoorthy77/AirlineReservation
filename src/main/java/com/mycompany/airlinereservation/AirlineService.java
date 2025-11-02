package com.mycompany.airlinereservation;

import java.sql.*;
import java.util.*;

/**
 * AirlineService.java - Business Logic Layer for Airline Operations
 * 
 * Handles:
 * - Flight search and filtering
 * - Booking management (create, cancel, update)
 * - Ticket operations (generate, retrieve)
 * - Payment tracking
 * - Boarding pass generation
 * - Customer profile management
 * - Aadhaar-based lookups
 */
public class AirlineService {
    
    /**
     * Search flights between source and destination with optional filters
     */
    public static List<Flight> searchFlights(String source, String destination, String date) {
        List<Flight> flights = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder query = new StringBuilder(
                "SELECT f.flight_code, f.flight_name, a.airline_name, f.source, f.destination, " +
                "f.departure_time, f.arrival_time, f.price, f.seats_available " +
                "FROM flight f " +
                "LEFT JOIN airlines a ON f.airline_id = a.airline_id " +
                "WHERE LOWER(f.source) = LOWER(?) AND LOWER(f.destination) = LOWER()"
            );
            
            if (date != null && !date.isEmpty()) {
                query.append(" AND DATE(f.departure_time) = DATE(?)");
            }
            
            query.append(" ORDER BY f.departure_time ASC");
            
            PreparedStatement pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1, source);
            pstmt.setString(2, destination);
            if (date != null && !date.isEmpty()) {
                pstmt.setString(3, date);
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                flights.add(new Flight(
                    rs.getString("flight_code"),
                    rs.getString("flight_name"),
                    rs.getString("airline_name") != null ? rs.getString("airline_name") : "Unknown",
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("departure_time"),
                    rs.getString("arrival_time"),
                    rs.getInt("price"),
                    rs.getInt("seats_available")
                ));
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error searching flights: " + e.getMessage());
        }
        
        return flights;
    }
    
    /**
     * Get flight by flight code
     */
    public static Flight getFlightByCode(String flightCode) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT f.flight_code, f.flight_name, a.airline_name, f.source, f.destination, " +
                          "f.departure_time, f.arrival_time, f.price, f.seats_available " +
                          "FROM flight f " +
                          "LEFT JOIN airlines a ON f.airline_id = a.airline_id " +
                          "WHERE f.flight_code = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, flightCode);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Flight flight = new Flight(
                    rs.getString("flight_code"),
                    rs.getString("flight_name"),
                    rs.getString("airline_name") != null ? rs.getString("airline_name") : "Unknown",
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("departure_time"),
                    rs.getString("arrival_time"),
                    rs.getInt("price"),
                    rs.getInt("seats_available")
                );
                rs.close();
                pstmt.close();
                return flight;
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching flight: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Create new booking
     */
    public static String createBooking(String username, String flightCode, String date, 
                                       String travelClass, String aadhaar) throws SQLException {
        String pnr = generatePNR();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO booking (pnr, username, flight_code, booking_date, date_of_travel, " +
                          "class, aadhaar, status) VALUES (?, ?, ?, NOW(), ?, ?, ?, 'Confirmed')";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            pstmt.setString(2, username);
            pstmt.setString(3, flightCode);
            pstmt.setString(4, date);
            pstmt.setString(5, travelClass);
            pstmt.setString(6, aadhaar);
            
            pstmt.executeUpdate();
            pstmt.close();
            
            // Create ticket entry
            createTicket(pnr, flightCode, aadhaar, username);
            
            System.out.println("✅ Booking created with PNR: " + pnr);
        }
        
        return pnr;
    }
    
    /**
     * Create ticket entry
     */
    private static void createTicket(String pnr, String flightCode, String aadhaar, String username) 
            throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO ticket (pnr, customer_aadhar, customer_name, flight_code, date_of_travel) " +
                          "VALUES (?, ?, ?, ?, NOW())";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            pstmt.setString(2, aadhaar);
            pstmt.setString(3, username);
            pstmt.setString(4, flightCode);
            
            pstmt.executeUpdate();
            pstmt.close();
        }
    }
    
    /**
     * Get booking by PNR
     */
    public static Map<String, Object> getBookingByPNR(String pnr) {
        Map<String, Object> booking = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT b.pnr, b.username, b.flight_code, b.booking_date, b.date_of_travel, " +
                          "b.class, b.status, f.source, f.destination, a.airline_name " +
                          "FROM booking b " +
                          "JOIN flight f ON b.flight_code = f.flight_code " +
                          "LEFT JOIN airlines a ON f.airline_id = a.airline_id " +
                          "WHERE b.pnr = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                booking.put("pnr", rs.getString("pnr"));
                booking.put("username", rs.getString("username"));
                booking.put("flightCode", rs.getString("flight_code"));
                booking.put("bookingDate", rs.getString("booking_date"));
                booking.put("travelDate", rs.getString("date_of_travel"));
                booking.put("class", rs.getString("class"));
                booking.put("status", rs.getString("status"));
                booking.put("source", rs.getString("source"));
                booking.put("destination", rs.getString("destination"));
                booking.put("airline", rs.getString("airline_name"));
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching booking: " + e.getMessage());
        }
        
        return booking;
    }
    
    /**
     * Get all bookings for a customer
     */
    public static List<Map<String, Object>> getBookingsByUsername(String username) {
        List<Map<String, Object>> bookings = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT b.pnr, b.flight_code, b.booking_date, b.date_of_travel, b.class, b.status, " +
                          "f.source, f.destination, a.airline_name, f.price " +
                          "FROM booking b " +
                          "JOIN flight f ON b.flight_code = f.flight_code " +
                          "LEFT JOIN airlines a ON f.airline_id = a.airline_id " +
                          "WHERE b.username = ? " +
                          "ORDER BY b.booking_date DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> booking = new HashMap<>();
                booking.put("pnr", rs.getString("pnr"));
                booking.put("flightCode", rs.getString("flight_code"));
                booking.put("bookingDate", rs.getString("booking_date"));
                booking.put("travelDate", rs.getString("date_of_travel"));
                booking.put("class", rs.getString("class"));
                booking.put("status", rs.getString("status"));
                booking.put("source", rs.getString("source"));
                booking.put("destination", rs.getString("destination"));
                booking.put("airline", rs.getString("airline_name"));
                booking.put("price", rs.getInt("price"));
                bookings.add(booking);
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching bookings: " + e.getMessage());
        }
        
        return bookings;
    }
    
    /**
     * Cancel booking by PNR
     */
    public static boolean cancelBooking(String pnr) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE booking SET status = 'Cancelled' WHERE pnr = ? AND status != 'Cancelled'";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            
            int result = pstmt.executeUpdate();
            pstmt.close();
            
            return result > 0;
        }
    }
    
    /**
     * Get ticket by PNR
     */
    public static Map<String, String> getTicketByPNR(String pnr) {
        Map<String, String> ticket = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT t.ticket_id, t.pnr, t.customer_aadhar, t.customer_name, " +
                          "t.nationality, t.address, t.gender, t.source, t.destination, " +
                          "t.flight_name, t.flight_code, t.date_of_travel, t.booking_date " +
                          "FROM ticket t WHERE t.pnr = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ticket.put("ticketId", rs.getString("ticket_id"));
                ticket.put("pnr", rs.getString("pnr"));
                ticket.put("aadhaar", rs.getString("customer_aadhar"));
                ticket.put("name", rs.getString("customer_name"));
                ticket.put("nationality", rs.getString("nationality"));
                ticket.put("address", rs.getString("address"));
                ticket.put("gender", rs.getString("gender"));
                ticket.put("source", rs.getString("source"));
                ticket.put("destination", rs.getString("destination"));
                ticket.put("flightName", rs.getString("flight_name"));
                ticket.put("flightCode", rs.getString("flight_code"));
                ticket.put("travelDate", rs.getString("date_of_travel"));
                ticket.put("bookingDate", rs.getString("booking_date"));
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching ticket: " + e.getMessage());
        }
        
        return ticket;
    }
    
    /**
     * Get payment details by PNR
     */
    public static Map<String, Object> getPaymentByPNR(String pnr) {
        Map<String, Object> payment = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.payment_id, p.amount, p.payment_method, p.payment_status, p.transaction_date " +
                          "FROM payments p WHERE p.pnr = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                payment.put("paymentId", rs.getString("payment_id"));
                payment.put("amount", rs.getInt("amount"));
                payment.put("method", rs.getString("payment_method"));
                payment.put("status", rs.getString("payment_status"));
                payment.put("date", rs.getString("transaction_date"));
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching payment: " + e.getMessage());
        }
        
        return payment;
    }
    
    /**
     * Get customer by Aadhaar
     */
    public static Map<String, String> getCustomerByAadhaar(String aadhaar) {
        Map<String, String> customer = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT customer_id, name, nationality, phone, address, aadhar_no, gender " +
                          "FROM customer WHERE aadhar_no = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, aadhaar);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                customer.put("id", rs.getString("customer_id"));
                customer.put("name", rs.getString("name"));
                customer.put("nationality", rs.getString("nationality"));
                customer.put("phone", rs.getString("phone"));
                customer.put("address", rs.getString("address"));
                customer.put("aadhaar", rs.getString("aadhar_no"));
                customer.put("gender", rs.getString("gender"));
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching customer: " + e.getMessage());
        }
        
        return customer;
    }
    
    /**
     * Create or update boarding pass
     */
    public static boolean createBoardingPass(String pnr, String passengerName, String flightCode, 
                                            String boardingTime, String gateNumber, String seatNumber) 
            throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO boarding_pass (pnr, passenger_name, flight_code, boarding_time, " +
                          "gate_number, seat_number, created_at) " +
                          "VALUES (?, ?, ?, ?, ?, ?, NOW())";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            pstmt.setString(2, passengerName);
            pstmt.setString(3, flightCode);
            pstmt.setString(4, boardingTime);
            pstmt.setString(5, gateNumber);
            pstmt.setString(6, seatNumber);
            
            int result = pstmt.executeUpdate();
            pstmt.close();
            
            return result > 0;
        }
    }
    
    /**
     * Get boarding pass by PNR
     */
    public static Map<String, String> getBoardingPassByPNR(String pnr) {
        Map<String, String> boardingPass = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id, pnr, passenger_name, flight_code, boarding_time, gate_number, " +
                          "seat_number, created_at FROM boarding_pass WHERE pnr = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boardingPass.put("id", rs.getString("id"));
                boardingPass.put("pnr", rs.getString("pnr"));
                boardingPass.put("passengerName", rs.getString("passenger_name"));
                boardingPass.put("flightCode", rs.getString("flight_code"));
                boardingPass.put("boardingTime", rs.getString("boarding_time"));
                boardingPass.put("gateNumber", rs.getString("gate_number"));
                boardingPass.put("seatNumber", rs.getString("seat_number"));
                boardingPass.put("createdAt", rs.getString("created_at"));
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("❌ Error fetching boarding pass: " + e.getMessage());
        }
        
        return boardingPass;
    }
    
    /**
     * Generate unique PNR
     */
    public static String generatePNR() {
        return "IN" + System.currentTimeMillis() % 100000;
    }
    
    /**
     * Format duration in minutes
     */
    public static String formatDuration(int minutes) {
        if (minutes <= 0) return "N/A";
        int hours = minutes / 60;
        int mins = minutes % 60;
        if (hours > 0 && mins > 0) {
            return String.format("%dh %02dm", hours, mins);
        }
        if (hours > 0) {
            return String.format("%dh", hours);
        }
        return mins + "m";
    }
    
    /**
     * Flight data model
     */
    public static class Flight {
        public String flightCode;
        public String flightName;
        public String airline;
        public String source;
        public String destination;
        public String departureTime;
        public String arrivalTime;
        public int price;
        public int seatsAvailable;
        
        public Flight(String flightCode, String flightName, String airline, String source, 
                     String destination, String departureTime, String arrivalTime, 
                     int price, int seatsAvailable) {
            this.flightCode = flightCode;
            this.flightName = flightName;
            this.airline = airline;
            this.source = source;
            this.destination = destination;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.price = price;
            this.seatsAvailable = seatsAvailable;
        }
    }
}
