package com.mycompany.airlinereservation;

import java.sql.Connection;

/**
 * ChatBotDatabaseTest.java - Test ChatBot database connectivity
 */
public class ChatBotDatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("ChatBot Database Connection Test");
        System.out.println("==============================================\n");
        
        // Test 1: Direct DBConnection
        System.out.println("Test 1: Direct DBConnection.getConnection()");
        try (Connection conn1 = DBConnection.getConnection()) {
            if (conn1 != null) {
                System.out.println("✅ Direct connection SUCCESS!");
                System.out.println("   Connection: " + conn1);
            } else {
                System.out.println("❌ Direct connection returned NULL!");
            }
        } catch (Exception e) {
            System.out.println("❌ Direct connection FAILED: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n" + "-".repeat(45) + "\n");
        
        // Test 2: ChatBot initialization
        System.out.println("Test 2: ChatBot Initialization");
        try {
            ChatBot bot = new ChatBot("testuser");
            System.out.println("✅ ChatBot created successfully!");
        } catch (Exception e) {
            System.out.println("❌ ChatBot creation FAILED: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n" + "-".repeat(45) + "\n");
        
        // Test 3: ChatBot query
        System.out.println("Test 3: ChatBot Flight Query");
        try {
            ChatBot bot = new ChatBot("testuser");
            String result = bot.processMessage("Show flights from Delhi to Mumbai");
            System.out.println("✅ Query executed!");
            System.out.println("\nResponse:");
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("❌ Query FAILED: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n" + "=".repeat(45));
        System.out.println("Test Complete!");
        System.out.println("=".repeat(45));
    }
}
