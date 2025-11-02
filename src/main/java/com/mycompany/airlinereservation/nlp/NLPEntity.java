package com.mycompany.airlinereservation.nlp;

import java.util.List;

/**
 * NLPEntity.java - Represents an entity extracted by spaCy NLP
 * 
 * Models different types of entities:
 * - Locations (cities, airports)
 * - Dates (travel dates, booking dates)
 * - Organizations (airlines)
 * - Money (prices, fares)
 * - Person (passenger names)
 */
public class NLPEntity {
    private String text;
    private String type;
    private String label;
    private String normalized;
    private int startChar;
    private int endChar;

    // Constructors
    public NLPEntity() {
    }

    public NLPEntity(String text, String type, String label) {
        this.text = text;
        this.type = type;
        this.label = label;
    }

    public NLPEntity(String text, String type, String label, String normalized) {
        this.text = text;
        this.type = type;
        this.label = label;
        this.normalized = normalized;
    }

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNormalized() {
        return normalized;
    }

    public void setNormalized(String normalized) {
        this.normalized = normalized;
    }

    public int getStartChar() {
        return startChar;
    }

    public void setStartChar(int startChar) {
        this.startChar = startChar;
    }

    public int getEndChar() {
        return endChar;
    }

    public void setEndChar(int endChar) {
        this.endChar = endChar;
    }

    @Override
    public String toString() {
        return "NLPEntity{" +
                "text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", label='" + label + '\'' +
                ", normalized='" + normalized + '\'' +
                '}';
    }
}
