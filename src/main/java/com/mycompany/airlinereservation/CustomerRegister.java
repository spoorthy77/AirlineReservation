package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerRegister extends JFrame {

    // Using centralized DBConnection for DB access

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public CustomerRegister() {
        setTitle("✈️ New Customer Registration");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        getContentPane().setBackground(ThemeManager.DARK_BG);

        JLabel titleLabel = new JLabel("Customer Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        ThemeManager.applyDarkLabelTheme(titleLabel);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; add(titleLabel, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; 
        JLabel usernameLabel = new JLabel("Username:");
        ThemeManager.applyDarkLabelTheme(usernameLabel);
        add(usernameLabel, gbc);
        
        gbc.gridx = 1; 
        usernameField = new JTextField(15); 
        ThemeManager.applyDarkTextFieldTheme(usernameField);
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; 
        JLabel passwordLabel = new JLabel("Password:");
        ThemeManager.applyDarkLabelTheme(passwordLabel);
        add(passwordLabel, gbc);
        
        gbc.gridx = 1; 
        passwordField = new JPasswordField(15); 
        ThemeManager.applyDarkPasswordFieldTheme(passwordField);
        add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 3; 
        JLabel confirmLabel = new JLabel("Confirm Password:");
        ThemeManager.applyDarkLabelTheme(confirmLabel);
        add(confirmLabel, gbc);
        
        gbc.gridx = 1; 
        confirmPasswordField = new JPasswordField(15); 
        ThemeManager.applyDarkPasswordFieldTheme(confirmPasswordField);
        add(confirmPasswordField, gbc);

        JButton registerBtn = new JButton("Register & Login");
        JButton backBtn = new JButton("Back to Login");
        
        ThemeManager.applyDarkButtonTheme(registerBtn, true);
        ThemeManager.applyDarkButtonTheme(backBtn, false);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        ThemeManager.applyDarkPanelTheme(btnPanel);
        btnPanel.add(backBtn);
        btnPanel.add(registerBtn);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; add(btnPanel, gbc);

        registerBtn.addActionListener(this::registerButtonActionPerformed);
        backBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(Login::new);
        });

        setVisible(true);
    }
    
    private void registerButtonActionPerformed(ActionEvent evt) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Registration Successful! Logging you in...");
            dispose();
            // After successful registration, launch Login with Customer preselected
            SwingUtilities.invokeLater(() -> new Login("Customer"));
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed. Username might already be taken.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 💾 Insert new Customer into database with 'Customer' role
    private boolean registerUser(String username, String password) {
        // Ensure your 'users' table has columns: username, password, role
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'Customer')";

        // Hash password before storing
        String hashed = PasswordUtils.hashPassword(password);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashed);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException ex) {
            System.err.println("SQL Error during customer registration: " + ex.getMessage());
            // Check for duplicate entry error
            if (ex.getErrorCode() == 1062) { 
                JOptionPane.showMessageDialog(this, "The username '" + username + "' is already taken.", "Username Conflict", JOptionPane.ERROR_MESSAGE);
            } else {
                 ex.printStackTrace();
            }
            return false;
        }
    }
}