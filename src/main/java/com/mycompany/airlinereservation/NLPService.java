package com.mycompany.airlinereservation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * NLPService.java - Integration with Python spaCy NLP Microservice
 * 
 * Handles HTTP communication with the Python Flask spaCy service
 * for natural language understanding and intent/entity extraction.
 * 
 * Features:
 * - Intent detection (book_flight, cancel_booking, etc.)
 * - Entity extraction (locations, dates, organizations, etc.)
 * - Route extraction (source, destination)
 * - Travel date extraction
 * - Travel class detection
 * - Aadhaar number extraction
 * - Retry logic and error handling
 */
public class NLPService {
    
    private static final Logger LOGGER = Logger.getLogger(NLPService.class.getName());
    
    // Python spaCy microservice endpoint
    private static final String SPACY_SERVICE_URL = "http://localhost:5000";
    private static final String PROCESS_ENDPOINT = "/process";
    private static final String ENTITIES_ENDPOINT = "/entities";
    private static final String INTENT_ENDPOINT = "/intent";
    private static final String ROUTE_ENDPOINT = "/extract-route";
    private static final String HEALTH_ENDPOINT = "/health";
    
    // Request timeout in milliseconds
    private static final int TIMEOUT_MS = 5000;
    
    // Retry configuration
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    
    private boolean serviceAvailable = false;
    private static NLPService instance;
    
    private NLPService() {
        // Private constructor for singleton
        checkServiceHealth();
    }
    
    /**
     * Get singleton instance of NLPService
     */
    public static synchronized NLPService getInstance() {
        if (instance == null) {
            instance = new NLPService();
        }
        return instance;
    }
    
    /**
     * Check if spaCy service is available and healthy
     */
    public void checkServiceHealth() {
        try {
            URL url = new URL(SPACY_SERVICE_URL + HEALTH_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            
            int responseCode = conn.getResponseCode();
            serviceAvailable = (responseCode == 200);
            
            if (serviceAvailable) {
                LOGGER.info("✅ spaCy NLP Service is HEALTHY and available");
            } else {
                LOGGER.warning("⚠️  spaCy NLP Service returned status code: " + responseCode);
                serviceAvailable = false;
            }
            
            conn.disconnect();
        } catch (Exception e) {
            LOGGER.warning("⚠️  spaCy NLP Service is NOT AVAILABLE: " + e.getMessage());
            serviceAvailable = false;
        }
    }
    
    /**
     * Check if service is available
     */
    public boolean isServiceAvailable() {
        return serviceAvailable;
    }
    
    /**
     * Send HTTP POST request to spaCy service
     */
    private String sendPostRequest(String endpoint, String jsonPayload) throws Exception {
        int retryCount = 0;
        
        while (retryCount < MAX_RETRIES) {
            try {
                URL url = new URL(SPACY_SERVICE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(TIMEOUT_MS);
                conn.setReadTimeout(TIMEOUT_MS);
                conn.setDoOutput(true);
                
                // Send request body
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                // Read response
                int responseCode = conn.getResponseCode();
                
                if (responseCode == 200) {
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                    }
                    
                    conn.disconnect();
                    return response.toString();
                } else {
                    LOGGER.warning("⚠️  spaCy service returned status code: " + responseCode);
                    conn.disconnect();
                    
                    if (responseCode >= 500) {
                        // Server error - retry
                        retryCount++;
                        if (retryCount < MAX_RETRIES) {
                            LOGGER.info("🔄 Retrying request (attempt " + (retryCount + 1) + "/" + MAX_RETRIES + ")...");
                            Thread.sleep(RETRY_DELAY_MS);
                            continue;
                        }
                    }
                    
                    throw new Exception("spaCy service returned status code: " + responseCode);
                }
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= MAX_RETRIES) {
                    throw e;
                }
                LOGGER.info("🔄 Retrying request (attempt " + (retryCount + 1) + "/" + MAX_RETRIES + ")...");
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        
        throw new Exception("Failed to get response after " + MAX_RETRIES + " retries");
    }
    
    /**
     * Process user text and extract intent, entities, and structured data
     * 
     * @param userText The user input text
     * @return JSONObject with intent, entities, and extracted data
     */
    public JSONObject processText(String userText) {
        if (!serviceAvailable) {
            LOGGER.warning("⚠️  spaCy service not available, returning null");
            return null;
        }
        
        try {
            JSONObject payload = new JSONObject();
            payload.put("text", userText);
            
            String response = sendPostRequest(PROCESS_ENDPOINT, payload.toString());
            return new JSONObject(response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error processing text with spaCy: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Extract only entities from user text
     * 
     * @param userText The user input text
     * @return JSONObject with extracted entities
     */
    public JSONObject extractEntities(String userText) {
        if (!serviceAvailable) {
            LOGGER.warning("⚠️  spaCy service not available, returning null");
            return null;
        }
        
        try {
            JSONObject payload = new JSONObject();
            payload.put("text", userText);
            
            String response = sendPostRequest(ENTITIES_ENDPOINT, payload.toString());
            return new JSONObject(response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error extracting entities: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Detect only intent from user text
     * 
     * @param userText The user input text
     * @return JSONObject with detected intent
     */
    public JSONObject detectIntent(String userText) {
        if (!serviceAvailable) {
            LOGGER.warning("⚠️  spaCy service not available, returning null");
            return null;
        }
        
        try {
            JSONObject payload = new JSONObject();
            payload.put("text", userText);
            
            String response = sendPostRequest(INTENT_ENDPOINT, payload.toString());
            return new JSONObject(response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error detecting intent: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Extract route (source and destination) from user text
     * 
     * @param userText The user input text
     * @return JSONObject with extracted route
     */
    public JSONObject extractRoute(String userText) {
        if (!serviceAvailable) {
            LOGGER.warning("⚠️  spaCy service not available, returning null");
            return null;
        }
        
        try {
            JSONObject payload = new JSONObject();
            payload.put("text", userText);
            
            String response = sendPostRequest(ROUTE_ENDPOINT, payload.toString());
            return new JSONObject(response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error extracting route: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Get primary intent from processed text
     * 
     * @param userText The user input text
     * @return Intent string (e.g., "book_flight", "cancel_booking") or null
     */
    public String getPrimaryIntent(String userText) {
        JSONObject intentResponse = detectIntent(userText);
        
        if (intentResponse != null && intentResponse.optBoolean("success", false)) {
            JSONObject intent = intentResponse.optJSONObject("intent");
            if (intent != null) {
                return intent.optString("primary_intent", null);
            }
        }
        
        return null;
    }
    
    /**
     * Get intent confidence score
     * 
     * @param userText The user input text
     * @return Confidence score (0.0 to 1.0) or -1 if error
     */
    public double getIntentConfidence(String userText) {
        JSONObject intentResponse = detectIntent(userText);
        
        if (intentResponse != null && intentResponse.optBoolean("success", false)) {
            JSONObject intent = intentResponse.optJSONObject("intent");
            if (intent != null) {
                return intent.optDouble("confidence", -1);
            }
        }
        
        return -1;
    }
    
    /**
     * Extract travel date from user text
     * 
     * @param userText The user input text
     * @return Date string or null
     */
    public String extractTravelDate(String userText) {
        JSONObject response = processText(userText);
        
        if (response != null && response.optBoolean("success", false)) {
            JSONObject extractedData = response.optJSONObject("extracted_data");
            if (extractedData != null) {
                return extractedData.optString("travel_date", null);
            }
        }
        
        return null;
    }
    
    /**
     * Extract travel class (Economy/Business) from user text
     * 
     * @param userText The user input text
     * @return Travel class string or null
     */
    public String extractTravelClass(String userText) {
        JSONObject response = processText(userText);
        
        if (response != null && response.optBoolean("success", false)) {
            JSONObject extractedData = response.optJSONObject("extracted_data");
            if (extractedData != null) {
                return extractedData.optString("travel_class", null);
            }
        }
        
        return null;
    }
    
    /**
     * Extract Aadhaar number from user text
     * 
     * @param userText The user input text
     * @return Aadhaar number string or null
     */
    public String extractAadhaar(String userText) {
        JSONObject response = processText(userText);
        
        if (response != null && response.optBoolean("success", false)) {
            JSONObject extractedData = response.optJSONObject("extracted_data");
            if (extractedData != null) {
                return extractedData.optString("aadhaar", null);
            }
        }
        
        return null;
    }
    
    /**
     * Extract all locations mentioned in text
     * 
     * @param userText The user input text
     * @return List of location objects
     */
    public List<String> extractLocations(String userText) {
        List<String> locations = new ArrayList<>();
        JSONObject response = extractEntities(userText);
        
        if (response != null && response.optBoolean("success", false)) {
            JSONObject entities = response.optJSONObject("entities");
            if (entities != null) {
                JSONArray locArray = entities.optJSONArray("locations");
                if (locArray != null) {
                    for (int i = 0; i < locArray.length(); i++) {
                        JSONObject loc = locArray.getJSONObject(i);
                        locations.add(loc.optString("normalized", loc.optString("text", "")));
                    }
                }
                
                JSONArray customLocArray = entities.optJSONArray("custom_locations");
                if (customLocArray != null) {
                    for (int i = 0; i < customLocArray.length(); i++) {
                        JSONObject loc = customLocArray.getJSONObject(i);
                        String normalized = loc.optString("normalized", loc.optString("text", ""));
                        if (!locations.contains(normalized)) {
                            locations.add(normalized);
                        }
                    }
                }
            }
        }
        
        return locations;
    }
}
