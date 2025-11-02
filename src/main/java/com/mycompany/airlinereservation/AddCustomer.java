package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCustomer extends JFrame {

    // 🚨 1. JDBC CONNECTION CONSTANTS (UPDATE THESE)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db"; 
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "Spoorthi@2005"; 
    
    // 🚨 2. Form fields declared as instance variables so the ActionListener can access them
    private JTextField nameField;
    private JTextField nationalityField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField aadharField;
    private JComboBox<String> genderBox;

    public AddCustomer() {
        setTitle("Add Customer Details");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setLayout(new GridBagLayout());
        getContentPane().setBackground(ThemeManager.DARK_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Initialize Fields with Enhanced Themes ---
        nameField = new JTextField();
        ThemeManager.applyUniversalInputContrastTheme(nameField);
        ThemeManager.addPlaceholderText(nameField, "Enter full name");
        
        nationalityField = new JTextField();
        ThemeManager.applyUniversalInputContrastTheme(nationalityField);
        ThemeManager.addPlaceholderText(nationalityField, "Enter nationality");
        
        phoneField = new JTextField();
        ThemeManager.applyUniversalInputContrastTheme(phoneField);
        ThemeManager.addPlaceholderText(phoneField, "Enter phone number");
        
        addressField = new JTextField();
        ThemeManager.applyUniversalInputContrastTheme(addressField);
        ThemeManager.addPlaceholderText(addressField, "Enter address");
        
        aadharField = new JTextField();
        ThemeManager.applyUniversalInputContrastTheme(aadharField);
        ThemeManager.addPlaceholderText(aadharField, "Enter Aadhar number");
        
        String[] genders = {"Select Gender", "Male", "Female", "Other"};
        genderBox = new JComboBox<>(genders);
        ThemeManager.applyUniversalComboBoxContrastTheme(genderBox);
        genderBox.setSelectedIndex(0);

        Dimension fieldSize = new Dimension(150, 25);
        nameField.setPreferredSize(fieldSize);
        nationalityField.setPreferredSize(fieldSize);
        phoneField.setPreferredSize(fieldSize);
        addressField.setPreferredSize(fieldSize);
        aadharField.setPreferredSize(fieldSize);
        genderBox.setPreferredSize(fieldSize);

        // --- Add Labels and Fields with Professional Styling ---
        gbc.gridx = 0; gbc.gridy = 0; 
        JLabel nameLabel = new JLabel("Name:");
        ThemeManager.applyProfessionalLabelTheme(nameLabel, "body");
        add(nameLabel, gbc);
        gbc.gridx = 1; add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; 
        JLabel nationalityLabel = new JLabel("Nationality:");
        ThemeManager.applyProfessionalLabelTheme(nationalityLabel, "body");
        add(nationalityLabel, gbc);
        gbc.gridx = 1; add(nationalityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; 
        JLabel phoneLabel = new JLabel("Phone:");
        ThemeManager.applyProfessionalLabelTheme(phoneLabel, "body");
        add(phoneLabel, gbc);
        gbc.gridx = 1; add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; 
        JLabel addressLabel = new JLabel("Address:");
        ThemeManager.applyProfessionalLabelTheme(addressLabel, "body");
        add(addressLabel, gbc);
        gbc.gridx = 1; add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; 
        JLabel aadharLabel = new JLabel("Aadhar No:");
        ThemeManager.applyProfessionalLabelTheme(aadharLabel, "body");
        add(aadharLabel, gbc);
        gbc.gridx = 1; add(aadharField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; 
        JLabel genderLabel = new JLabel("Gender:");
        ThemeManager.applyProfessionalLabelTheme(genderLabel, "body");
        add(genderLabel, gbc);
        gbc.gridx = 1; add(genderBox, gbc);

        // --- Submit Button with Professional Styling ---
        JButton submitBtn = new JButton("Add Customer");
        ThemeManager.applyProfessionalButtonTheme(submitBtn, true); // Primary action
        gbc.gridx = 1; gbc.gridy = 6; gbc.anchor = GridBagConstraints.CENTER;
        
        // Apply consistent spacing to the entire form
        ThemeManager.applyConsistentSpacing(this);
        
        // Modified ActionListener to call the database insertion method
        submitBtn.addActionListener(this::handleSubmitAction);
        add(submitBtn, gbc);

        // FORCE white text on all labels to fix visibility issue
        ThemeManager.forceHighContrastTextInContainer(this);

        setVisible(true);
    }

    // Submit action handler
    private void handleSubmitAction(ActionEvent e) {
        if (genderBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a gender!");
            return;
        }
        // Attempt to save data
        if (addCustomerToDatabase()) {
            JOptionPane.showMessageDialog(this, "Customer Added Successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer. Check console for details.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🚨 4. Database insertion method
    private boolean addCustomerToDatabase() {
        String sql = "INSERT INTO customer (name, nationality, phone, address, aadhar_no, gender) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the values from the form fields
            pstmt.setString(1, nameField.getText());
            pstmt.setString(2, nationalityField.getText());
            pstmt.setString(3, phoneField.getText());
            pstmt.setString(4, addressField.getText());
            pstmt.setString(5, aadharField.getText());
            pstmt.setString(6, (String) genderBox.getSelectedItem());

            // Execute the insertion
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException ex) {
            System.err.println("SQL Error during customer insertion: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddCustomer());
    }
}