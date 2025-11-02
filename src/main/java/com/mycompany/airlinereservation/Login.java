package com.mycompany.airlinereservation;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class Login extends JFrame {

    // DBConnection class provides connection details (DB_URL/USER/PASSWORD centralized)

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRole;

    // Default constructor: normal login screen
    public Login() {
        initComponents();
    }

    // Overloaded constructor to preselect a role (e.g., "Customer" or "Admin")
    public Login(String preselectedRole) {
        initComponents();
        if (preselectedRole != null && comboRole != null) {
            comboRole.setSelectedItem(preselectedRole);
        }
    }

    // Shared UI initialization
    private void initComponents() {
        setTitle("✈️ Airline Reservation System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Apply explicit dark background to content pane to make colors consistent
        getContentPane().setBackground(ThemeManager.DARK_BG);

        JLabel lblTitle = new JLabel("AIRLINE RESERVATION LOGIN", SwingConstants.CENTER);
        ThemeManager.applyProfessionalLabelTheme(lblTitle, "title");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblUser = new JLabel("Username:");
        ThemeManager.applyProfessionalLabelTheme(lblUser, "body");
        add(lblUser, gbc);

        txtUsername = new JTextField(15);
        // Apply universal input contrast theme for maximum readability
        ThemeManager.applyUniversalInputContrastTheme(txtUsername);
        ThemeManager.addPlaceholderText(txtUsername, "Enter your username");
        gbc.gridx = 1;
        add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblPass = new JLabel("Password:");
        ThemeManager.applyProfessionalLabelTheme(lblPass, "body");
        add(lblPass, gbc);

        txtPassword = new JPasswordField(15);
        // Apply universal input contrast theme for password field
        ThemeManager.applyUniversalInputContrastTheme(txtPassword);
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblRole = new JLabel("Role:");
        ThemeManager.applyProfessionalLabelTheme(lblRole, "body");
        add(lblRole, gbc);

    comboRole = new JComboBox<>(new String[]{"Admin", "Customer"});
    ThemeManager.applyUniversalComboBoxContrastTheme(comboRole);
    gbc.gridx = 1;
    add(comboRole, gbc);

        // Buttons
        JButton btnLogin = new JButton("Login");
        JButton btnClear = new JButton("Clear");
        JButton btnRegister = new JButton("Register"); // NEW BUTTON

    // Button Panel for better layout - match dark theme
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    ThemeManager.applyDarkPanelTheme(btnPanel);

    // Apply professional button themes with proper hierarchy
    ThemeManager.applyProfessionalButtonTheme(btnLogin, true);    // Primary action
    ThemeManager.applyProfessionalButtonTheme(btnRegister, true); // Primary action
    ThemeManager.applyProfessionalButtonTheme(btnClear, false);   // Secondary action

    btnPanel.add(btnRegister); // Add Register button
    btnPanel.add(btnLogin);
    btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(btnPanel, gbc);

        // Apply consistent spacing throughout the form
        ThemeManager.applyConsistentSpacing(this);

        // Action Listeners
        btnLogin.addActionListener(this::handleLogin);
        btnClear.addActionListener(this::handleClear);
        btnRegister.addActionListener(this::handleRegister); // NEW ACTION

        setVisible(true);
    }
    
    // 🔹 Handler for Clear Button
    private void handleClear(ActionEvent e) {
        txtUsername.setText("");
        txtPassword.setText("");
        // Reset to placeholder text if using placeholders
        txtUsername.requestFocus();
    }
    
    // Use centralized DBConnection for consistency
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
    
    // 🔹 Handler for Register Button
    private void handleRegister(ActionEvent e) {
        dispose(); // Close the current login window
        SwingUtilities.invokeLater(() -> new CustomerRegister()); // Open the new registration window
    }

    // 🔹 Login Action (No change needed here, the prompt is handled by the new button)
    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String role = (String) comboRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            ThemeManager.showWarning(this, "Please fill in all fields.");
            return;
        }

        try (Connection con = getConnection()) {
            String query = "SELECT password FROM users WHERE username=? AND role=? LIMIT 1";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, role);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String stored = rs.getString("password");

                // If stored value is a BCrypt hash, verify it.
                boolean verified = PasswordUtils.verifyPassword(password, stored);

                // If not verified, support legacy plaintext passwords: compare directly.
                boolean legacyMatch = !verified && stored.equals(password);

                if (verified || legacyMatch) {
                    // If legacy plaintext matched, migrate to a hashed password immediately.
                    if (legacyMatch) {
                        String newHash = PasswordUtils.hashPassword(password);
                        try (PreparedStatement ups = con.prepareStatement("UPDATE users SET password = ? WHERE username = ?")) {
                            ups.setString(1, newHash);
                            ups.setString(2, username);
                            ups.executeUpdate();
                            System.out.println("Login: Migrated user '" + username + "' to BCrypt-hashed password.");
                        } catch (SQLException ignore) {
                            // Migration failed — not fatal for login
                            System.err.println("Login: failed to migrate password for user " + username + ": " + ignore.getMessage());
                        }
                    }

                    ThemeManager.showInfo(this, "✅ Login Successful!");
                    // Store the current user in SessionManager for session management
                    SessionManager.getInstance().setCurrentUser(username);
                    dispose(); // close login window
                    if ("Admin".equalsIgnoreCase(role)) {
                        SwingUtilities.invokeLater(() -> new AdminDashboard(username));
                    } else {
                        SwingUtilities.invokeLater(() -> new UserDashboard(username));
                    }
                    return;
                }
            }

            // If we reach here, authentication failed
            if (role.equals("Customer")) {
                int option = JOptionPane.showConfirmDialog(this,
                        "❌ Invalid credentials! Would you like to register a new account?",
                        "Login Failed", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    handleRegister(null);
                }
            } else {
                ThemeManager.showError(this, "❌ Invalid credentials or role!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            ThemeManager.showError(this, "Database error. Please check connection.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}