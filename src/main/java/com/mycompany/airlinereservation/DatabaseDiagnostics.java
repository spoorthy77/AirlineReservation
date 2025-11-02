package com.mycompany.airlinereservation;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * DatabaseDiagnostics.java - Diagnose database table structure
 */
public class DatabaseDiagnostics {
    
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.out.println("❌ No database connection!");
                return;
            }
            
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("=== DATABASE DIAGNOSTICS ===\n");
            
            // Check airlines table
            System.out.println("🔍 AIRLINES TABLE COLUMNS:");
            try (ResultSet columns = metaData.getColumns(null, null, "airlines", null)) {
                if (columns.isBeforeFirst()) {
                    while (columns.next()) {
                        String colName = columns.getString("COLUMN_NAME");
                        String colType = columns.getString("TYPE_NAME");
                        System.out.println("   - " + colName + " (" + colType + ")");
                    }
                } else {
                    System.out.println("   ❌ Airlines table does not exist!");
                }
            }
            
            System.out.println("\n🔍 FLIGHT TABLE COLUMNS:");
            try (ResultSet columns = metaData.getColumns(null, null, "flight", null)) {
                if (columns.isBeforeFirst()) {
                    while (columns.next()) {
                        String colName = columns.getString("COLUMN_NAME");
                        String colType = columns.getString("TYPE_NAME");
                        System.out.println("   - " + colName + " (" + colType + ")");
                    }
                } else {
                    System.out.println("   ❌ Flight table does not exist!");
                }
            }
            
            System.out.println("\n=== END DIAGNOSTICS ===");
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
