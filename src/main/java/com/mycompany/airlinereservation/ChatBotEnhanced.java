package com.mycompany.airlinereservation;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

/**
 * ChatBotEnhanced.java - NLP-Enhanced Chatbot with spaCy Integration
 * 
 * This enhanced version of the chatbot integrates with spaCy NLP service
 * for intelligent natural language understanding and entity extraction.
 * 
 * Features:
 * - spaCy-based intent detection
 * - Automatic entity extraction (locations, dates, travel class, Aadhaar)
 * - Reduced multi-step booking by pre-populating extracted data
 * - Better understanding of natural language queries
 * - Graceful fallback to rule-based routing if NLP service unavailable
 * - Integration with existing database and booking logic
 */
public class ChatBotEnhanced extends ChatBot {
    
    private static final Logger LOGGER = Logger.getLogger(ChatBotEnhanced.class.getName());
    
    private String currentUsername;
    private Map<String, String> bookingContext = new HashMap<>();
    private List<String> conversationHistory = new ArrayList<>();
    private NLPService nlpService;
    
    // Booking states
    private enum BookingState {
        IDLE, AWAITING_SOURCE, AWAITING_DESTINATION, AWAITING_DATE, AWAITING_CLASS, AWAITING_AADHAR, CONFIRMING_BOOKING
    }
    private BookingState currentBookingState = BookingState.IDLE;
    
    public ChatBotEnhanced(String username) {
        super(username);
        this.currentUsername = username;
        DatabaseInitializer.initializeDatabase();
        testDatabaseConnection();
        
        // Initialize NLP service
        try {
            this.nlpService = NLPService.getInstance();
            if (nlpService.isServiceAvailable()) {
                LOGGER.info("✅ spaCy NLP Service initialized successfully");
            } else {
                LOGGER.warning("⚠️  spaCy NLP Service not available - using fallback mode");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "❌ Failed to initialize NLP Service: " + e.getMessage());
            nlpService = null;
        }
    }
    
    /**
     * Test if database is connected
     */
    private void testDatabaseConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                LOGGER.info("✅ ChatBotEnhanced: Database connection successful!");
            } else {
                LOGGER.warning("❌ ChatBotEnhanced: Database connection returned null!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "❌ ChatBotEnhanced: Database connection failed - " + e.getMessage());
        }
    }
    
    /**
     * Process user message with NLP enhancement
     * 
     * This is the main entry point that uses spaCy for intelligent message processing
     */
    public String processMessage(String userMessage) {
        conversationHistory.add("User: " + userMessage);
        
        // Try NLP-based processing first if service available
        if (nlpService != null && nlpService.isServiceAvailable()) {
            String nlpResponse = processMessageWithNLP(userMessage);
            if (nlpResponse != null) {
                return nlpResponse;
            }
        }
        
        // Fallback to rule-based routing
        String response = handleSpecificCommands(userMessage);
        if (response != null) {
            return response;
        }
        
        String routed = routeLocally(userMessage);
        if (routed != null) {
            return routed;
        }
        
        return getFallbackResponse(userMessage);
    }
    
    /**
     * Process message using NLP analysis
     */
    private String processMessageWithNLP(String userMessage) {
        try {
            // Get NLP analysis
            JSONObject nlpResult = nlpService.processText(userMessage);
            
            if (nlpResult == null || !nlpResult.optBoolean("success", false)) {
                return null;
            }
            
            // Extract intent
            JSONObject intentObj = nlpResult.optJSONObject("intent");
            if (intentObj == null) {
                return null;
            }
            
            String primaryIntent = intentObj.optString("primary_intent", null);
            double confidence = intentObj.optDouble("confidence", 0);
            
            LOGGER.info("🧠 NLP Analysis: Intent=" + primaryIntent + ", Confidence=" + confidence);
            
            // Handle based on detected intent
            if (confidence < 0.3) {
                // Low confidence - use fallback
                return null;
            }
            
            switch (primaryIntent) {
                case "book_flight":
                    return handleBookingWithNLP(userMessage, nlpResult);
                    
                case "cancel_booking":
                    return handleCancelBooking(userMessage);
                    
                case "view_booking":
                    return handleShowBookings();
                    
                case "flight_status":
                    return handleFlightStatusQuery(userMessage);
                    
                case "boarding_pass":
                    return handleBoardingPassGeneration(userMessage);
                    
                case "customer_lookup":
                    return handleAadhaarLookup(userMessage);
                    
                case "check_availability":
                    return handleShowFlightsWithNLP(userMessage, nlpResult);
                    
                case "payment_info":
                    return handlePaymentSummary();
                    
                case "help":
                    return getHelpMessage();
                    
                default:
                    return null;
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error in NLP processing: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Handle booking with NLP-extracted data
     */
    private String handleBookingWithNLP(String userMessage, JSONObject nlpResult) {
        try {
            JSONObject extractedData = nlpResult.optJSONObject("extracted_data");
            if (extractedData == null) {
                return null;
            }
            
            // Try to extract all booking information
            boolean hasSource = false;
            boolean hasDestination = false;
            boolean hasDate = false;
            boolean hasClass = false;
            
            // Extract route
            if (extractedData.has("route")) {
                JSONObject route = extractedData.getJSONObject("route");
                String source = route.optString("source", null);
                String destination = route.optString("destination", null);
                
                if (source != null && !source.isEmpty()) {
                    bookingContext.put("source", source);
                    hasSource = true;
                }
                if (destination != null && !destination.isEmpty()) {
                    bookingContext.put("destination", destination);
                    hasDestination = true;
                }
            }
            
            // Extract date
            String date = extractedData.optString("travel_date", null);
            if (date != null && !date.isEmpty()) {
                bookingContext.put("date", date);
                hasDate = true;
            }
            
            // Extract travel class
            String travelClass = extractedData.optString("travel_class", null);
            if (travelClass != null && !travelClass.isEmpty()) {
                bookingContext.put("class", travelClass.toUpperCase());
                hasClass = true;
            }
            
            // Extract Aadhaar if present
            String aadhaar = extractedData.optString("aadhaar", null);
            if (aadhaar != null && !aadhaar.isEmpty()) {
                bookingContext.put("aadhar", aadhaar);
            }
            
            // Determine next state based on what we have
            if (!hasSource) {
                currentBookingState = BookingState.AWAITING_SOURCE;
                return "🎫 Let's book a flight for you!\n\nPlease tell me your departure city (source):";
            } else if (!hasDestination) {
                currentBookingState = BookingState.AWAITING_DESTINATION;
                return "✈️ Great! Source: " + bookingContext.get("source") + "\n\nWhere would you like to go (destination)?";
            } else if (!hasDate) {
                currentBookingState = BookingState.AWAITING_DATE;
                return "📅 Perfect! Destination: " + bookingContext.get("destination") + 
                       "\n\nWhen would you like to travel? (Please provide date in YYYY-MM-DD format):";
            } else if (!hasClass) {
                currentBookingState = BookingState.AWAITING_CLASS;
                return "💺 Which class would you prefer?\n1. Economy\n2. Business\n\nPlease reply with 'Economy' or 'Business':";
            } else {
                // Have all booking info, ask for Aadhaar
                currentBookingState = BookingState.AWAITING_AADHAR;
                return "🆔 Please provide your 12-digit Aadhaar number:";
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error in NLP booking handler: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Handle flights with NLP-extracted route
     */
    private String handleShowFlightsWithNLP(String userMessage, JSONObject nlpResult) {
        try {
            JSONObject extractedData = nlpResult.optJSONObject("extracted_data");
            if (extractedData != null && extractedData.has("route")) {
                JSONObject route = extractedData.getJSONObject("route");
                String source = route.optString("source", null);
                String destination = route.optString("destination", null);
                
                if (source != null && destination != null && !source.isEmpty() && !destination.isEmpty()) {
                    return getFlightsList(source, destination);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error extracting route: " + e.getMessage());
        }
        
        return handleShowFlights(userMessage);
    }
    
    /**
     * Handle specific booking and query commands (original logic)
     */
    private String handleSpecificCommands(String message) {
        String lowerMessage = message.toLowerCase().trim();
        
        if ((lowerMessage.contains("show") || lowerMessage.contains("list")) && 
            (lowerMessage.contains("flight") || lowerMessage.contains("available"))) {
            return handleShowFlights(message);
        }
        
        if ((lowerMessage.contains("book") || lowerMessage.contains("reserve") || lowerMessage.contains("buy")) && 
            lowerMessage.contains("flight")) {
            currentBookingState = BookingState.AWAITING_SOURCE;
            return "🎫 Let's book a flight for you!\n\nPlease tell me your departure city (source):";
        }
        
        if (currentBookingState != BookingState.IDLE) {
            return handleBookingFlow(message);
        }
        
        if ((lowerMessage.contains("show") || lowerMessage.contains("list") || lowerMessage.contains("view")) && 
            lowerMessage.contains("booking")) {
            return handleShowBookings();
        }
        
        if ((lowerMessage.contains("cancel") || lowerMessage.contains("refund")) && 
            (lowerMessage.contains("booking") || lowerMessage.matches(".*\\bpnr\\b.*"))) {
            return handleCancelBooking(message);
        }
        
        if (lowerMessage.contains("status") || lowerMessage.contains("price") || 
            lowerMessage.contains("cost") || lowerMessage.contains("detail")) {
            return handleFlightStatusQuery(message);
        }
        
        if (lowerMessage.contains("boarding") && lowerMessage.contains("pass")) {
            return handleBoardingPassGeneration(message);
        }
        
        if (lowerMessage.contains("customer") || lowerMessage.contains("profile") || 
            lowerMessage.contains("aadhaar") || lowerMessage.contains("aadhar")) {
            return handleAadhaarLookup(message);
        }
        
        if (lowerMessage.contains("payment") || lowerMessage.contains("summary")) {
            return handlePaymentSummary();
        }
        
        if (lowerMessage.contains("help") || lowerMessage.contains("what can you do") || 
            lowerMessage.contains("menu")) {
            return getHelpMessage();
        }
        
        return null;
    }
    
    /**
     * Handle multi-step booking flow
     */
    private String handleBookingFlow(String userInput) {
        switch (currentBookingState) {
            case AWAITING_SOURCE:
                bookingContext.put("source", userInput.trim());
                currentBookingState = BookingState.AWAITING_DESTINATION;
                return "✈️ Great! Source: " + userInput.trim() + "\n\nWhere would you like to go (destination)?";
                
            case AWAITING_DESTINATION:
                bookingContext.put("destination", userInput.trim());
                currentBookingState = BookingState.AWAITING_DATE;
                return "📅 Perfect! Destination: " + userInput.trim() + 
                       "\n\nWhen would you like to travel? (Please provide date in YYYY-MM-DD format):";
                
            case AWAITING_DATE:
                bookingContext.put("date", userInput.trim());
                currentBookingState = BookingState.AWAITING_CLASS;
                return "💺 Which class would you prefer?\n1. Economy\n2. Business\n\nPlease reply with 'Economy' or 'Business':";
                
            case AWAITING_CLASS:
                bookingContext.put("class", userInput.trim().toUpperCase());
                currentBookingState = BookingState.AWAITING_AADHAR;
                return "🆔 Please provide your 12-digit Aadhaar number (e.g., 123456789012):";
                
            case AWAITING_AADHAR:
                String aadhar = userInput.trim().replaceAll("[^0-9]", "");
                if (aadhar.length() != 12) {
                    return "❌ Invalid Aadhaar number. Please enter exactly 12 digits:";
                }
                bookingContext.put("aadhar", aadhar);
                currentBookingState = BookingState.CONFIRMING_BOOKING;
                return confirmAndCreateBooking();
                
            default:
                currentBookingState = BookingState.IDLE;
                return "Booking process cancelled. How can I help you?";
        }
    }
    
    /**
     * Confirm and create booking
     */
    private String confirmAndCreateBooking() {
        String source = bookingContext.get("source");
        String destination = bookingContext.get("destination");
        String date = bookingContext.get("date");
        String travelClass = bookingContext.get("class");
        
        try {
            Flight flight = getFlightBetween(source, destination);
            if (flight == null) {
                currentBookingState = BookingState.IDLE;
                return "❌ Sorry, no flights found between " + source + " and " + destination + 
                       ".\n\nWould you like to try other destinations?";
            }
            
            String pnr = generatePNR();
            insertBooking(pnr, flight.getFlightCode(), date, travelClass);
            
            currentBookingState = BookingState.IDLE;
            bookingContext.clear();
            
            return "✅ Booking Successful!\n\n" +
                   "📋 Booking Details:\n" +
                   "PNR: " + pnr + "\n" +
                   "Flight: " + flight.getFlightCode() + " (" + flight.getAirline() + ")\n" +
                   "Route: " + source + " → " + destination + "\n" +
                   "Date: " + date + "\n" +
                   "Class: " + travelClass + "\n" +
                   "Fare: ₹" + flight.getPrice() + "\n" +
                   "Departure: " + flight.getDepartureTime() + " | Arrival: " + flight.getArrivalTime() + "\n\n" +
                   "✈️ Thank you for booking with us!";
        } catch (Exception e) {
            currentBookingState = BookingState.IDLE;
            return "❌ Error creating booking: " + e.getMessage();
        }
    }
    
    /**
     * Handle show flights
     */
    private String handleShowFlights(String message) {
        Pattern pattern = Pattern.compile("from\\s+([A-Za-z\\s]+?)\\s+to\\s+([A-Za-z\\s]+)", 
                                        Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        
        if (matcher.find()) {
            String source = matcher.group(1).trim().replaceAll("[.,]$", "");
            String destination = matcher.group(2).trim().replaceAll("[.,]$", "");
            return getFlightsList(source, destination);
        }
        
        return "✈️ Please specify the route.\n\nExample: 'Show flights from Delhi to Mumbai'";
    }
    
    /**
     * Get flights list
     */
    private String getFlightsList(String source, String destination) {
        StringBuilder result = new StringBuilder("✈️ Available Flights:\n" +
                "From: " + source + " → To: " + destination + "\n\n");
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return "❌ Database connection failed. Please try again.";
            }
            
            String query = "SELECT f.flight_code, a.airline_name AS airline, f.departure_time, f.arrival_time, " +
                          "TIMESTAMPDIFF(MINUTE, f.departure_time, f.arrival_time) AS duration_minutes, " +
                          "f.price, f.seats_available " +
                          "FROM flight f " +
                          "LEFT JOIN airlines a ON f.airline_id = a.airline_id " +
                          "WHERE LOWER(f.source) = LOWER(?) AND LOWER(f.destination) = LOWER(?)";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, source);
            pstmt.setString(2, destination);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                return "❌ No flights found for this route.";
            }
            
            int count = 0;
            while (rs.next()) {
                count++;
                String airline = rs.getString("airline");
                if (airline == null || airline.isEmpty()) airline = "Unknown Airline";
                
                int durationMinutes = rs.getInt("duration_minutes");
                String duration = rs.wasNull() ? "N/A" : formatDuration(durationMinutes);
                
                result.append(count).append(". Flight: ").append(rs.getString("flight_code"))
                      .append(" (").append(airline).append(")\n")
                      .append("   Depart: ").append(rs.getString("departure_time"))
                      .append(" | Arrive: ").append(rs.getString("arrival_time")).append("\n")
                      .append("   Duration: ").append(duration)
                      .append(" | Price: ₹").append(rs.getInt("price")).append("\n")
                      .append("   Seats: ").append(rs.getInt("seats_available")).append("\n\n");
            }
            
            rs.close();
            pstmt.close();
            
            if (count == 0) {
                return "❌ No flights found for this route.";
            }
            
        } catch (SQLException e) {
            return "❌ Database error: " + e.getMessage();
        }
        
        return result.toString();
    }
    
    /**
     * Get flight between cities
     */
    private Flight getFlightBetween(String source, String destination) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT f.flight_code, a.airline_name AS airline, f.source, f.destination, " +
                          "f.departure_time, f.arrival_time, f.price " +
                          "FROM flight f " +
                          "LEFT JOIN airlines a ON f.airline_id = a.airline_id " +
                          "WHERE LOWER(f.source) = LOWER(?) AND LOWER(f.destination) = LOWER(?) LIMIT 1";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, source);
            pstmt.setString(2, destination);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String airline = rs.getString("airline");
                if (airline == null || airline.isEmpty()) airline = "Unknown Airline";
                
                Flight flight = new Flight(
                    rs.getString("flight_code"),
                    airline,
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("departure_time"),
                    rs.getString("arrival_time"),
                    rs.getInt("price")
                );
                rs.close();
                pstmt.close();
                return flight;
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Handle flight status query
     */
    private String handleFlightStatusQuery(String message) {
        Pattern pattern = Pattern.compile("(\\b[A-Za-z]{1,3}\\d{2,4}\\b)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        
        if (matcher.find()) {
            String flightCode = matcher.group(1).toUpperCase();
            return getFlightStatus(flightCode);
        }
        
        return "🔍 Please provide a flight code (e.g., AI203) to check status.";
    }
    
    /**
     * Get flight status
     */
    private String getFlightStatus(String flightCode) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT f.flight_code, f.source, f.destination, f.departure_time, f.arrival_time, " +
                          "f.price, f.seats_available, a.airline_name AS airline " +
                          "FROM flight f " +
                          "LEFT JOIN airlines a ON f.airline_id = a.airline_id " +
                          "WHERE f.flight_code = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, flightCode);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String airline = rs.getString("airline");
                if (airline == null || airline.isEmpty()) airline = "Unknown Airline";
                
                return "✈️ Flight Details:\n" +
                       "Flight Code: " + rs.getString("flight_code") + "\n" +
                       "Airline: " + airline + "\n" +
                       "Route: " + rs.getString("source") + " → " + rs.getString("destination") + "\n" +
                       "Departure: " + rs.getString("departure_time") + "\n" +
                       "Arrival: " + rs.getString("arrival_time") + "\n" +
                       "Price: ₹" + rs.getInt("price") + "\n" +
                       "Seats Available: " + rs.getInt("seats_available") + "\n" +
                       "Status: ✅ Active";
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            return "❌ Error fetching flight details: " + e.getMessage();
        }
        
        return "❌ Flight not found: " + flightCode;
    }
    
    /**
     * Handle show bookings
     */
    private String handleShowBookings() {
        StringBuilder result = new StringBuilder("📋 Your Bookings:\n\n");
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT b.id, b.flight_code, b.booking_date, f.source, f.destination, f.price, a.airline_name AS airline " +
                          "FROM booking b " +
                          "JOIN flight f ON b.flight_code = f.flight_code " +
                          "LEFT JOIN airlines a ON f.airline_id = a.airline_id " +
                          "WHERE b.username = ? " +
                          "ORDER BY b.booking_date DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, currentUsername);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                return "❌ You have no bookings yet.\n\n💡 Say 'Book a flight' to get started!";
            }
            
            int count = 0;
            while (rs.next()) {
                count++;
                String airline = rs.getString("airline");
                if (airline == null || airline.isEmpty()) airline = "Unknown Airline";
                
                result.append(count).append(". Flight: ").append(rs.getString("flight_code"))
                      .append(" (").append(airline).append(")\n")
                      .append("   Route: ").append(rs.getString("source"))
                      .append(" → ").append(rs.getString("destination")).append("\n")
                      .append("   Booking Date: ").append(rs.getString("booking_date"))
                      .append(" | Amount: ₹").append(String.format("%.2f", rs.getDouble("price"))).append("\n\n");
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            return "❌ Database error: " + e.getMessage();
        }
        
        return result.toString();
    }
    
    /**
     * Handle cancel booking
     */
    private String handleCancelBooking(String message) {
        Pattern pattern = Pattern.compile("(IN\\d+|[A-Z]{2}\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        
        String pnr = null;
        if (matcher.find()) {
            pnr = matcher.group(1).toUpperCase();
        } else {
            return "🔄 Please provide your PNR number to cancel the booking.";
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            String checkQuery = "SELECT id, status, flight_code FROM booking WHERE pnr = ? AND username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, pnr);
            checkStmt.setString(2, currentUsername);
            
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                return "❌ Booking with PNR " + pnr + " not found.";
            }
            
            String currentStatus = rs.getString("status");
            String flightCode = rs.getString("flight_code");
            rs.close();
            checkStmt.close();
            
            if (currentStatus.equalsIgnoreCase("Cancelled")) {
                return "⚠️ This booking has already been cancelled.";
            }
            
            String updateQuery = "UPDATE booking SET status = 'Cancelled' WHERE pnr = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, pnr);
            updateStmt.executeUpdate();
            updateStmt.close();
            
            return "✅ Booking Cancelled Successfully!\n\n" +
                   "📋 PNR: " + pnr + "\n" +
                   "Flight: " + flightCode + "\n" +
                   "Status: Cancelled\n" +
                   "Refund will be processed within 3-5 business days.";
                   
        } catch (SQLException e) {
            return "❌ Database error: " + e.getMessage();
        }
    }
    
    /**
     * Handle boarding pass generation
     */
    private String handleBoardingPassGeneration(String message) {
        Pattern pnrPattern = Pattern.compile("(?:PNR|P)([A-Za-z0-9]{4,8})", Pattern.CASE_INSENSITIVE);
        Matcher pnrMatcher = pnrPattern.matcher(message);
        String pnr = null;
        
        if (pnrMatcher.find()) {
            pnr = pnrMatcher.group(1).toUpperCase();
        }
        
        if (pnr == null) {
            return "📄 Please provide your PNR number to generate boarding pass.";
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT bp.pnr, bp.passenger_name, bp.flight_code, bp.boarding_time, bp.gate_number, " +
                          "bp.seat_number, a.airline_name, f.source, f.destination, f.departure_time " +
                          "FROM boarding_pass bp " +
                          "JOIN flight f ON bp.flight_code = f.flight_code " +
                          "LEFT JOIN airlines a ON f.airline_id = a.airline_id " +
                          "WHERE UPPER(bp.pnr) = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                return "❌ Boarding pass not found for PNR: " + pnr;
            }
            
            String airline = rs.getString("airline_name");
            if (airline == null) airline = "Unknown Airline";
            
            try {
                String boardingPassPath = generateBoardingPassPDF(
                    pnr,
                    rs.getString("flight_code"),
                    airline,
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getString("departure_time"),
                    rs.getString("seat_number"),
                    rs.getString("passenger_name"),
                    rs.getString("boarding_time")
                );
                
                rs.close();
                pstmt.close();
                
                return "🎟️ Boarding Pass Generated Successfully!\n" +
                       "📁 Saved at: " + boardingPassPath;
            } catch (Exception e) {
                rs.close();
                pstmt.close();
                return "❌ Error generating boarding pass: " + e.getMessage();
            }
            
        } catch (SQLException e) {
            return "❌ Database error: " + e.getMessage();
        }
    }
    
    /**
     * Handle Aadhaar lookup
     */
    private String handleAadhaarLookup(String message) {
        Pattern pattern = Pattern.compile("\\b(\\d{12})\\b");
        Matcher matcher = pattern.matcher(message);
        
        String aadhaar = null;
        if (matcher.find()) {
            aadhaar = matcher.group(1);
        } else {
            return "🆔 Please provide your 12-digit Aadhaar number.";
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT name, gender, phone, email, address FROM customer WHERE aadhaar = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, aadhaar);
            
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                return "❌ No customer found with Aadhaar number " + aadhaar;
            }
            
            String name = rs.getString("name");
            String gender = rs.getString("gender");
            String phone = rs.getString("phone");
            String email = rs.getString("email");
            String address = rs.getString("address");
            
            rs.close();
            pstmt.close();
            
            return "👤 Passenger Profile:\n\n" +
                   "📝 Name: " + name + "\n" +
                   "👥 Gender: " + gender + "\n" +
                   "📞 Phone: " + phone + "\n" +
                   "📧 Email: " + email + "\n" +
                   "📍 Address: " + address + "\n\n" +
                   "✅ Profile verified!";
                   
        } catch (SQLException e) {
            return "❌ Database error: " + e.getMessage();
        }
    }
    
    /**
     * Handle payment summary
     */
    private String handlePaymentSummary() {
        StringBuilder result = new StringBuilder("💳 Payment Summary:\n\n");
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT p.pnr, p.amount, p.payment_method, p.payment_status, p.transaction_date, " +
                          "t.flight_code FROM payments p " +
                          "INNER JOIN ticket t ON p.pnr = t.pnr " +
                          "ORDER BY p.transaction_date DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                return "❌ No payment records found.";
            }
            
            int recordCount = 0;
            int totalPaid = 0;
            
            while (rs.next()) {
                recordCount++;
                String status = rs.getString("payment_status");
                double amount = rs.getDouble("amount");
                
                if (status != null && (status.equalsIgnoreCase("Paid") || status.equalsIgnoreCase("Success"))) {
                    totalPaid += (int) amount;
                }
                
                result.append(recordCount).append(". PNR: ").append(rs.getString("pnr"))
                      .append(" | Amount: ₹").append(String.format("%.2f", amount))
                      .append(" | Status: ").append(status).append("\n");
            }
            
            result.append("\nTotal Paid: ₹").append(totalPaid);
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            return "❌ Database error: " + e.getMessage();
        }
        
        return result.toString();
    }
    
    /**
     * Local routing fallback
     */
    private String routeLocally(String message) {
        String l = message == null ? "" : message.toLowerCase().trim();
        if (l.isEmpty()) return null;
        
        if (l.contains("help") || l.contains("menu")) return getHelpMessage();
        if (l.contains("hello") || l.contains("hi")) return getFallbackResponse(message);
        
        return null;
    }
    
    /**
     * Get help message
     */
    private String getHelpMessage() {
        return "📚 ChatBot Help Menu (NLP-Enhanced):\n\n" +
               "✈️ FLIGHTS:\n" +
               "  • 'Show flights from [city] to [city]'\n" +
               "  • 'Flight status [code]'\n\n" +
               "🎫 BOOKING:\n" +
               "  • 'Book a flight from [city] to [city] on [date]'\n" +
               "  • 'Show my bookings'\n" +
               "  • 'Cancel booking [PNR]'\n\n" +
               "📄 DOCUMENTS:\n" +
               "  • 'Generate boarding pass [PNR]'\n\n" +
               "💡 The chatbot now uses NLP to understand natural language!";
    }
    
    /**
     * Get fallback response
     */
    private String getFallbackResponse(String userMessage) {
        return "👋 Hello! I'm your NLP-enhanced airline assistant.\n\n" +
               "I can help you with:\n" +
               "• Booking flights\n" +
               "• Viewing bookings\n" +
               "• Checking flight details\n\n" +
               "Try: 'Book a flight from Delhi to Mumbai'";
    }
    
    /**
     * Format duration
     */
    private String formatDuration(int minutes) {
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
     * Generate PNR
     */
    private String generatePNR() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
    
    /**
     * Generate boarding pass PDF
     */
    private String generateBoardingPassPDF(String pnr, String flightCode, String airline, 
                                          String source, String destination, String departureTime, 
                                          String seat, String passengerName, String boardingTime) throws Exception {
        String boardingPassDir = "C:\\Airline\\BoardingPasses";
        File dir = new File(boardingPassDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String filePath = boardingPassDir + "\\" + pnr + ".txt";
        
        String content = "╔════════════════════════════════════════════╗\n" +
                "║          ✈️ AIRLINE BOARDING PASS ✈️         ║\n" +
                "╠════════════════════════════════════════════╣\n" +
                "║ PNR: " + pnr + "\n" +
                "║ Passenger: " + (passengerName != null ? passengerName : "N/A") + "\n" +
                "║ Flight: " + flightCode + " (" + airline + ")\n" +
                "║ Route: " + source + " → " + destination + "\n" +
                "║ Departure: " + departureTime + "\n" +
                "║ Seat: " + seat + "\n" +
                "║ Boarding: " + boardingTime + "\n" +
                "║════════════════════════════════════════════║\n" +
                "║ Have a great flight! ✈️\n" +
                "╚════════════════════════════════════════════╝";
        
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(content);
        }
        
        return filePath;
    }
    
    /**
     * Insert booking
     */
    private void insertBooking(String pnr, String flightCode, String date, String travelClass) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String aadhaar = bookingContext.get("aadhar");
            if (aadhaar == null) {
                throw new SQLException("Aadhaar number not found");
            }
            
            String query = "INSERT INTO booking (pnr, username, flight_code, date_of_travel, class, aadhaar, status) " +
                          "VALUES (?, ?, ?, ?, ?, ?, 'Confirmed')";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, pnr);
                pstmt.setString(2, currentUsername);
                pstmt.setString(3, flightCode);
                pstmt.setString(4, date);
                pstmt.setString(5, travelClass);
                pstmt.setString(6, aadhaar);
                
                pstmt.executeUpdate();
            }
            
            String flightDetailsQuery = "SELECT departure_time FROM flight WHERE flight_code = ?";
            try (PreparedStatement flightStmt = conn.prepareStatement(flightDetailsQuery)) {
                flightStmt.setString(1, flightCode);
                ResultSet rs = flightStmt.executeQuery();
                
                if (rs.next()) {
                    String departureTime = rs.getString("departure_time");
                    
                    String boardingPassQuery = "INSERT INTO boarding_pass (pnr, passenger_name, flight_code, boarding_time, gate_number, seat_number) " +
                                              "VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement boardingStmt = conn.prepareStatement(boardingPassQuery)) {
                        boardingStmt.setString(1, pnr);
                        boardingStmt.setString(2, currentUsername);
                        boardingStmt.setString(3, flightCode);
                        boardingStmt.setString(4, departureTime);
                        boardingStmt.setString(5, "TBD");
                        boardingStmt.setString(6, "TBD");
                        
                        boardingStmt.executeUpdate();
                    }
                }
                rs.close();
            }
        }
    }
    
    /**
     * Get conversation history
     */
    public List<String> getConversationHistory() {
        return conversationHistory;
    }
    
    /**
     * Clear conversation
     */
    public void clearConversation() {
        conversationHistory.clear();
        bookingContext.clear();
        currentBookingState = BookingState.IDLE;
    }
    
    /**
     * Flight data class
     */
    public static class Flight {
        private String flightCode;
        private String airline;
        private String source;
        private String destination;
        private String departureTime;
        private String arrivalTime;
        private int price;
        
        public Flight(String flightCode, String airline, String source, String destination,
                     String departureTime, String arrivalTime, int price) {
            this.flightCode = flightCode;
            this.airline = airline;
            this.source = source;
            this.destination = destination;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.price = price;
        }
        
        public String getFlightCode() { return flightCode; }
        public String getAirline() { return airline; }
        public String getSource() { return source; }
        public String getDestination() { return destination; }
        public String getDepartureTime() { return departureTime; }
        public String getArrivalTime() { return arrivalTime; }
        public int getPrice() { return price; }
    }
}
