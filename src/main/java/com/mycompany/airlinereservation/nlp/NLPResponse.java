package com.mycompany.airlinereservation.nlp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NLPResponse.java - Models the response from spaCy NLP microservice
 * 
 * Contains:
 * - Original text processed
 * - Detected intent and confidence
 * - Extracted entities (locations, dates, money, organizations, persons)
 * - Extracted data (route, travel date, travel class, Aadhaar)
 * - POS tags for grammatical analysis
 * - Processing timestamp
 */
public class NLPResponse {
    private boolean success;
    private String originalText;
    private NLPIntent intent;
    private Map<String, Object> entities;
    private Map<String, Object> extractedData;
    private String[] posTags;
    private String processedAt;
    private String error;

    // Constructors
    public NLPResponse() {
        this.entities = new HashMap<>();
        this.extractedData = new HashMap<>();
    }

    public NLPResponse(boolean success, String originalText) {
        this.success = success;
        this.originalText = originalText;
        this.entities = new HashMap<>();
        this.extractedData = new HashMap<>();
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public NLPIntent getIntent() {
        return intent;
    }

    public void setIntent(NLPIntent intent) {
        this.intent = intent;
    }

    public Map<String, Object> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, Object> entities) {
        this.entities = entities;
    }

    public Map<String, Object> getExtractedData() {
        return extractedData;
    }

    public void setExtractedData(Map<String, Object> extractedData) {
        this.extractedData = extractedData;
    }

    public String[] getPosTags() {
        return posTags;
    }

    public void setPosTags(String[] posTags) {
        this.posTags = posTags;
    }

    public String getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * Get primary intent string
     */
    public String getPrimaryIntent() {
        return intent != null ? intent.getPrimaryIntent() : "unknown";
    }

    /**
     * Get intent confidence
     */
    public double getIntentConfidence() {
        return intent != null ? intent.getConfidence() : 0.0;
    }

    /**
     * Get locations from entities
     */
    @SuppressWarnings("unchecked")
    public List<Object> getLocations() {
        return (List<Object>) entities.getOrDefault("locations", List.of());
    }

    /**
     * Get dates from entities
     */
    @SuppressWarnings("unchecked")
    public List<Object> getDates() {
        return (List<Object>) entities.getOrDefault("dates", List.of());
    }

    @Override
    public String toString() {
        return "NLPResponse{" +
                "success=" + success +
                ", originalText='" + originalText + '\'' +
                ", intent=" + intent +
                ", processedAt='" + processedAt + '\'' +
                '}';
    }
}
