package com.mycompany.airlinereservation.nlp;

/**
 * NLPRequest.java - Models the request to spaCy NLP microservice
 * 
 * Simple POJO containing the text to be processed
 */
public class NLPRequest {
    private String text;

    // Constructors
    public NLPRequest() {
    }

    public NLPRequest(String text) {
        this.text = text;
    }

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "NLPRequest{" +
                "text='" + text + '\'' +
                '}';
    }
}
