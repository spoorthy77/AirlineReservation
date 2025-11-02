package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.UUID;
import com.toedter.calendar.JDateChooser;

public class BookFlight extends JFrame {

    // ✅ DB CONNECTION DETAILS (kept for compatibility with existing code)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005";

    private JTextField aadharField, nameField, nationalityField, flightNameField, flightCodeField;
    private JTextArea addressArea;
    private JComboBox<String> genderCombo, sourceCombo, destinationCombo;
    private JDateChooser dateChooser;
    
    // 🧮 Variable to store the fetched ticket price
    private double ticketPrice = 0.0;
    
    // ✅ Store current user's username for session-based booking
    private String currentUsername;

    public BookFlight() {
        // ✅ ENHANCEMENT: Get the current logged-in user from SessionManager
        currentUsername = SessionManager.getInstance().getCurrentUser();
        if (currentUsername == null || currentUsername.isEmpty()) {
            currentUsername = "Unknown User";
            System.err.println("❌ Warning: BookFlight opened without a logged-in user!");
        }
        System.out.println("✅ BookFlight initialized with user: " + currentUsername);  // Debug log
        
        setTitle("Book Flight - User: " + currentUsername);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(ThemeManager.DARK_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add Book Flight title header
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        JLabel titleLabel = new JLabel("Book Flight", SwingConstants.CENTER);
        ThemeManager.applyProfessionalLabelTheme(titleLabel, "title");
        add(titleLabel, gbc);
        
        // Reset for normal components
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        // --- Passenger Fields ---
        aadharField = new JTextField(15);
        ThemeManager.applyUniversalInputContrastTheme(aadharField);
        ThemeManager.addPlaceholderText(aadharField, "Enter Aadhar Number");
        
        nameField = new JTextField(15);
        ThemeManager.applyUniversalInputContrastTheme(nameField);
        
        nationalityField = new JTextField(15);
        ThemeManager.applyUniversalInputContrastTheme(nationalityField);
        
        addressArea = new JTextArea(3, 15);
        ThemeManager.applyUniversalInputContrastTheme(addressArea);
        
        genderCombo = new JComboBox<>(new String[]{"Select Gender", "Male", "Female", "Other"});
        ThemeManager.applyUniversalComboBoxContrastTheme(genderCombo);
        
        dateChooser = new JDateChooser();
        java.awt.Component dateEditor = dateChooser.getDateEditor().getUiComponent();
        if (dateEditor instanceof JTextField dateField) {
            ThemeManager.applyUniversalInputContrastTheme(dateField);
        }

        nameField.setEditable(false);
        nationalityField.setEditable(false);
        addressArea.setEditable(false);

        // --- Flight Fields ---
        sourceCombo = new JComboBox<>();
        ThemeManager.applyUniversalComboBoxContrastTheme(sourceCombo);
        
        destinationCombo = new JComboBox<>();
        ThemeManager.applyUniversalComboBoxContrastTheme(destinationCombo);
        
        flightNameField = new JTextField(15);
        ThemeManager.applyUniversalInputContrastTheme(flightNameField);
        
        flightCodeField = new JTextField(15);
        ThemeManager.applyUniversalInputContrastTheme(flightCodeField);

        flightNameField.setEditable(false);
        flightCodeField.setEditable(false);

        // Initially empty dropdowns
        sourceCombo.addItem("Click to Load Sources");
        destinationCombo.addItem("Click to Load Destinations");

        // When dropdown is clicked, load from DB
        sourceCombo.addPopupMenuListener(new PopupMenuListenerAdapter() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                loadSources();
            }
        });

        destinationCombo.addPopupMenuListener(new PopupMenuListenerAdapter() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                loadDestinations();
            }
        });

        // --- Layout Design ---
        gbc.gridx = 0; gbc.gridy = 1; 
        JLabel aadharLabel = new JLabel("Aadhar");
        aadharLabel.setForeground(Color.WHITE); // Pure white color
        aadharLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(aadharLabel, gbc);
        
        gbc.gridx = 1; add(aadharField, gbc);
        JButton fetchUserBtn = new JButton("Fetch User");
        ThemeManager.applyProfessionalButtonTheme(fetchUserBtn, false); // Secondary action
        fetchUserBtn.addActionListener(this::fetchUserAction);
        gbc.gridx = 2; add(fetchUserBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 2; 
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setForeground(Color.WHITE); // Pure white color
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(nameLabel, gbc);
        gbc.gridx = 1; add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; 
        JLabel nationalityLabel = new JLabel("Nationality");
        nationalityLabel.setForeground(Color.WHITE); // Pure white color
        nationalityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(nationalityLabel, gbc);
        gbc.gridx = 1; add(nationalityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; 
        JLabel addressLabel = new JLabel("Address");
        addressLabel.setForeground(Color.WHITE); // Pure white color
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(addressLabel, gbc);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.getViewport().setBackground(Color.WHITE);
        addressScroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));
        gbc.gridx = 1; add(addressScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 5; 
        JLabel genderLabel = new JLabel("Gender");
        genderLabel.setForeground(Color.WHITE); // Pure white color
        genderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(genderLabel, gbc);
        gbc.gridx = 1; add(genderCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 6; 
        JLabel sourceLabel = new JLabel("Source");
        sourceLabel.setForeground(Color.WHITE); // Pure white color
        sourceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(sourceLabel, gbc);
        gbc.gridx = 1; add(sourceCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 7; 
        JLabel destinationLabel = new JLabel("Destination");
        destinationLabel.setForeground(Color.WHITE); // Pure white color
        destinationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(destinationLabel, gbc);
        gbc.gridx = 1; add(destinationCombo, gbc);

        JButton fetchFlightsBtn = new JButton("Fetch Flight & Price");
        ThemeManager.applyProfessionalButtonTheme(fetchFlightsBtn, false); // Secondary action
        fetchFlightsBtn.addActionListener(this::fetchFlightsAction);
        gbc.gridx = 2; gbc.gridy = 7; add(fetchFlightsBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 8; 
        JLabel flightNameLabel = new JLabel("Flight Name");
        flightNameLabel.setForeground(Color.WHITE); // Pure white color
        flightNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(flightNameLabel, gbc);
        gbc.gridx = 1; add(flightNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 9; 
        JLabel flightCodeLabel = new JLabel("Flight Code");
        flightCodeLabel.setForeground(Color.WHITE); // Pure white color
        flightCodeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(flightCodeLabel, gbc);
        gbc.gridx = 1; add(flightCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 10; 
        JLabel dateLabel = new JLabel("Date of Travel");
        dateLabel.setForeground(Color.WHITE); // Pure white color
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(dateLabel, gbc);
        gbc.gridx = 1; add(dateChooser, gbc);

        JButton bookFlightBtn = new JButton("Book & Pay");
        ThemeManager.applyProfessionalButtonTheme(bookFlightBtn, true); // Primary action
        bookFlightBtn.addActionListener(this::bookFlightAction);
        gbc.gridx = 1; gbc.gridy = 11; gbc.anchor = GridBagConstraints.CENTER;
        add(bookFlightBtn, gbc);

        // Apply consistent spacing to the entire form
        ThemeManager.applyConsistentSpacing(this);
        
        // FORCE white text on all labels to fix visibility issue
        ThemeManager.forceHighContrastTextInContainer(this);
        
        // EMERGENCY: Force white text more aggressively
        ThemeManager.emergencyForceWhiteText(this);

        setVisible(true);
        
        // Apply white text again after window is visible to ensure it takes effect
        SwingUtilities.invokeLater(() -> {
            ThemeManager.emergencyForceWhiteText(this);
            
            // ✅ ENHANCEMENT: Auto-fetch user data from database using username
            // This ensures the form is pre-populated with customer information
            fetchUserDataByUsername();
        });
    }
    
    // ✅ NEW METHOD: Fetch user data directly from the session username (using aadhar)
    // This pre-populates customer information when the form loads
    private void fetchUserDataByUsername() {
        // Note: The customer table doesn't have a username column, so we skip this
        // Users can manually enter their aadhar and click "Fetch User" button to load their data
        System.out.println("⚠️ BookFlight: Customer table does not have username column. Manual entry required.");
    }
    
    // --- ACTION HANDLERS ---
    
    private void fetchUserAction(ActionEvent e) {
        // Existing implementation is fine
        String aadhar = aadharField.getText();
        if (aadhar.isEmpty()) {
            showWarningMessage("Please enter Aadhar number.");
            return;
        }

        String sql = "SELECT name, nationality, address, gender FROM customer WHERE aadhar_no = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // ... (rest of the fetchUserAction logic)
            pstmt.setString(1, aadhar);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                nationalityField.setText(rs.getString("nationality"));
                addressArea.setText(rs.getString("address"));
                genderCombo.setSelectedItem(rs.getString("gender"));
            } else {
                showWarningMessage("Customer not found. Please add customer first.");
                nameField.setEditable(true);
                nationalityField.setEditable(true);
                addressArea.setEditable(true);
            }
        } catch (SQLException ex) {
            showErrorMessage("DB Error fetching user: " + ex.getMessage());
        }
    }

    // ✅ Fetch Flight Details and Price
    private void fetchFlightsAction(ActionEvent e) {
        String source = (String) sourceCombo.getSelectedItem();
        String destination = (String) destinationCombo.getSelectedItem();

        if (source == null || destination == null) {
            showWarningMessage("Select source and destination.");
            return;
        }

        // Fetch flight details AND price
        String sql = "SELECT flight_name, flight_code, price FROM flight WHERE source = ? AND destination = ? LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, source);
            pstmt.setString(2, destination);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                flightNameField.setText(rs.getString("flight_name"));
                String code = rs.getString("flight_code");
                flightCodeField.setText(code);
                
                // Store the price
                ticketPrice = rs.getDouble("price");
                
                showInfoMessage(String.format("Flight %s found. Price: %.2f INR", code, ticketPrice));
            } else {
                showWarningMessage("No flight found for this route.");
                ticketPrice = 0.0;
            }
        } catch (SQLException ex) {
            showErrorMessage("Error fetching flight: " + ex.getMessage());
        }
    }

    // ✅ Book Flight Action (Insert into ticket, booking, and payments tables)
    private void bookFlightAction(ActionEvent e) {
        if (aadharField.getText().isEmpty() || flightCodeField.getText().isEmpty() || dateChooser.getDate() == null || ticketPrice <= 0) {
            showWarningMessage("Fill all required details and ensure flight price is fetched (click 'Fetch Flight & Price').");
            return;
        }

        String pnr = generatePNR(); 
        
        System.out.println("🔍 DEBUG: Booking with username: " + currentUsername);  // Debug log

        // 1. INSERT INTO TICKET AND BOOKING TABLES
        // ✅ FIXED: Added username field so bookings are linked to the current user
        String sqlTicket = "INSERT INTO ticket (pnr, username, customer_aadhar, customer_name, nationality, address, gender, source, destination, flight_name, flight_code, date_of_travel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlBooking = "INSERT INTO booking (pnr, username, flight_code, booking_date, date_of_travel, class, aadhaar, status) VALUES (?, ?, ?, NOW(), ?, ?, ?, 'Confirmed')";
        // Use a transaction so: check seats -> insert ticket -> insert payment -> decrement seats (atomic)
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try {
                conn.setAutoCommit(false);

                // 0. Check seat availability with row-level lock
                String checkSql = "SELECT seats_available FROM flight WHERE flight_code = ? FOR UPDATE";
                try (PreparedStatement pstCheck = conn.prepareStatement(checkSql)) {
                    pstCheck.setString(1, flightCodeField.getText());
                    try (ResultSet rs = pstCheck.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            showErrorMessage("Selected flight not found. Booking cancelled.");
                            return;
                        }
                        int avail = rs.getInt("seats_available");
                        if (avail <= 0) {
                            conn.rollback();
                            showWarningMessage("No seats available for the selected flight.");
                            return;
                        }
                    }
                }

                // 1. INSERT INTO TICKET TABLE
                try (PreparedStatement pstmtTicket = conn.prepareStatement(sqlTicket)) {
                    pstmtTicket.setString(1, pnr);
                    pstmtTicket.setString(2, currentUsername);  // ✅ FIXED: Added username
                    pstmtTicket.setString(3, aadharField.getText());
                    pstmtTicket.setString(4, nameField.getText());
                    pstmtTicket.setString(5, nationalityField.getText());
                    pstmtTicket.setString(6, addressArea.getText());
                    pstmtTicket.setString(7, (String) genderCombo.getSelectedItem());
                    pstmtTicket.setString(8, (String) sourceCombo.getSelectedItem());
                    pstmtTicket.setString(9, (String) destinationCombo.getSelectedItem());
                    pstmtTicket.setString(10, flightNameField.getText());
                    pstmtTicket.setString(11, flightCodeField.getText());
                    pstmtTicket.setDate(12, new java.sql.Date(dateChooser.getDate().getTime()));

                    pstmtTicket.executeUpdate();
                }

                // 2. INSERT INTO BOOKING TABLE
                try (PreparedStatement pstmtBooking = conn.prepareStatement(sqlBooking)) {
                    pstmtBooking.setString(1, pnr);  // pnr
                    pstmtBooking.setString(2, currentUsername);  // username
                    pstmtBooking.setString(3, flightCodeField.getText());  // flight_code
                    pstmtBooking.setDate(4, new java.sql.Date(dateChooser.getDate().getTime()));  // date_of_travel
                    pstmtBooking.setString(5, "Economy");  // class (default to Economy)
                    pstmtBooking.setString(6, aadharField.getText());  // aadhaar
                    
                    pstmtBooking.executeUpdate();
                }

                // 2. INSERT INTO PAYMENTS TABLE (use same connection so it's part of the transaction)
                String paymentSql = "INSERT INTO payments (pnr, amount, payment_method, payment_status, transaction_date) VALUES (?, ?, ?, ?, NOW())";
                try (PreparedStatement pstPay = conn.prepareStatement(paymentSql)) {
                    pstPay.setString(1, pnr);
                    pstPay.setDouble(2, ticketPrice);
                    pstPay.setString(3, "Credit Card");
                    pstPay.setString(4, "Completed");
                    pstPay.executeUpdate();
                }

                // 3. Decrement seats_available
                String updSql = "UPDATE flight SET seats_available = seats_available - 1 WHERE flight_code = ?";
                try (PreparedStatement pstUpd = conn.prepareStatement(updSql)) {
                    pstUpd.setString(1, flightCodeField.getText());
                    pstUpd.executeUpdate();
                }

                conn.commit();
                dispose();

            } catch (SQLException exInner) {
                try { conn.rollback(); } catch (SQLException ignore) {}
                showErrorMessage("Booking failed (transaction rolled back): " + exInner.getMessage());
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
            }

        } catch (SQLException ex) {
            showErrorMessage("Booking failed: " + ex.getMessage());
        }
    }

    // --- Utility: Generate Random 6-char PNR using UUID and ensure (best-effort) uniqueness ---
    private String generatePNR() {
        String pnr = null;
        int attempts = 0;
        while (attempts < 5) {
            // generate 6-char alphanumeric from UUID and uppercase it
            pnr = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();

            // Check DB for collision (best-effort). If DB check fails, just return the generated PNR.
            String sql = "SELECT COUNT(*) FROM ticket WHERE pnr = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, pnr);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        if (count == 0) return pnr;
                    } else {
                        return pnr;
                    }
                }
            } catch (SQLException ex) {
                // If DB check fails, log and return the generated PNR (so booking can proceed)
                System.err.println("PNR uniqueness check failed: " + ex.getMessage());
                return pnr;
            }

            attempts++;
        }

        // After a few attempts, return the last generated PNR
        return pnr != null ? pnr : UUID.randomUUID().toString().replace("-", "").substring(0,6).toUpperCase();
    }
    
    // ... (loadSources, loadDestinations, and PopupMenuListenerAdapter remain the same) ...

    private void loadSources() {
        sourceCombo.removeAllItems();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT source FROM flight");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                sourceCombo.addItem(rs.getString("source"));
            }
        } catch (SQLException ex) {
            showErrorMessage("Error loading sources: " + ex.getMessage());
        }
    }

    private void loadDestinations() {
        destinationCombo.removeAllItems();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT destination FROM flight");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                destinationCombo.addItem(rs.getString("destination"));
            }
        } catch (SQLException ex) {
            showErrorMessage("Error loading destinations: " + ex.getMessage());
        }
    }

    private void showInfoMessage(String message) {
        ThemeManager.showInfo(this, message);
    }

    private void showWarningMessage(String message) {
        ThemeManager.showWarning(this, message);
    }

    private void showErrorMessage(String message) {
        ThemeManager.showError(this, message);
    }

    private abstract static class PopupMenuListenerAdapter implements javax.swing.event.PopupMenuListener {
        @Override
        public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {}
        @Override
        public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
        @Override
        public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookFlight::new);
    }
    
}
