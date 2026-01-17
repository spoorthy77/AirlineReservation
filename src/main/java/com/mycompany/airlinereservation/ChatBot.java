package com.mycompany.airlinereservation;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ChatBot.java - Base Chatbot class for Airline Reservation System
 * 
 * This is the base class that can be extended by enhanced versions
 * like ChatBotEnhanced with NLP capabilities.
 */
public class ChatBot {
    
    private static final Logger LOGGER = Logger.getLogger(ChatBot.class.getName());
    protected String username;
    private List<String> conversationHistory;
    
    /**
     * Constructor
     */
    public ChatBot(String username) {
        this.username = username;
        this.conversationHistory = new ArrayList<>();
        DatabaseInitializer.initializeDatabase();
        testDatabaseConnection();
    }
    
    /**
     * Test database connection
     */
    private void testDatabaseConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                LOGGER.info("✅ ChatBot: Database connection successful!");
            } else {
                LOGGER.warning("❌ ChatBot: Database connection returned null!");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "❌ ChatBot: Database connection failed - " + e.getMessage());
        }
    }
    
    /**
     * Process user message - This is the main method that subclasses should override
     */
    public String processMessage(String userMessage) {
        conversationHistory.add("User: " + userMessage);
        
        // Basic response handling (can be overridden by subclasses)
        String response = "I received your message: " + userMessage;
        
        // Check for common commands
        if (userMessage.toLowerCase().contains("help")) {
            response = getHelpMessage();
        } else if (userMessage.toLowerCase().contains("hello") || 
                   userMessage.toLowerCase().contains("hi")) {
            response = "Hello " + username + "! How can I assist you with your airline reservations today?";
        }
        
        conversationHistory.add("Bot: " + response);
        return response;
    }
    
    /**
     * Get help message
     */
    protected String getHelpMessage() {
        return "🤖 Available Commands:\n\n" +
               "✈️  Search flights: 'flights from Delhi to Mumbai'\n" +
               "📅 Book a flight: 'book flight'\n" +
               "🎫 View bookings: 'my bookings' or 'show bookings'\n" +
               "❌ Cancel booking: 'cancel booking'\n" +
               "💳 Payment info: 'payment details'\n" +
               "🎟️  Boarding pass: 'boarding pass for [PNR]'\n\n" +
               "Type your question naturally and I'll help you!";
    }
    
    /**
     * Get conversation history
     */
    public List<String> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }
    
    /**
     * Clear conversation history
     */
    public void clearConversation() {
        conversationHistory.clear();
    }
    
    /**
     * Get username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Fallback response for unrecognized queries
     */
    protected String getFallbackResponse(String userMessage) {
        return "I'm not sure how to help with that. Type 'help' to see available commands.";
    }
    
    /**
     * Handle specific commands - can be overridden by subclasses
     */
    protected String handleSpecificCommands(String userMessage) {
        // Subclasses will implement specific command handling
        return null;
    }
    
    /**
     * Route message locally - can be overridden by subclasses
     */
    protected String routeLocally(String userMessage) {
        // Subclasses will implement routing logic
        return null;
    }
}
