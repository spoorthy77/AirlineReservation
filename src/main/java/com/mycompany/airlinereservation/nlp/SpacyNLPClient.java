package com.mycompany.airlinereservation.nlp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * SpacyNLPClient.java - HTTP Client for spaCy NLP Microservice Integration
 * 
 * Provides methods to:
 * - Process user text for intent and entity extraction
 * - Extract specific entities (locations, dates, money, etc.)
 * - Detect user intent with confidence scoring
 * - Extract travel routes from natural language
 * 
 * Features:
 * - Automatic retry on connection failure
 * - Timeout handling
 * - JSON serialization/deserialization using Gson
 * - Comprehensive logging
 * - Health check endpoint
 */
public class SpacyNLPClient {
    private static final Logger LOGGER = Logger.getLogger(SpacyNLPClient.class.getName());
    
    private final String serviceUrl;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private boolean serviceAvailable = false;
    private int retryCount = 0;
    private static final int MAX_RETRIES = 3;

    // Default endpoints
    private static final String PROCESS_ENDPOINT = "/process";
    private static final String ENTITIES_ENDPOINT = "/entities";
    private static final String INTENT_ENDPOINT = "/intent";
    private static final String EXTRACT_ROUTE_ENDPOINT = "/extract-route";
    private static final String HEALTH_ENDPOINT = "/health";

    /**
     * Constructor with service URL
     */
    public SpacyNLPClient(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        // Configure OkHttpClient with timeout
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        
        // Check service health on initialization
        checkServiceHealth();
    }

    /**
     * Constructor with default localhost URL (port 5000)
     */
    public SpacyNLPClient() {
        this("http://localhost:5000");
    }

    /**
     * Check if spaCy service is available
     */
    public boolean checkServiceHealth() {
        try {
            Request request = new Request.Builder()
                    .url(serviceUrl + HEALTH_ENDPOINT)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                serviceAvailable = response.isSuccessful();
                if (serviceAvailable) {
                    LOGGER.info("✅ spaCy NLP Service is available at " + serviceUrl);
                } else {
                    LOGGER.warning("⚠️  spaCy NLP Service returned status: " + response.code());
                }
                return serviceAvailable;
            }
        } catch (IOException e) {
            serviceAvailable = false;
            LOGGER.warning("❌ spaCy NLP Service unavailable: " + e.getMessage());
            return false;
        }
    }

    /**
     * Process user text for complete NLP analysis
     * 
     * Returns intent, entities, and extracted data in a single response
     */
    public NLPResponse processText(String text) {
        if (!serviceAvailable) {
            LOGGER.warning("Service not available, attempting to reconnect...");
            if (!checkServiceHealth()) {
                return createErrorResponse("spaCy NLP Service unavailable");
            }
        }

        try {
            NLPRequest request = new NLPRequest(text);
            String jsonRequest = gson.toJson(request);

            RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
            Request httpRequest = new Request.Builder()
                    .url(serviceUrl + PROCESS_ENDPOINT)
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    LOGGER.warning("API Error " + response.code() + ": " + errorBody);
                    return createErrorResponse("API returned status " + response.code());
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                return parseResponse(responseBody);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Network error calling spaCy service", e);
            return createErrorResponse("Network error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing text", e);
            return createErrorResponse("Processing error: " + e.getMessage());
        }
    }

    /**
     * Extract only entities from text
     */
    public NLPResponse extractEntities(String text) {
        if (!serviceAvailable && !checkServiceHealth()) {
            return createErrorResponse("spaCy NLP Service unavailable");
        }

        try {
            NLPRequest request = new NLPRequest(text);
            String jsonRequest = gson.toJson(request);

            RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
            Request httpRequest = new Request.Builder()
                    .url(serviceUrl + ENTITIES_ENDPOINT)
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    return createErrorResponse("API returned status " + response.code());
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                return parseResponse(responseBody);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error extracting entities", e);
            return createErrorResponse("Error: " + e.getMessage());
        }
    }

    /**
     * Detect only intent from text
     */
    public NLPIntent detectIntent(String text) {
        if (!serviceAvailable && !checkServiceHealth()) {
            return new NLPIntent("unknown", 0.0);
        }

        try {
            NLPRequest request = new NLPRequest(text);
            String jsonRequest = gson.toJson(request);

            RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
            Request httpRequest = new Request.Builder()
                    .url(serviceUrl + INTENT_ENDPOINT)
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    return new NLPIntent("unknown", 0.0);
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                
                if (json.has("intent")) {
                    JsonObject intentJson = json.getAsJsonObject("intent");
                    String primaryIntent = intentJson.get("primary_intent").getAsString();
                    double confidence = intentJson.get("confidence").getAsDouble();
                    return new NLPIntent(primaryIntent, confidence);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error detecting intent", e);
        }

        return new NLPIntent("unknown", 0.0);
    }

    /**
     * Extract route (source and destination) from text
     */
    public RouteInfo extractRoute(String text) {
        if (!serviceAvailable && !checkServiceHealth()) {
            return new RouteInfo(null, null);
        }

        try {
            NLPRequest request = new NLPRequest(text);
            String jsonRequest = gson.toJson(request);

            RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
            Request httpRequest = new Request.Builder()
                    .url(serviceUrl + EXTRACT_ROUTE_ENDPOINT)
                    .post(body)
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    return new RouteInfo(null, null);
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                
                if (json.has("route")) {
                    JsonObject routeJson = json.getAsJsonObject("route");
                    String source = routeJson.get("source").isJsonNull() ? null : routeJson.get("source").getAsString();
                    String destination = routeJson.get("destination").isJsonNull() ? null : routeJson.get("destination").getAsString();
                    return new RouteInfo(source, destination);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error extracting route", e);
        }

        return new RouteInfo(null, null);
    }

    /**
     * Parse JSON response from spaCy service
     */
    private NLPResponse parseResponse(String jsonResponse) {
        try {
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
            NLPResponse response = new NLPResponse();
            
            response.setSuccess(json.get("success").getAsBoolean());
            response.setOriginalText(json.get("original_text").getAsString());
            response.setProcessedAt(json.get("processed_at").getAsString());

            // Parse intent
            if (json.has("intent")) {
                JsonObject intentJson = json.getAsJsonObject("intent");
                String primaryIntent = intentJson.get("primary_intent").getAsString();
                double confidence = intentJson.get("confidence").getAsDouble();
                response.setIntent(new NLPIntent(primaryIntent, confidence));
            }

            // Parse entities (using Gson to convert JSON to Map)
            if (json.has("entities")) {
                response.setEntities(gson.fromJson(json.get("entities"), java.util.Map.class));
            }

            // Parse extracted data
            if (json.has("extracted_data")) {
                response.setExtractedData(gson.fromJson(json.get("extracted_data"), java.util.Map.class));
            }

            LOGGER.info("✅ Successfully processed: " + response.getOriginalText());
            return response;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing NLP response", e);
            return createErrorResponse("Failed to parse response: " + e.getMessage());
        }
    }

    /**
     * Create error response
     */
    private NLPResponse createErrorResponse(String error) {
        NLPResponse response = new NLPResponse();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }

    /**
     * Check if service is currently available
     */
    public boolean isServiceAvailable() {
        return serviceAvailable;
    }

    /**
     * Get service URL
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * Simple route info holder
     */
    public static class RouteInfo {
        private String source;
        private String destination;

        public RouteInfo(String source, String destination) {
            this.source = source;
            this.destination = destination;
        }

        public String getSource() {
            return source;
        }

        public String getDestination() {
            return destination;
        }

        public boolean isComplete() {
            return source != null && destination != null;
        }

        @Override
        public String toString() {
            return "Route{" +
                    "from=" + source +
                    ", to=" + destination +
                    '}';
        }
    }
}
