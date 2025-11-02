package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * JourneyDetails.java
 * 
 * Displays detailed information about a specific journey/booking for the current user.
 * 
 * ✅ Session-Based Filtering:
 *    - Shows ONLY journey details that belong to the logged-in user
 *    - Uses: WHERE pnr = ? AND username = ?
 *    - Gets username from: SessionManager.getInstance().getCurrentUser()
 * 
 * This ensures users can only view their own journey details.
 */
public class JourneyDetails extends JFrame {

    // ✅ DB CONNECTION DETAILS
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005";

    private JTextField pnrField;
    private JTextArea journeyArea;
    private String currentUsername;

    public JourneyDetails() {
        // 🔑 Get the currently logged-in user from session
        currentUsername = SessionManager.getInstance().getCurrentUser();
        
        if (currentUsername == null || currentUsername.isEmpty()) {
            JOptionPane.showMessageDialog(null, "❌ Please log in first to view journey details.", 
                                        "Session Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        setTitle("📍 Journey Details - User: " + currentUsername);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel setup
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top label
        JLabel title = new JLabel("Check Journey Details", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // Input section
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel pnrLabel = new JLabel("Enter Ticket ID (PNR): ");
        pnrField = new JTextField(15);
        JButton searchBtn = new JButton("Fetch Details");

        inputPanel.add(pnrLabel);
        inputPanel.add(pnrField);
        inputPanel.add(searchBtn);

        panel.add(inputPanel, BorderLayout.CENTER);

        // Output section
        journeyArea = new JTextArea(10, 40);
        journeyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        journeyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(journeyArea);
        panel.add(scrollPane, BorderLayout.SOUTH);

        // Button Action
        searchBtn.addActionListener(e -> fetchJourneyDetails());

        add(panel);
        setVisible(true);
    }

    private void fetchJourneyDetails() {
        // PNR is treated as the 'pnr' column value
        String pnr = pnrField.getText().trim().toUpperCase(); 
        
        if (pnr.isEmpty()) {
            ThemeManager.showWarning(this, "Please enter a valid Ticket ID (PNR).");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); 
            
            // 🔑 ENHANCED: Added username filter to ensure user owns this journey
            // This prevents users from seeing other users' journey details
            String query = "SELECT t.pnr, t.name, t.aadhar_no, t.nationality, " +
                          "t.address, t.gender, t.class, t.status, t.journey_date, t.booking_date, " +
                          "f.flight_name, f.flight_code, f.source, f.destination " +
                          "FROM ticket t " +
                          "JOIN flight f ON t.flight_code = f.flight_code " +
                          "WHERE t.pnr = ? AND t.username = ?";  // 🔑 Double-check: PNR AND username

            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pnr);
            pstmt.setString(2, currentUsername);  // 🔑 Only allow access if username matches
            rs = pstmt.executeQuery();

            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("✈️  JOURNEY DETAILS\n\n");
                details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                details.append("Ticket ID (PNR):   ").append(rs.getString("pnr")).append("\n");
                details.append("Status:            ").append(rs.getString("status")).append("\n");
                details.append("Booking Date:      ").append(rs.getTimestamp("booking_date")).append("\n\n");
                
                details.append("👤 PASSENGER INFORMATION\n");
                details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                details.append("Name:              ").append(rs.getString("name")).append("\n");
                details.append("Aadhar Number:     ").append(rs.getString("aadhar_no")).append("\n");
                details.append("Nationality:       ").append(rs.getString("nationality")).append("\n");
                details.append("Gender:            ").append(rs.getString("gender")).append("\n");
                details.append("Class:             ").append(rs.getString("class")).append("\n");
                details.append("Address:           ").append(rs.getString("address")).append("\n\n");
                
                details.append("✈️  FLIGHT INFORMATION\n");
                details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                details.append("Flight Name:       ").append(rs.getString("flight_name")).append("\n");
                details.append("Flight Code:       ").append(rs.getString("flight_code")).append("\n");
                details.append("Source:            ").append(rs.getString("source")).append("\n");
                details.append("Destination:       ").append(rs.getString("destination")).append("\n");
                details.append("Journey Date:      ").append(rs.getDate("journey_date")).append("\n");

                journeyArea.setText(details.toString());
            } else {
                ThemeManager.showInfo(this, "❌ No journey found for PNR: " + pnr + 
                                     "\nOr this ticket does not belong to your account.");
                journeyArea.setText("");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database Error: " + e.getMessage());
            ThemeManager.showError(this, "Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error fetching details: " + e.getMessage());
            ThemeManager.showError(this, "Error fetching details: " + e.getMessage());
        } finally {
            // Close resources in reverse order
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JourneyDetails());
    }
}