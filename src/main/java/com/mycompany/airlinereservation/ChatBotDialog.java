package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ChatBotDialog.java - Popup Dialog for Chatbot Interface
 * 
 * Features:
 * - Floating dialog window
 * - Can be positioned at bottom-right
 * - Resizable and draggable
 * - Minimize/Maximize controls
 * - Modern styling with rounded edges
 */
public class ChatBotDialog extends JFrame {
    
    private ChatBotUI chatBotUI;
    private JButton minimizeButton, closeButton;
    private final JPanel titleBar;
    
    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 500;
    private static final int TITLE_BAR_HEIGHT = 35;
    
    public ChatBotDialog(String username, JFrame parentFrame) {
        setTitle("Airline Chatbot Assistant");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setUndecorated(true);  // Remove default window decorations
        setAlwaysOnTop(false);
        setResizable(true);
        
        // Initialize chatbot with NLP enhancement
        ChatBot chatBot = new ChatBotEnhanced(username);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 255));
        
        // Title bar
        titleBar = createTitleBar();
        mainPanel.add(titleBar, BorderLayout.NORTH);
        
        // Chatbot UI
        chatBotUI = new ChatBotUI(chatBot);
        mainPanel.add(chatBotUI, BorderLayout.CENTER);
        
        // Set content pane
        setContentPane(mainPanel);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        
        // Position at bottom-right of parent frame
        if (parentFrame != null) {
            int parentX = parentFrame.getX();
            int parentY = parentFrame.getY();
            int parentWidth = parentFrame.getWidth();
            int parentHeight = parentFrame.getHeight();
            
            int x = parentX + parentWidth - DEFAULT_WIDTH - 20;
            int y = parentY + parentHeight - DEFAULT_HEIGHT - 20;
            
            setLocation(x, y);
        } else {
            setLocationRelativeTo(null);
        }
        
    // Add rounded border effect (visual only)
    applyRoundedBorder(mainPanel);
    }
    
    /**
     * Create custom title bar with controls
     */
    private JPanel createTitleBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(74, 166, 240));
        panel.setPreferredSize(new Dimension(0, TITLE_BAR_HEIGHT));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 210, 225)));
        
        // Title label
        JLabel titleLabel = new JLabel("  💬 Chatbot Assistant");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(Color.WHITE);
        
        // Mouse listener for dragging
        MouseAdapter dragListener = new MouseAdapter() {
            private int lastX = 0;
            private int lastY = 0;
            
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getXOnScreen();
                lastY = e.getYOnScreen();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                int currentX = e.getXOnScreen();
                int currentY = e.getYOnScreen();
                
                int deltaX = currentX - lastX;
                int deltaY = currentY - lastY;
                
                setLocation(getX() + deltaX, getY() + deltaY);
                
                lastX = currentX;
                lastY = currentY;
            }
        };
        
        panel.addMouseListener(dragListener);
        panel.addMouseMotionListener(dragListener);
        titleLabel.addMouseListener(dragListener);
        titleLabel.addMouseMotionListener(dragListener);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setOpaque(false);
        
    // Minimize button
    minimizeButton = createIconButton("−");
    minimizeButton.addActionListener(e -> { e.getSource(); setVisible(false); });
        buttonPanel.add(minimizeButton);
        
        // Close button
    closeButton = createIconButton("×");
    closeButton.addActionListener(e -> { e.getSource(); setVisible(false); });
        buttonPanel.add(closeButton);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Create icon button for title bar
     */
    private JButton createIconButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(74, 166, 240));
        button.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
                button.setContentAreaFilled(true);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });
        
        return button;
    }
    
    /**
     * Apply rounded border effect
     */
    private void applyRoundedBorder(JPanel panel) {
        panel.setOpaque(false);
    }
    
    /**
     * Get the chatbot UI component
     */
    public ChatBotUI getChatBotUI() {
        return chatBotUI;
    }
    
    /**
     * Show the dialog
     */
    public void showDialog() {
        setVisible(true);
        if (chatBotUI != null) {
            chatBotUI.focusInput();
        }
    }
    
    /**
     * Hide the dialog
     */
    public void hideDialog() {
        setVisible(false);
    }
    
    /**
     * Toggle dialog visibility
     */
    public void toggleDialog() {
        setVisible(!isVisible());
    }
}
