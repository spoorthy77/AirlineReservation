package com.mycompany.airlinereservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple viewer for the `bookings` table.
 * Uses DBConnection.getConnection() so it shares the project's DB config.
 * 
 * Enhanced for user-specific data isolation:
 * - Shows only tickets and bookings for the currently logged-in user
 * - Retrieves username from SessionManager
 * - Displays user-specific booking history and payment information
 */
public class ViewBookings extends JFrame {

    private DefaultTableModel model;
    private JTable table;
    private String currentUsername;

    public ViewBookings() {
        // Get the current logged-in user from SessionManager
        currentUsername = SessionManager.getInstance().getCurrentUser();
        
        // If no user is logged in, show a warning and return
        if (currentUsername == null || currentUsername.isEmpty()) {
            currentUsername = "Unknown User";
            System.err.println("❌ Warning: ViewBookings opened without a logged-in user!");
        }
        System.out.println("✅ ViewBookings initialized - Filtering by username: '" + currentUsername + "'");  // Debug log
        setTitle("✈️ View Bookings / Tickets - User: " + currentUsername);
        setSize(900, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(ThemeManager.DARK_BG);

        // Show PNR (ticket id) and ticket-specific columns by reading from `ticket` table
        model = new DefaultTableModel(new String[]{"PNR", "Flight Code", "Flight Name", "Source", "Destination", "Date of Travel", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        
        // Apply dark theme to table
        ThemeManager.applyDarkTableTheme(table);

        JScrollPane scroll = new JScrollPane(table);

        JButton btnRefresh = new JButton("Refresh");
        ThemeManager.applyLightButtonTheme(btnRefresh);  // Switch to light theme for better contrast
        btnRefresh.setForeground(new Color(0, 0, 0));  // Pure black text for maximum contrast
        btnRefresh.addActionListener(this::loadBookings);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ThemeManager.applyDarkPanelTheme(top);
        top.add(btnRefresh);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        loadBookings(null);

        setVisible(true);
    }

    // Loads tickets and joins with flight and payments to show PNR and amounts
    // ✅ ENHANCED: Shows only tickets for the current customer (filtered by username)
    // The ticket table now stores username directly, so we filter by that
    private void loadBookings(ActionEvent e) {
        model.setRowCount(0);

        // ✅ SIMPLIFIED Query: Filter directly by username in ticket table
        String sql = "SELECT t.pnr, t.flight_code, IFNULL(t.flight_name, '') AS flight_name, t.source, t.destination, t.date_of_travel, IFNULL(p.amount, 0) AS amount "
            + "FROM ticket t "
            + "LEFT JOIN payments p ON t.pnr = p.pnr "
            + "WHERE t.username = ? "
            + "ORDER BY t.date_of_travel DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            // Set the current username parameter
            System.out.println("🔍 DEBUG: Querying tickets for username: '" + currentUsername + "'");  // Debug log
            pst.setString(1, currentUsername);
            
            try (ResultSet rs = pst.executeQuery()) {
                int rowCount = 0;
                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getString("pnr"),
                        rs.getString("flight_code"),
                        rs.getString("flight_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("date_of_travel"),
                        rs.getDouble("amount")
                    });
                    rowCount++;
                }
                
                if (rowCount == 0) {
                    ThemeManager.showInfo(this, "No bookings found for user: " + currentUsername);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            ThemeManager.showError(this, "Error loading bookings: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewBookings::new);
    }
}
