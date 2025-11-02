package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FlightInfo extends JFrame {

    // Use centralized DBConnection.getConnection() for DB access

    // ✅ NEW: Map to store Airline Name -> ID for insertion
    private Map<String, Integer> airlineNameToIdMap = new HashMap<>();

    private JComboBox<String> comboSource;
    private JComboBox<String> comboDestination;
    private JComboBox<String> comboAirline;
    private JComboBox<String> comboFlights; 

    private JTextField flightCodeField;
    private JTextField flightNameField;
    private JTextField departureTimeField;
    private JTextField arrivalTimeField;
    private JTextField priceField;
    private JTextField seatsAvailableField;
    
    // ⚠️ CRITICAL MISSING FIELD: total_seats must be added to the UI
    private JTextField totalSeatsField; 

    public FlightInfo() {
        setTitle("Add Flight Details (Admin)");
        setSize(700, 550); // Increased size to fit total_seats field
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ThemeManager.DARK_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Row 0: Flight Code
        gbc.gridx = 0; gbc.gridy = row; 
        JLabel flightCodeLabel = new JLabel("Flight Code:");
        ThemeManager.applyDarkLabelTheme(flightCodeLabel);
        add(flightCodeLabel, gbc);
    gbc.gridx = 1; flightCodeField = new JTextField(15); 
    ThemeManager.applyDarkSurfaceTextFieldTheme(flightCodeField);
        add(flightCodeField, gbc);

        // Row 1: Flight Name
        gbc.gridx = 0; gbc.gridy = ++row; 
        JLabel flightNameLabel = new JLabel("Flight Name:");
        ThemeManager.applyDarkLabelTheme(flightNameLabel);
        add(flightNameLabel, gbc);
    gbc.gridx = 1; flightNameField = new JTextField(15); 
    ThemeManager.applyDarkSurfaceTextFieldTheme(flightNameField);
        add(flightNameField, gbc);

        // Row 2: Airline
        gbc.gridx = 0; gbc.gridy = ++row; 
        JLabel airlineLabel = new JLabel("Airline:");
        ThemeManager.applyDarkLabelTheme(airlineLabel);
        add(airlineLabel, gbc);
        gbc.gridx = 1; comboAirline = new JComboBox<>(); 
        ThemeManager.applyDarkComboBoxTheme(comboAirline);
        add(comboAirline, gbc);

        // Row 3: Source
        gbc.gridx = 0; gbc.gridy = ++row; 
        JLabel sourceLabel = new JLabel("Source:");
        ThemeManager.applyDarkLabelTheme(sourceLabel);
        add(sourceLabel, gbc);
        gbc.gridx = 1; comboSource = new JComboBox<>(); 
        ThemeManager.applyDarkComboBoxTheme(comboSource);
        add(comboSource, gbc);

        // Row 4: Destination
        gbc.gridx = 0; gbc.gridy = ++row; 
        JLabel destinationLabel = new JLabel("Destination:");
        ThemeManager.applyDarkLabelTheme(destinationLabel);
        add(destinationLabel, gbc);
        gbc.gridx = 1; comboDestination = new JComboBox<>(); 
        ThemeManager.applyDarkComboBoxTheme(comboDestination);
        add(comboDestination, gbc);

        // Row 5: Departure Time 
        gbc.gridx = 0; gbc.gridy = ++row; 
        JLabel departureLabel = new JLabel("Departure Time (YYYY-MM-DD HH:MM):");
        ThemeManager.applyDarkLabelTheme(departureLabel);
        add(departureLabel, gbc);
    gbc.gridx = 1; departureTimeField = new JTextField(15); 
    ThemeManager.applyDarkSurfaceTextFieldTheme(departureTimeField);
        add(departureTimeField, gbc);

        // Row 6: Arrival Time 
        gbc.gridx = 0; gbc.gridy = ++row; 
        JLabel arrivalLabel = new JLabel("Arrival Time (YYYY-MM-DD HH:MM):");
        ThemeManager.applyDarkLabelTheme(arrivalLabel);
        add(arrivalLabel, gbc);
    gbc.gridx = 1; arrivalTimeField = new JTextField(15); 
    ThemeManager.applyDarkSurfaceTextFieldTheme(arrivalTimeField);
        add(arrivalTimeField, gbc);

        // Row 7: Base Price
        gbc.gridx = 0; gbc.gridy = ++row; 
        JLabel priceLabel = new JLabel("Base Price:");
        ThemeManager.applyDarkLabelTheme(priceLabel);
        add(priceLabel, gbc);
    gbc.gridx = 1; priceField = new JTextField(15); 
    ThemeManager.applyDarkSurfaceTextFieldTheme(priceField);
        add(priceField, gbc);

        // Row 8: Total Seats (NEW FIELD ADDED TO MATCH SQL SCHEMA)
        gbc.gridx = 0; gbc.gridy = ++row; 
        JLabel totalSeatsLabel = new JLabel("Total Seats:");
        ThemeManager.applyDarkLabelTheme(totalSeatsLabel);
        add(totalSeatsLabel, gbc);
    gbc.gridx = 1; totalSeatsField = new JTextField(15); 
    ThemeManager.applyDarkSurfaceTextFieldTheme(totalSeatsField);
        add(totalSeatsField, gbc);
        
        // Row 9: Seats Available 
        gbc.gridx = 0; gbc.gridy = ++row; 
        JLabel seatsLabel = new JLabel("Seats Available:");
        ThemeManager.applyDarkLabelTheme(seatsLabel);
        add(seatsLabel, gbc);
    gbc.gridx = 1; seatsAvailableField = new JTextField(15); 
    ThemeManager.applyDarkSurfaceTextFieldTheme(seatsAvailableField);
        add(seatsAvailableField, gbc);
        
        // --- Existing Flights Display Section (Moved to side) ---
        
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridwidth = 2; 
        JLabel existingLabel = new JLabel("Existing Flights for Route:");
        ThemeManager.applyDarkLabelTheme(existingLabel);
        add(existingLabel, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridheight = 4; 
        gbc.weighty = 1.0; 
        comboFlights = new JComboBox<>();
        ThemeManager.applyDarkComboBoxTheme(comboFlights);
        comboFlights.setPreferredSize(new Dimension(250, 100)); 
        add(new JScrollPane(comboFlights), gbc);
        
        gbc.gridheight = 1; 
        gbc.weighty = 0.0;  

        // Row 10: Save Button
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveBtn = new JButton("Save New Flight Schedule");
        saveBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(250, 40));
        add(saveBtn, gbc);

        // Load dropdowns
        loadAirlines();
        loadSourceDestinations();

        // 🔄 When source or destination changes → fetch flights
        comboSource.addActionListener(e -> loadAvailableFlights());
        comboDestination.addActionListener(e -> loadAvailableFlights());

        saveBtn.addActionListener(e -> {
            if (flightCodeField.getText().isEmpty() || flightNameField.getText().isEmpty() ||
                departureTimeField.getText().isEmpty() || arrivalTimeField.getText().isEmpty() ||
                priceField.getText().isEmpty() || seatsAvailableField.getText().isEmpty() ||
                totalSeatsField.getText().isEmpty() || // Check the new field
                comboAirline.getSelectedItem() == null ||
                comboSource.getSelectedItem() == null ||
                comboDestination.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!isValidData()) return;

            if (saveFlightToDatabase()) {
                ThemeManager.showInfo(this, "Flight Details Added Successfully!");
                // Optionally reload flights after saving
                loadAvailableFlights(); 
                // Don't dispose yet if admin might want to add another flight
            } else {
                ThemeManager.showError(this, "Failed to add flight. Check console for SQL error.");
            }
        });

        // FORCE white text on all labels to fix visibility issue
        ThemeManager.forceHighContrastTextInContainer(this);

        setVisible(true);
    }
    
    // 🔍 Input Validation (Updated to check total_seats)
    private boolean isValidData() {
        try {
            // Validate Price
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // Validate Seats
            int availableSeats = Integer.parseInt(seatsAvailableField.getText());
            int totalSeats = Integer.parseInt(totalSeatsField.getText());
            
            if (totalSeats <= 0) {
                 JOptionPane.showMessageDialog(this, "Total Seats must be a positive integer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (availableSeats > totalSeats) {
                 JOptionPane.showMessageDialog(this, "Available Seats cannot exceed Total Seats.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (availableSeats < 0) {
                 JOptionPane.showMessageDialog(this, "Available Seats cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Basic time format check (using DATETIME format YYYY-MM-DD HH:MM:SS)
            // Note: If you only use HH:MM in the field, MySQL might reject DATETIME type insertion
            // We simplify the regex check but remind the user of the format needed for MySQL DATETIME
            String timeRegex = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$";
            if (!departureTimeField.getText().matches(timeRegex) || !arrivalTimeField.getText().matches(timeRegex)) {
                JOptionPane.showMessageDialog(this, "Time must be in YYYY-MM-DD HH:MM format (e.g., 2024-10-30 14:30).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price, Total Seats, and Available Seats must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // 🛫 Load Airlines Dropdown (Updated to fetch ID)
    private void loadAirlines() {
        airlineNameToIdMap.clear(); // Reset map
        comboAirline.removeAllItems(); // Clear ComboBox

        // Query to fetch both ID and Name
       // Query the lowercase `airlines` table created by init_users.sql
       String query = "SELECT id, airline_name FROM airlines";
       try (Connection con = DBConnection.getConnection();
           PreparedStatement pst = con.prepareStatement(query);
           ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                String name = rs.getString("airline_name");
                int id = rs.getInt("id");
                comboAirline.addItem(name);
                airlineNameToIdMap.put(name, id); // Store the ID associated with the name
            }
            
            if (comboAirline.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No Airlines found. Please add airlines to the 'Airlines' table first.", "Data Missing", JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading Airlines. Check if 'Airlines' table exists and has 'airline_id' and 'airline_name' columns.", "DB Load Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 🗺️ Load distinct Sources and Destinations (No change needed)
    private void loadSourceDestinations() {
        // ... (existing implementation is fine)
    try (Connection con = DBConnection.getConnection()) {
            // Load from the 'flight' table as before, for existing destinations
            PreparedStatement pstSrc = con.prepareStatement("SELECT DISTINCT source FROM flight");
            PreparedStatement pstDest = con.prepareStatement("SELECT DISTINCT destination FROM flight");

            ResultSet rsSrc = pstSrc.executeQuery();
            comboSource.removeAllItems();
            while (rsSrc.next()) comboSource.addItem(rsSrc.getString("source"));

            ResultSet rsDest = pstDest.executeQuery();
            comboDestination.removeAllItems();
            while (rsDest.next()) comboDestination.addItem(rsDest.getString("destination"));
        } catch (SQLException e) {
             // This is fine if the 'flight' table is empty.
             // e.g. If you run this file before creating/populating the flight table, it will print stack trace but not crash the UI.
             // e.printStackTrace(); 
        }
    }

    // 🧭 Load Available Flights dynamically (No change needed)
    private void loadAvailableFlights() {
        // ... (existing implementation is fine)
        comboFlights.removeAllItems();
        String source = (String) comboSource.getSelectedItem();
        String destination = (String) comboDestination.getSelectedItem();

        if (source == null || destination == null) return;

       String query = "SELECT flight_code, flight_name, price, seats_available FROM flight WHERE source=? AND destination=?";
       try (Connection con = DBConnection.getConnection();
           PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, source);
            pst.setString(2, destination);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String flightInfo = String.format("%s - %s (Price: %.2f, Seats: %d)",
                    rs.getString("flight_code"),
                    rs.getString("flight_name"),
                    rs.getDouble("price"),
                    rs.getInt("seats_available")
                );
                comboFlights.addItem(flightInfo);
            }

            if (comboFlights.getItemCount() == 0) {
                comboFlights.addItem("❌ No flights available for this route");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 💾 Insert new Flight into database (CRITICALLY UPDATED to use airline_id and total_seats)
    private boolean saveFlightToDatabase() {
        String selectedAirlineName = (String) comboAirline.getSelectedItem();
        Integer airlineId = airlineNameToIdMap.get(selectedAirlineName);

        if (airlineId == null) {
            JOptionPane.showMessageDialog(this, "Internal Error: Could not find Airline ID for the selected airline.", "Logic Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // CORRECT SQL: Using airline_id, departure_time, arrival_time, price, seats_available, and total_seats
        String sql = "INSERT INTO flight (flight_code, flight_name, source, destination, airline_id, departure_time, arrival_time, price, seats_available, total_seats) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

       try (Connection conn = DBConnection.getConnection();
           PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, flightCodeField.getText());
            pstmt.setString(2, flightNameField.getText());
            pstmt.setString(3, (String) comboSource.getSelectedItem());
            pstmt.setString(4, (String) comboDestination.getSelectedItem());
            
            // PARAMETER 5: Use the ID, not the name
            pstmt.setInt(5, airlineId); 
            
            // PARAMETERS 6 & 7: Time (must match DATETIME format in MySQL)
            pstmt.setString(6, departureTimeField.getText() + ":00"); // Append seconds for proper DATETIME
            pstmt.setString(7, arrivalTimeField.getText() + ":00"); // Append seconds
            
            // PARAMETERS 8, 9, 10: Price and Seats
            pstmt.setDouble(8, Double.parseDouble(priceField.getText()));
            pstmt.setInt(9, Integer.parseInt(seatsAvailableField.getText()));
            pstmt.setInt(10, Integer.parseInt(totalSeatsField.getText())); // Insert total_seats

            return pstmt.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("SQL Error while saving flight: " + ex.getMessage());
            // Most likely cause now: UNIQUE key violation (duplicate flight_code) or a formatting error.
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FlightInfo::new);
    }
}