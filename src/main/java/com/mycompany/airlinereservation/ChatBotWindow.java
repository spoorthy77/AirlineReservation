package com.mycompany.airlinereservation;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;

/**
 * ChatBotWindow.java - Chat Window for Airline Reservation Chatbot
 */
public class ChatBotWindow extends JFrame {
    
    private JTextPane chatDisplay;
    private JTextField inputField;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private final ChatBot chatBot;
    private final String username;
    
    private static final Color PRIMARY_COLOR = new Color(74, 166, 240);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
    private static final Color USER_BUBBLE = new Color(220, 248, 198);
    private static final Color BOT_BUBBLE = new Color(255, 255, 255);
    
    public ChatBotWindow(String username) {
        this.username = username;
        this.chatBot = new ChatBotEnhanced(username);
        
        setupWindow();
        setupUI();
        
        setVisible(true);
    }
    
    private void setupWindow() {
        setTitle("💬 Chat Support - " + username);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(false);
    }
    
    private void setupUI() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Airline Chat Support");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Chat display area
        chatDisplay = new JTextPane();
        chatDisplay.setEditable(false);
        chatDisplay.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chatDisplay.setBackground(BACKGROUND_COLOR);
        chatDisplay.setMargin(new Insets(10, 10, 10, 10));
        
        scrollPane = new JScrollPane(chatDisplay);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        
        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(BACKGROUND_COLOR);
        
        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        inputField.setBackground(Color.WHITE);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 11));
        sendButton.setBackground(PRIMARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(70, 32));
        sendButton.addActionListener(e -> sendMessage());
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(SECONDARY_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(PRIMARY_COLOR);
            }
        });
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Add welcome message
        addMessage("👋 Welcome! How can we help you today?\n\n" +
                  "You can ask about:\n" +
                  "✈️ Flight availability\n" +
                  "🎫 Book a flight\n" +
                  "📋 View your bookings\n" +
                  "💳 Payments", false);
        
        inputField.requestFocus();
    }
    
    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) {
            return;
        }
        
        addMessage(userInput, true);
        inputField.setText("");
        
        // Process message in background thread
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return chatBot.processMessage(userInput);
            }
            
            @Override
            protected void done() {
                try {
                    String response = get();
                    addMessage(response, false);
                } catch (Exception e) {
                    addMessage("❌ Error: " + e.getMessage(), false);
                }
            }
        };
        
        worker.execute();
    }
    
    private void addMessage(String message, boolean isUser) {
        try {
            StyledDocument doc = chatDisplay.getStyledDocument();
            
            // Create bubble style
            SimpleAttributeSet bubbleStyle = new SimpleAttributeSet();
            Color bubbleColor = isUser ? USER_BUBBLE : BOT_BUBBLE;
            StyleConstants.setBackground(bubbleStyle, bubbleColor);
            StyleConstants.setSpaceAbove(bubbleStyle, 5);
            StyleConstants.setSpaceBelow(bubbleStyle, 5);
            StyleConstants.setLeftIndent(bubbleStyle, 10);
            StyleConstants.setRightIndent(bubbleStyle, 10);
            
            // Add alignment
            SimpleAttributeSet alignment = new SimpleAttributeSet();
            StyleConstants.setAlignment(alignment, isUser ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
            
            // Insert message
            String prefix = isUser ? "You: " : "Bot: ";
            doc.insertString(doc.getLength(), prefix + message + "\n\n", bubbleStyle);
            
            // Auto-scroll to bottom
            SwingUtilities.invokeLater(() -> {
                try {
                    JScrollBar vertical = scrollPane.getVerticalScrollBar();
                    if (vertical != null) {
                        vertical.setValue(vertical.getMaximum());
                    }
                } catch (Exception ex) {
                    // Ignore scroll errors
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error adding message: " + e.getMessage());
        }
    }
}
