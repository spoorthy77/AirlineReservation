package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ChatBotIcon.java - Simple Floating Chatbot Icon
 * Appears in the bottom-right corner of the screen
 */
public class ChatBotIcon extends JButton {
    
    private static final int ICON_SIZE = 60;
    private static final Color BUTTON_COLOR = new Color(74, 166, 240);  // Blue
    private static final Color HOVER_COLOR = new Color(52, 152, 219);   // Darker blue
    private static final Color BORDER_COLOR = new Color(255, 255, 255, 200);
    
    private boolean isHovered = false;
    private ChatBotWindow chatWindow;
    
    public ChatBotIcon(String username) {
        // Setup button properties
        setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        setMinimumSize(new Dimension(ICON_SIZE, ICON_SIZE));
        setMaximumSize(new Dimension(ICON_SIZE, ICON_SIZE));
        
        // Remove default button styling
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add action listener to open chat window
        addActionListener(e -> {
            if (chatWindow == null || !chatWindow.isVisible()) {
                chatWindow = new ChatBotWindow(username);
                chatWindow.setVisible(true);
            } else {
                chatWindow.toFront();
                chatWindow.requestFocus();
            }
        });
        
        // Add hover effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        
        // Draw circular background
        Color bgColor = isHovered ? HOVER_COLOR : BUTTON_COLOR;
        g2d.setColor(bgColor);
        g2d.fillOval(x, y, size, size);
        
        // Draw border
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(BORDER_COLOR);
        g2d.drawOval(x, y, size, size);
        
        // Draw chat bubble icon (emoji)
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 32));
        FontMetrics fm = g2d.getFontMetrics();
        String icon = "💬";
        int iconX = x + (size - fm.stringWidth(icon)) / 2;
        int iconY = y + ((size - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(icon, iconX, iconY);
    }
}
