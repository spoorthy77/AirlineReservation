package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;

public class AddPayment extends JFrame {


    private JTextField txtPnr, txtAmount;
    private JComboBox<String> comboMethod, comboStatus;

    public AddPayment() {
        setTitle("💳 Record Payment");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Record Transaction Details", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        int row = 1;
        
        // PNR (Ticket ID)
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row++;
        add(new JLabel("PNR (Ticket ID):"), gbc);
        txtPnr = new JTextField(20);
        gbc.gridx = 1;
        add(txtPnr, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = row++;
        add(new JLabel("Amount (₹):"), gbc);
        txtAmount = new JTextField(20);
        gbc.gridx = 1;
        add(txtAmount, gbc);

        // Payment Method
        gbc.gridx = 0; gbc.gridy = row++;
        add(new JLabel("Method:"), gbc);
        comboMethod = new JComboBox<>(new String[]{"Credit/Debit Card", "UPI", "Net Banking", "Wallet"});
        gbc.gridx = 1;
        add(comboMethod, gbc);

        // Payment Status
        gbc.gridx = 0; gbc.gridy = row++;
        add(new JLabel("Status:"), gbc);
        comboStatus = new JComboBox<>(new String[]{"Completed", "Pending", "Failed"});
        gbc.gridx = 1;
        add(comboStatus, gbc);

        // Button
        JButton btnRecord = new JButton("Record Payment");
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        btnRecord.addActionListener(this::handleRecordPayment);
        add(btnRecord, gbc);

        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return com.mycompany.airlinereservation.DBConnection.getConnection();
    }

    private void handleRecordPayment(ActionEvent e) {
        String pnr = txtPnr.getText().trim().toUpperCase();
        String amountStr = txtAmount.getText().trim();
        String method = (String) comboMethod.getSelectedItem();
        String status = (String) comboStatus.getSelectedItem();
        double amount;

        if (pnr.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "PNR and Amount are mandatory.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // SQL to insert payment record. transaction_date uses NOW() to record current time.
        String sql = "INSERT INTO payments (pnr, amount, payment_method, payment_status, transaction_date) VALUES (?, ?, ?, ?, NOW())";

        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            // Validate PNR exists in the ticket table (Good practice, but skipped for simplicity)

            pst.setString(1, pnr);
            pst.setDouble(2, amount);
            pst.setString(3, method);
            pst.setString(4, status);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Payment recorded successfully for PNR " + pnr + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear fields
                txtPnr.setText("");
                txtAmount.setText("");
                comboMethod.setSelectedIndex(0);
                comboStatus.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to record payment.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLIntegrityConstraintViolationException ex) {
             // Likely a foreign key constraint failure if PNR does not exist in the ticket table
             JOptionPane.showMessageDialog(this, "Invalid PNR. Please ensure the ticket exists.", "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddPayment::new);
    }
}