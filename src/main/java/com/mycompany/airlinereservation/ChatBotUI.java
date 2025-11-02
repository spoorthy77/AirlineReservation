package com.mycompany.airlinereservation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ChatBotUI.java - Modern Swing UI Components for Chatbot
 * 
 * Features:
 * - Scrollable chat history area
 * - Modern input field with send button
 * - Rounded edges and transparency effects
 * - Auto-scrolling to latest messages
 * - Chat bubble styling for messages
 */
public class ChatBotUI extends JPanel {
    
    private JTextPane chatHistoryPane;
    private JTextField messageInputField;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private final ChatBot chatBot;
    private Runnable onSendMessage;
    
    // Color scheme - Modern blue theme and styling
    private static final Color TEXT_COLOR = new Color(30, 30, 30);       // Dark text (#1E1E1E)
    
    public ChatBotUI(ChatBot chatBot) {
        this.chatBot = chatBot;
        initializeUI();
    }
    
    /**
     * Initialize the chatbot UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 248, 255)); // Light blue background (#F0F8FF)
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Chat history pane with HTML/CSS styling for chat bubbles
        chatHistoryPane = new JTextPane();
        chatHistoryPane.setEditable(false);
        chatHistoryPane.setContentType("text/html");
        chatHistoryPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatHistoryPane.setBackground(new Color(240, 248, 255)); // Light blue (#F0F8FF)
        chatHistoryPane.setForeground(TEXT_COLOR);
        chatHistoryPane.setMargin(new Insets(10, 10, 10, 10));
        
        // Initialize HTML document structure
        chatHistoryPane.setText("<html><body style='font-family:Segoe UI; font-size:13px; background-color:#F0F8FF; margin:0; padding:10px;'></body></html>");
        
        // Scroll pane with custom scrollbar
        scrollPane = new JScrollPane(chatHistoryPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 2)); // Blue border (#007BFF)
        scrollPane.setBackground(new Color(240, 248, 255));
        
        // Input panel
        JPanel inputPanel = createInputPanel();
        
        // Add components
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        
        // Initial welcome message
        appendMessage("Bot", "👋 Welcome to Airline Reservation Chatbot!\n\n" +
                        "I can help you with:\n" +
                        "✈️ View available flights\n" +
                        "🎫 Book flights\n" +
                        "📋 Check your bookings\n" +
                        "❌ Cancel bookings\n" +
                        "📄 Generate boarding passes\n" +
                        "💳 View payment details\n\n" +
                        "Type 'help' for more options or just chat naturally!", false);
    }
    
    /**
     * Create input panel with text field and send button (Blue theme #007BFF)
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(true);
        panel.setBackground(new Color(240, 248, 255)); // Light blue background (#F0F8FF)
        
        // Input field with Segoe UI font and modern styling
        messageInputField = new JTextField();
        messageInputField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageInputField.setBackground(Color.WHITE);
        messageInputField.setForeground(TEXT_COLOR); // Dark text
        messageInputField.setCaretColor(new Color(0, 123, 255)); // Blue caret (#007BFF)
        messageInputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 123, 255), 2), // Blue border (#007BFF)
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        messageInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        // Send button with blue theme (#007BFF) and rounded corners
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendButton.setBackground(new Color(0, 123, 255)); // Blue button (#007BFF)
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setContentAreaFilled(true);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(80, 38));
        sendButton.addActionListener(evt -> sendMessage());
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(new Color(0, 102, 204)); // Darker blue on hover
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(new Color(0, 123, 255)); // Back to #007BFF
            }
        });
        
        panel.add(messageInputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Send message and get chatbot response
     */
    private void sendMessage() {
        String userInput = messageInputField.getText().trim();
        
        if (userInput.isEmpty()) {
            return;
        }
        
        // Display user message
        appendUserMessage(userInput);
        messageInputField.setText("");
        messageInputField.requestFocus();
        
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
                    appendBotMessage(response);
                    
                    if (onSendMessage != null) {
                        onSendMessage.run();
                    }
                } catch (Exception e) {
                    appendBotMessage("❌ Error: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Append user message to chat history (right-aligned green bubble)
     */
    public void appendUserMessage(String message) {
        appendMessage("You", message, true);
    }
    
    /**
     * Append bot message to chat history (left-aligned white bubble)
     */
    public void appendBotMessage(String message) {
        appendMessage("Bot", message, false);
    }
    
    /**
     * Append message with modern chat bubble styling using HTML/CSS
     * User messages: right-aligned green bubble (#DCF8C6)
     * Bot messages: left-aligned white bubble (#FFFFFF)
     */
    private void appendMessage(String sender, String message, boolean isUser) {
        try {
            // HTML/CSS styling for chat bubbles
            String bubbleColor = isUser ? "#DCF8C6" : "#FFFFFF"; // Green for user, white for bot
            String alignment = isUser ? "right" : "left";
            String borderRadius = isUser ? "15px 15px 0px 15px" : "15px 15px 15px 0px"; // Rounded corners
            
            // Escape HTML special characters in message
            String escapedMessage = message
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("\n", "<br>");
            
            // Create styled HTML bubble
            String htmlBubble = String.format(
                "<div style='text-align:%s; margin-bottom:6px;'>" +
                "<div style='display:inline-block; background-color:%s; padding:8px 12px; border-radius:%s; " +
                "max-width:75%%; word-wrap:break-word; box-shadow:0 1px 2px rgba(0,0,0,0.1);'>" +
                "<b style='color:#007BFF;'>%s</b><br>" +
                "<span style='color:#333333;'>%s</span>" +
                "</div></div>",
                alignment, bubbleColor, borderRadius, sender, escapedMessage
            );
            
            // Get current HTML content and insert new bubble
            String currentHTML = chatHistoryPane.getText();
            
            // Insert before closing body tag
            String updatedHTML = currentHTML.replace("</body>", htmlBubble + "</body>");
            chatHistoryPane.setText(updatedHTML);
            
            // Auto-scroll to bottom with smooth scrolling
            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });
            
        } catch (Exception e) {
            System.err.println("Error appending message: " + e.getMessage());
        }
    }
    
    /**
     * Set callback for send message event
     */
    public void setOnSendMessageListener(Runnable callback) {
        this.onSendMessage = callback;
    }
    
    /**
     * Clear chat history
     */
    public void clearChat() {
        chatHistoryPane.setText("");
        chatBot.clearConversation();
    }
    
    /**
     * Enable/disable input
     */
    public void setInputEnabled(boolean enabled) {
        messageInputField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }
    
    /**
     * Focus on input field
     */
    public void focusInput() {
        messageInputField.requestFocus();
    }
    
    /**
     * Modern scrollbar UI with blue theme
     */
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private static final int THUMB_SIZE = 8;
        private static final Color THUMB_COLOR = new Color(0, 123, 255); // Blue (#007BFF)
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !c.isEnabled()) {
                return;
            }
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(THUMB_COLOR);
            g2d.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, THUMB_SIZE, thumbBounds.height, 
                             THUMB_SIZE, THUMB_SIZE);
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // Leave track transparent
        }
    }
}
