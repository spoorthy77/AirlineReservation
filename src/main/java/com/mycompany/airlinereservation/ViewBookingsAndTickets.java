package com.mycompany.airlinereservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

/**
 * ViewBookingsAndTickets.java
 * 
 * Displays all bookings and tickets for the currently logged-in user.
 * 
 * ✅ Session-Based Filtering:
 *    - Shows ONLY tickets belonging to the logged-in user
 *    - Uses: WHERE ticket.username = ?
 *    - Gets username from: SessionManager.getInstance().getCurrentUser()
 * 
 * Features:
 * - View all past and current bookings
 * - See ticket status (CONFIRMED, CANCELLED, COMPLETED)
 * - View journey details
 * - Cancel tickets (if allowed)
 */
public class ViewBookingsAndTickets extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/airline_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Spoorthi@2005";

    private JTable bookingsTable;
    private DefaultTableModel model;
    private String currentUsername;
    private JButton viewDetailsBtn, cancelTicketBtn, refreshBtn;

    public ViewBookingsAndTickets() {
        // 🔑 Get the currently logged-in user from session
        currentUsername = SessionManager.getInstance().getCurrentUser();
        
        if (currentUsername == null || currentUsername.isEmpty()) {
            JOptionPane.showMessageDialog(null, "❌ Please log in first to view bookings.", 
                                        "Session Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setTitle("📋 My Bookings & Tickets - " + currentUsername);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(ThemeManager.DARK_BG);

        // --- Top Panel (Buttons) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        ThemeManager.applyDarkPanelTheme(topPanel);

        refreshBtn = new JButton("🔄 Refresh");
        viewDetailsBtn = new JButton("📖 View Details");
        cancelTicketBtn = new JButton("❌ Cancel Ticket");

        ThemeManager.applyProfessionalButtonTheme(refreshBtn, false);
        ThemeManager.applyProfessionalButtonTheme(viewDetailsBtn, false);
        ThemeManager.applyProfessionalButtonTheme(cancelTicketBtn, true);

        topPanel.add(refreshBtn);
        topPanel.add(viewDetailsBtn);
        topPanel.add(cancelTicketBtn);

        // --- Table Section ---
        String[] columns = {"PNR", "Flight Code", "Flight Name", "Source", "Destination", 
                           "Date", "Class", "Status", "Booking Date", "Price"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only table
            }
        };

        bookingsTable = new JTable(model);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ThemeManager.applyUniversalTableContrastTheme(bookingsTable);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);

        // --- Layout ---
        setLayout(new BorderLayout(10, 10));
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Event Listeners ---
        refreshBtn.addActionListener(e -> loadUserBookings());
        viewDetailsBtn.addActionListener(this::handleViewDetails);
        cancelTicketBtn.addActionListener(this::handleCancelTicket);

        // --- Initial Load ---
        loadUserBookings();

        setVisible(true);
    }

    /**
     * Load all bookings for the currently logged-in user
     * ✅ KEY: Uses WHERE username = ? to filter by current user
     */
    private void loadUserBookings() {
        model.setRowCount(0); // Clear table

        String sql = "SELECT t.pnr, t.flight_code, f.flight_name, f.source, f.destination, " +
                     "t.journey_date, t.class, t.status, t.booking_date, f.price " +
                     "FROM ticket t " +
                     "JOIN flight f ON t.flight_code = f.flight_code " +
                     "WHERE t.username = ? " +  // 🔑 FILTER: Only current user's bookings
                     "ORDER BY t.booking_date DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the current user's username as the filter
            pstmt.setString(1, currentUsername);

            try (ResultSet rs = pstmt.executeQuery()) {
                int rowCount = 0;
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("pnr"),
                            rs.getString("flight_code"),
                            rs.getString("flight_name"),
                            rs.getString("source"),
                            rs.getString("destination"),
                            rs.getDate("journey_date"),
                            rs.getString("class"),
                            rs.getString("status"),
                            rs.getTimestamp("booking_date"),
                            String.format("₹%.2f", rs.getDouble("price"))
                    });
                    rowCount++;
                }

                if (rowCount == 0) {
                    JOptionPane.showMessageDialog(this, 
                        "📭 No bookings found. You haven't booked any flights yet.", 
                        "No Bookings", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error loading bookings: " + ex.getMessage());
            ThemeManager.showError(this, "Error loading bookings: " + ex.getMessage());
        }
    }

    /**
     * View detailed information of a selected booking
     */
    private void handleViewDetails(ActionEvent ignored) {
        int selectedRow = bookingsTable.getSelectedRow();
        
        if (selectedRow < 0) {
            ThemeManager.showWarning(this, "Please select a booking to view details.");
            return;
        }

        String pnr = (String) model.getValueAt(selectedRow, 0);
        
        // Verify that the PNR belongs to the current user before showing details
        String sql = "SELECT t.*, f.flight_name, f.source, f.destination, " +
                     "f.departure_time, f.arrival_time " +
                     "FROM ticket t " +
                     "JOIN flight f ON t.flight_code = f.flight_code " +
                     "WHERE t.pnr = ? AND t.username = ?";  // 🔑 Double-check: both PNR and username

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pnr);
            pstmt.setString(2, currentUsername);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    showDetailDialog(rs);
                } else {
                    ThemeManager.showError(this, "❌ Booking not found or access denied.");
                }
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error fetching booking details: " + ex.getMessage());
            ThemeManager.showError(this, "Error fetching details: " + ex.getMessage());
        }
    }

    /**
     * Display booking details in a dialog
     */
    private void showDetailDialog(ResultSet rs) throws SQLException {
        StringBuilder details = new StringBuilder();
        details.append("📋 BOOKING DETAILS\n");
        details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        details.append("✈️  FLIGHT INFORMATION\n");
        details.append("  PNR: ").append(rs.getString("pnr")).append("\n");
        details.append("  Flight Code: ").append(rs.getString("flight_code")).append("\n");
        details.append("  Flight Name: ").append(rs.getString("flight_name")).append("\n");
        details.append("  Route: ").append(rs.getString("source")).append(" → ")
                .append(rs.getString("destination")).append("\n\n");

        details.append("👤 PASSENGER INFORMATION\n");
        details.append("  Name: ").append(rs.getString("name")).append("\n");
        details.append("  Aadhar: ").append(rs.getString("aadhar_no")).append("\n");
        details.append("  Gender: ").append(rs.getString("gender")).append("\n\n");

        details.append("📅 JOURNEY DETAILS\n");
        details.append("  Journey Date: ").append(rs.getDate("journey_date")).append("\n");
        details.append("  Booking Date: ").append(rs.getTimestamp("booking_date")).append("\n");
        details.append("  Class: ").append(rs.getString("class")).append("\n");
        details.append("  Status: ").append(rs.getString("status")).append("\n");

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(40, 40, 40));
        textArea.setForeground(Color.WHITE);
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Booking Details", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Cancel a selected ticket
     */
    private void handleCancelTicket(ActionEvent ignored) {
        int selectedRow = bookingsTable.getSelectedRow();
        
        if (selectedRow < 0) {
            ThemeManager.showWarning(this, "Please select a booking to cancel.");
            return;
        }

        String pnr = (String) model.getValueAt(selectedRow, 0);
        String status = (String) model.getValueAt(selectedRow, 7);

        // Check if ticket can be cancelled
        if ("CANCELLED".equals(status)) {
            ThemeManager.showWarning(this, "❌ This ticket is already cancelled.");
            return;
        }

        if ("COMPLETED".equals(status)) {
            ThemeManager.showWarning(this, "❌ Cannot cancel completed journeys.");
            return;
        }

        // Confirm cancellation
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel PNR: " + pnr + "?\n" +
            "A refund will be processed to your account.",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // 🔑 Cancel ticket with double-check (PNR + username)
        String sql = "UPDATE ticket SET status = 'CANCELLED', cancellation_date = NOW() " +
                     "WHERE pnr = ? AND username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pnr);
            pstmt.setString(2, currentUsername);

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "✅ Ticket cancelled successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUserBookings(); // Refresh the table
            } else {
                ThemeManager.showError(this, "❌ Failed to cancel ticket. Access denied or ticket not found.");
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error cancelling ticket: " + ex.getMessage());
            ThemeManager.showError(this, "Error cancelling ticket: " + ex.getMessage());
        }
    }
}
