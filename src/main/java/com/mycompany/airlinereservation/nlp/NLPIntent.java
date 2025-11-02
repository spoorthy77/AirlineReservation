package com.mycompany.airlinereservation.nlp;

/**
 * NLPIntent.java - Represents the intent detected from user query
 * 
 * Intent types:
 * - book_flight: User wants to book a flight
 * - cancel_booking: User wants to cancel a booking
 * - view_booking: User wants to view booking details
 * - flight_status: User wants to check flight status
 * - boarding_pass: User wants to generate boarding pass
 * - customer_lookup: User wants to look up customer info
 * - check_availability: User wants to search available flights
 * - payment_info: User wants payment information
 * - help: User wants help menu
 */
public class NLPIntent {
    private String primaryIntent;
    private double confidence;
    private String[] allIntents;

    // Constructors
    public NLPIntent() {
    }

    public NLPIntent(String primaryIntent, double confidence) {
        this.primaryIntent = primaryIntent;
        this.confidence = confidence;
    }

    public NLPIntent(String primaryIntent, double confidence, String[] allIntents) {
        this.primaryIntent = primaryIntent;
        this.confidence = confidence;
        this.allIntents = allIntents;
    }

    // Getters and Setters
    public String getPrimaryIntent() {
        return primaryIntent;
    }

    public void setPrimaryIntent(String primaryIntent) {
        this.primaryIntent = primaryIntent;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String[] getAllIntents() {
        return allIntents;
    }

    public void setAllIntents(String[] allIntents) {
        this.allIntents = allIntents;
    }

    /**
     * Check if intent is recognized with sufficient confidence
     */
    public boolean isHighConfidence() {
        return confidence >= 0.7;
    }

    @Override
    public String toString() {
        return "NLPIntent{" +
                "primaryIntent='" + primaryIntent + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
