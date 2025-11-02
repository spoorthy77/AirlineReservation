package com.mycompany.airlinereservation;
import java.sql.*;
import javax.swing.*;

public class Airlines extends javax.swing.JFrame {
    Connection con;
    PreparedStatement pst;
    private JTextField txtAirlineName;
    private JTextField txtRating;
    private JButton btnAdd;

    public Airlines() {
        setTitle("Add Airline");
        setSize(400, 200);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblName = new JLabel("Airline Name:");
        lblName.setBounds(30, 30, 100, 30);
        add(lblName);

        txtAirlineName = new JTextField();
        txtAirlineName.setBounds(150, 30, 200, 30);
        add(txtAirlineName);

        JLabel lblRating = new JLabel("Rating:");
        lblRating.setBounds(30, 70, 100, 30);
        add(lblRating);

        txtRating = new JTextField();
        txtRating.setBounds(150, 70, 200, 30);
        add(txtRating);

        btnAdd = new JButton("Add Airline");
        btnAdd.setBounds(130, 120, 120, 30);
        add(btnAdd);

        btnAdd.addActionListener(e -> addAirline());

        con = DBConnection.getConnection();
    }

    private void addAirline() {
        try {
            String name = txtAirlineName.getText();
            double rating = Double.parseDouble(txtRating.getText());

            pst = con.prepareStatement("INSERT INTO Airlines (airline_name, rating) VALUES (?, ?)");
            pst.setString(1, name);
            pst.setDouble(2, rating);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Airline added successfully!");
            txtAirlineName.setText("");
            txtRating.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding airline!");
        }
    }

    // 🟢 Add this to run directly from NetBeans
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new Airlines().setVisible(true);
        });
    }
}
