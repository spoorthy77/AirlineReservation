package com.mycompany.airlinereservation; // Adjust package name if needed

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BoardingPass extends JFrame {

    // ✅ DB CONNECTION DETAILS (MUST MATCH your airline_db settings)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005"; // Ensure this is correct

    private JTextField pnrInputField;
    private JLabel pnrValueLabel, nameValueLabel, nationalityValueLabel, srcValueLabel, destValueLabel, flightNameValueLabel, flightCodeValueLabel, dateValueLabel;
    private JButton fetchBtn;

    public BoardingPass() {
        setTitle("AIR INDIA - Boarding Pass");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null); 

        // --- PNR Input Section (Top) ---
        JLabel pnrInputLabel = new JLabel("PNR DETAILS");
        pnrInputLabel.setBounds(50, 40, 100, 30);
        pnrInputLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        add(pnrInputLabel);

        pnrInputField = new JTextField();
        pnrInputField.setBounds(160, 40, 150, 30);
        add(pnrInputField);

        fetchBtn = new JButton("Fetch");
        fetchBtn.setBounds(330, 40, 100, 30);
        fetchBtn.setBackground(Color.BLACK);
        fetchBtn.setForeground(Color.WHITE);
        fetchBtn.addActionListener(this::fetchBoardingPassDetails);
        add(fetchBtn);
        
        // --- Boarding Pass Display Section ---
        
        // Header
        JLabel header = new JLabel("AIR INDIA", SwingConstants.CENTER);
        header.setBounds(0, 0, 700, 30);
        header.setFont(new Font("Tahoma", Font.BOLD, 24));
        add(header);
        
        JLabel subHeader = new JLabel("Boarding Pass", SwingConstants.CENTER);
        subHeader.setBounds(0, 30, 700, 30);
        subHeader.setFont(new Font("Tahoma", Font.BOLD, 18));
        subHeader.setForeground(new Color(204, 51, 0)); // Red color
        add(subHeader);
        
        // Separator Line
        JSeparator separator = new JSeparator();
        separator.setBounds(20, 80, 660, 10);
        add(separator);

        // --- Details Labels (PNR, Name, etc.) ---

        // 1. PNR
        JLabel pnrLabel = new JLabel("PNR:");
        pnrLabel.setBounds(50, 110, 100, 30);
        add(pnrLabel);
        pnrValueLabel = new JLabel("XXXXXX"); // Placeholder
        pnrValueLabel.setBounds(160, 110, 200, 30);
        pnrValueLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        add(pnrValueLabel);

        // 2. Name
        JLabel nameLabel = new JLabel("NAME:");
        nameLabel.setBounds(50, 150, 100, 30);
        add(nameLabel);
        nameValueLabel = new JLabel("N/A");
        nameValueLabel.setBounds(160, 150, 200, 30);
        add(nameValueLabel);
        
        // 3. Nationality
        JLabel nationalityLabel = new JLabel("NATIONALITY:");
        nationalityLabel.setBounds(50, 190, 100, 30);
        add(nationalityLabel);
        nationalityValueLabel = new JLabel("N/A");
        nationalityValueLabel.setBounds(160, 190, 200, 30);
        add(nationalityValueLabel);

        // 4. Source & Destination
        JLabel srcLabel = new JLabel("SRC:");
        srcLabel.setBounds(50, 230, 100, 30);
        add(srcLabel);
        srcValueLabel = new JLabel("N/A");
        srcValueLabel.setBounds(160, 230, 150, 30);
        add(srcValueLabel);

        JLabel destLabel = new JLabel("DEST:");
        destLabel.setBounds(330, 230, 100, 30);
        add(destLabel);
        destValueLabel = new JLabel("N/A");
        destValueLabel.setBounds(440, 230, 150, 30);
        add(destValueLabel);

        // 5. Flight Name & Code
        JLabel flightNameLabel = new JLabel("Flight Name:");
        flightNameLabel.setBounds(50, 270, 100, 30);
        add(flightNameLabel);
        flightNameValueLabel = new JLabel("N/A");
        flightNameValueLabel.setBounds(160, 270, 150, 30);
        add(flightNameValueLabel);

        JLabel flightCodeLabel = new JLabel("Flight Code:");
        flightCodeLabel.setBounds(330, 270, 100, 30);
        add(flightCodeLabel);
        flightCodeValueLabel = new JLabel("N/A");
        flightCodeValueLabel.setBounds(440, 270, 150, 30);
        add(flightCodeValueLabel);

        // 6. Date
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds(50, 310, 100, 30);
        add(dateLabel);
        dateValueLabel = new JLabel("N/A");
        dateValueLabel.setBounds(160, 310, 200, 30);
        add(dateValueLabel);
        
        // --- Airline Logo (Optional, based on your project structure) ---
        JLabel logo = new JLabel();
        // You'll need to adjust the path to your logo file (e.g., airindia.png)
        // For now, it's just a placeholder or text.
        logo.setText("✈️ AIR INDIA"); 
        logo.setFont(new Font("Serif", Font.ITALIC, 20));
        logo.setBounds(480, 150, 200, 100);
        add(logo);

        setVisible(true);
    }
    
    /**
     * Resets all displayed detail labels to N/A or default state.
     */
    private void resetDetails() {
        pnrValueLabel.setText("XXXXXX");
        nameValueLabel.setText("N/A");
        nationalityValueLabel.setText("N/A");
        srcValueLabel.setText("N/A");
        destValueLabel.setText("N/A");
        flightNameValueLabel.setText("N/A");
        flightCodeValueLabel.setText("N/A");
        dateValueLabel.setText("N/A");
    }

    /**
     * Fetches ticket and passenger details using the PNR.
     */
    private void fetchBoardingPassDetails(ActionEvent e) {
        String pnr = pnrInputField.getText().trim().toUpperCase();
        resetDetails(); // Clear previous data

        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the PNR number.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT customer_name, nationality, source, destination, flight_name, flight_code, date_of_travel FROM ticket WHERE pnr = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pnr);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Populate GUI labels
                pnrValueLabel.setText(pnr);
                nameValueLabel.setText(rs.getString("customer_name"));
                nationalityValueLabel.setText(rs.getString("nationality"));
                srcValueLabel.setText(rs.getString("source"));
                destValueLabel.setText(rs.getString("destination"));
                flightNameValueLabel.setText(rs.getString("flight_name"));
                flightCodeValueLabel.setText(rs.getString("flight_code"));
                
                // Format date
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                Date travelDate = rs.getDate("date_of_travel");
                dateValueLabel.setText(sdf.format(travelDate));

            } else {
                JOptionPane.showMessageDialog(this, "No ticket found for PNR: " + pnr, "Not Found", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BoardingPass::new);
    }
}