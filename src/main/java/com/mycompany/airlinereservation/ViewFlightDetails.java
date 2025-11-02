package com.mycompany.airlinereservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewFlightDetails extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(ViewFlightDetails.class.getName());

    private JTable flightTable;
    private JComboBox<String> comboSource;
    private JComboBox<String> comboDestination;
    private DefaultTableModel model;
    
    // Current logged-in user for session-based operations
    private final String currentUsername;

    public ViewFlightDetails() {
        String username = SessionManager.getInstance().getCurrentUser();
        this.currentUsername = (username != null && !username.isEmpty()) ? username : "Unknown User";
        if ("Unknown User".equals(this.currentUsername)) {
            LOGGER.warning("ViewFlightDetails opened without a logged-in user!");
        }
        
        setTitle("✈️ View Available Flights - User: " + this.currentUsername);
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIManager.getColor("Panel.background"));

        // Initialize UI components
        this.model = new DefaultTableModel(new String[]{"Flight Code", "Flight Name", "Source", "Destination", "Seats", "Price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        this.flightTable = new JTable(model);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightTable.setDefaultEditor(Object.class, null);

        this.comboSource = new JComboBox<>();
        this.comboSource.addItem("Select Source");
        this.comboDestination = new JComboBox<>();
        this.comboDestination.addItem("Select Destination");
        
        // --- North Panel (Filter Section) ---
        JPanel topPanel = new JPanel(new FlowLayout());
        ThemeManager.applyDarkPanelTheme(topPanel);

        // Apply universal combo box contrast for maximum readability
        ThemeManager.applyUniversalComboBoxContrastTheme(comboSource);
        ThemeManager.applyUniversalComboBoxContrastTheme(comboDestination);

        JButton searchBtn = new JButton("🔍 Search Flights");
        // Apply professional button themes with proper hierarchy
        ThemeManager.applyProfessionalButtonTheme(searchBtn, false);  // Secondary action

        JLabel lblSource = new JLabel("Source:");
        JLabel lblDest = new JLabel("Destination:");
        ThemeManager.applyProfessionalLabelTheme(lblSource, "body");
        ThemeManager.applyProfessionalLabelTheme(lblDest, "body");

        topPanel.add(lblSource);
        topPanel.add(comboSource);
        topPanel.add(lblDest);
        topPanel.add(comboDestination);
        topPanel.add(searchBtn);

        // Apply consistent spacing to the top panel
        ThemeManager.applyConsistentSpacing(topPanel);

        // --- Table Section ---
        String[] columns = {"Flight Code", "Flight Name", "Source", "Destination", "Seats", "Price"};
        model = new DefaultTableModel(columns, 0);
        flightTable = new JTable(model);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightTable.setDefaultEditor(Object.class, null);
        
        // Apply universal table contrast theme for maximum readability
        ThemeManager.applyUniversalTableContrastTheme(flightTable);

        JScrollPane scrollPane = new JScrollPane(flightTable);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadSourceDestinations();
        fetchFlightData(); // initially show all flights

        searchBtn.addActionListener(e -> handleSearch());

        setVisible(true);
    }

    // 🔹 Load distinct source and destination for filters
    private void loadSourceDestinations() {
        String sqlSrc = "SELECT DISTINCT source FROM flight";
        String sqlDest = "SELECT DISTINCT destination FROM flight";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement pstSrc = conn.prepareStatement(sqlSrc);
                 ResultSet rsSrc = pstSrc.executeQuery()) {
                while (rsSrc.next()) {
                    comboSource.addItem(rsSrc.getString("source"));
                }
            }

            try (PreparedStatement pstDest = conn.prepareStatement(sqlDest);
                 ResultSet rsDest = pstDest.executeQuery()) {
                while (rsDest.next()) {
                    comboDestination.addItem(rsDest.getString("destination"));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error loading source/destination data", ex);
            JOptionPane.showMessageDialog(this, 
                "Error loading locations: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🔹 Fetch and display all flights
    // ✅ ENHANCED: Also show which flights the current user has already booked
    private void fetchFlightData() {
        model.setRowCount(0);
        
        String sql = "SELECT f.flight_code, f.flight_name, f.source, f.destination, f.seats_available, f.price " +
                    "FROM flight f " +
                    "ORDER BY f.flight_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("flight_code"),
                    rs.getString("flight_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getInt("seats_available"),
                    rs.getDouble("price")
                });
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error loading flight data", ex);
            JOptionPane.showMessageDialog(this, 
                "Error loading flight data: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🔹 Handle Search Button
    private void handleSearch() {
        String source = (String) comboSource.getSelectedItem();
        String destination = (String) comboDestination.getSelectedItem();

        // Validate that both source and destination are selected (not the "Select" default items)
        if (source == null || source.trim().isEmpty() || source.equals("Select Source") || 
            destination == null || destination.trim().isEmpty() || destination.equals("Select Destination")) {
            JOptionPane.showMessageDialog(this, 
                "Please select both Source and Destination.",
                "Input Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        model.setRowCount(0);
        
        String sql = "SELECT f.flight_code, f.flight_name, f.source, f.destination, f.seats_available, f.price " +
                    "FROM flight f " +
                    "WHERE f.source = ? AND f.destination = ? " +
                    "ORDER BY f.flight_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, source);
            pst.setString(2, destination);
            
            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("flight_code"),
                    rs.getString("flight_name"),
                    rs.getString("source"),
                    rs.getString("destination"),
                    rs.getInt("seats_available"),
                    rs.getDouble("price")
                });
            }

            if (model.getRowCount() == 0) {
                ThemeManager.showInfo(this, "No flights found for this route.");
            }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error searching flights", ex);
            JOptionPane.showMessageDialog(this,
                "Error searching flights: " + ex.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🔹 Handle Booking
    private void handleBooking() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a flight to book.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String flightCode = model.getValueAt(selectedRow, 0).toString();
        String flightName = model.getValueAt(selectedRow, 1).toString();
        String source = model.getValueAt(selectedRow, 2).toString();
        String destination = model.getValueAt(selectedRow, 3).toString();
        double price = Double.parseDouble(model.getValueAt(selectedRow, 5).toString());

        int confirm = JOptionPane.showConfirmDialog(this,
                """
                Book this flight?

                Flight: %s
                Route: %s → %s
                Price: ₹%.2f
                """.formatted(flightName, source, destination, price),
                "Confirm Booking",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            bookFlight(flightCode, price);
        }
    }

    // 🔹 Insert into Bookings table
    private void bookFlight(String flightCode, double price) {
        String checkSql = "SELECT seats_available FROM flight WHERE flight_code = ? FOR UPDATE";
        String insertSql = "INSERT INTO bookings (flight_code, booking_date, amount) VALUES (?, NOW(), ?)";
        String updateSql = "UPDATE flight SET seats_available = seats_available - 1 WHERE flight_code = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 0. Check availability
                try (PreparedStatement pstCheck = conn.prepareStatement(checkSql)) {
                    pstCheck.setString(1, flightCode);
                    try (ResultSet rs = pstCheck.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            ThemeManager.showError(this, "Flight not found. Cannot book.");
                            return;
                        }
                        int avail = rs.getInt("seats_available");
                        if (avail <= 0) {
                            conn.rollback();
                            ThemeManager.showError(this, "No seats available for this flight.");
                            return;
                        }
                    }
                }

                // 1. Insert booking
                try (PreparedStatement pstIns = conn.prepareStatement(insertSql)) {
                    pstIns.setString(1, flightCode);
                    pstIns.setDouble(2, price);
                    int rows = pstIns.executeUpdate();
                    if (rows <= 0) {
                        conn.rollback();
                        ThemeManager.showError(this, "Booking failed!");
                        return;
                    }
                }

                // 2. Decrement seats
                try (PreparedStatement pstUpd = conn.prepareStatement(updateSql)) {
                    pstUpd.setString(1, flightCode);
                    pstUpd.executeUpdate();
                }

                conn.commit();
                ThemeManager.showInfo(this, "✅ Booking Successful!");

            } catch (SQLException exInner) {
                try { conn.rollback(); } catch (SQLException ignore) {}
                exInner.printStackTrace();
                ThemeManager.showError(this, "Error during booking (rolled back): " + exInner.getMessage());
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            ThemeManager.showError(this, "Error during booking!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewFlightDetails::new);
    }
}
