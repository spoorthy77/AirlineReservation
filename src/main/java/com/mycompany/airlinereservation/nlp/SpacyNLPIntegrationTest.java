package com.mycompany.airlinereservation.nlp;

/**
 * SpacyNLPIntegrationTest.java - Integration tests for spaCy NLP service
 * 
 * Tests various NLP capabilities:
 * - Intent detection
 * - Entity extraction
 * - Route extraction (source, destination)
 * - Multi-language support
 * - Confidence scoring
 */
public class SpacyNLPIntegrationTest {

    public static void main(String[] args) {
        System.out.println("=== spaCy NLP Integration Test Suite ===\n");
        
        // Initialize client
        SpacyNLPClient client = new SpacyNLPClient("http://localhost:5000");
        
        // Check service health
        System.out.println("1. Health Check:");
        boolean isHealthy = client.checkServiceHealth();
        System.out.println("   Service Status: " + (isHealthy ? "✅ Available" : "❌ Unavailable") + "\n");
        
        if (!isHealthy) {
            System.out.println("❌ spaCy service is not running. Please start it with:");
            System.out.println("   cd spacy_service");
            System.out.println("   python app.py");
            return;
        }
        
        // Test cases
        testIntentDetection(client);
        testEntityExtraction(client);
        testRouteExtraction(client);
        testCompleteProcessing(client);
        
        System.out.println("\n=== Test Suite Complete ===");
    }
    
    /**
     * Test intent detection
     */
    private static void testIntentDetection(SpacyNLPClient client) {
        System.out.println("2. Intent Detection Tests:");
        
        String[] testQueries = {
            "I want to book a flight from Delhi to Mumbai",
            "Show me available flights",
            "What is the status of my booking PNR ABC1234",
            "Generate my boarding pass",
            "Cancel my booking",
            "Check flight price",
            "Help me with the menu"
        };
        
        for (String query : testQueries) {
            NLPIntent intent = client.detectIntent(query);
            System.out.println("   Query: \"" + query + "\"");
            System.out.println("   Intent: " + intent.getPrimaryIntent() + 
                             " (Confidence: " + intent.getConfidence() + ")");
            System.out.println();
        }
    }
    
    /**
     * Test entity extraction
     */
    private static void testEntityExtraction(SpacyNLPClient client) {
        System.out.println("3. Entity Extraction Tests:");
        
        String[] testQueries = {
            "Flight from Delhi to Mumbai on 2025-12-15 costs ₹5000",
            "Book a ticket for John Smith with Aadhaar 123456789012",
            "Air India flight AI203 departs at 10:30 AM from Indira Gandhi Airport",
            "The ticket costs between ₹3000 and ₹5500"
        };
        
        for (String query : testQueries) {
            NLPResponse response = client.extractEntities(query);
            System.out.println("   Query: \"" + query + "\"");
            if (response.isSuccess()) {
                System.out.println("   Entities: " + response.getEntities());
            } else {
                System.out.println("   Error: " + response.getError());
            }
            System.out.println();
        }
    }
    
    /**
     * Test route extraction
     */
    private static void testRouteExtraction(SpacyNLPClient client) {
        System.out.println("4. Route Extraction Tests:");
        
        String[] testQueries = {
            "Show flights from Delhi to Bangalore",
            "I need a flight from Mumbai to Kolkata",
            "Flights between Delhi and Hyderabad",
            "What flights go from Pune to Goa?",
            "Travel from Jaipur to Ahmedabad"
        };
        
        for (String query : testQueries) {
            SpacyNLPClient.RouteInfo route = client.extractRoute(query);
            System.out.println("   Query: \"" + query + "\"");
            System.out.println("   Route: " + route);
            System.out.println();
        }
    }
    
    /**
     * Test complete processing
     */
    private static void testCompleteProcessing(SpacyNLPClient client) {
        System.out.println("5. Complete NLP Processing Tests:");
        
        String[] testQueries = {
            "I want to book a business class flight from Delhi to Mumbai on 2025-12-20 with Aadhaar 987654321098",
            "Can you show me economy flights from Bangalore to Pune?",
            "What's the status of my booking with PNR XYZ9876?"
        };
        
        for (String query : testQueries) {
            NLPResponse response = client.processText(query);
            System.out.println("   Query: \"" + query + "\"");
            System.out.println("   Original Text: " + response.getOriginalText());
            System.out.println("   Intent: " + response.getPrimaryIntent() + 
                             " (Confidence: " + response.getIntentConfidence() + ")");
            System.out.println("   Entities: " + response.getEntities());
            System.out.println("   Extracted Data: " + response.getExtractedData());
            System.out.println("   Processed At: " + response.getProcessedAt());
            System.out.println();
        }
    }
}
