package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Objects;

public class AddAirline extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005";

    private JTextField txtAirlineName;
    private JTextField txtRating; // Decimal(2,1)

    public AddAirline() {
        setTitle("✈️ Add New Airline");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    setLayout(new GridBagLayout());
    getContentPane().setBackground(ThemeManager.DARK_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Enter Airline Details", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
    ThemeManager.applyDarkLabelTheme(lblTitle);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // Airline Name
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
    JLabel lblAirlineName = new JLabel("Airline Name:");
    ThemeManager.applyDarkLabelTheme(lblAirlineName);
    add(lblAirlineName, gbc);
        txtAirlineName = new JTextField(15);
    ThemeManager.applyDarkTextFieldTheme(txtAirlineName);
        gbc.gridx = 1;
        add(txtAirlineName, gbc);

        // Rating
        gbc.gridx = 0; gbc.gridy = 2;
    JLabel lblRating = new JLabel("Rating (X.X):");
    ThemeManager.applyDarkLabelTheme(lblRating);
    add(lblRating, gbc);
        txtRating = new JTextField(15);
    ThemeManager.applyDarkTextFieldTheme(txtRating);
        gbc.gridx = 1;
        add(txtRating, gbc);

        // Button
        JButton btnAdd = new JButton("Add Airline");
    ThemeManager.applyDarkButtonTheme(btnAdd, true);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        btnAdd.addActionListener(this::handleAddAirline);
        add(btnAdd, gbc);

        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void handleAddAirline(ActionEvent event) {
        Objects.requireNonNull(event, "event");
        String name = txtAirlineName.getText().trim();
        String ratingStr = txtRating.getText().trim();
        double rating = 0.0;

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Airline Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate and parse Rating (optional field, but if provided, must be a number)
        if (!ratingStr.isEmpty()) {
            try {
                rating = Double.parseDouble(ratingStr);
                if (rating < 0.0 || rating > 5.0) { // Basic rating check (assuming 0-5 scale)
                    JOptionPane.showMessageDialog(this, "Rating must be between 0.0 and 5.0.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Rating must be a valid decimal number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }


        String sql = "INSERT INTO airlines (airline_name, rating) VALUES (?, ?)";

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            // Handle NULL for rating if not provided, otherwise set the parsed value
            if (ratingStr.isEmpty()) {
                pst.setNull(2, Types.DECIMAL); 
            } else {
                pst.setDouble(2, rating);
            }

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Airline '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtAirlineName.setText("");
                txtRating.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add airline.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLIntegrityConstraintViolationException ex) {
             JOptionPane.showMessageDialog(this, "Airline name may already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ThemeManager.showError(this, "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddAirline::new);
    }
}