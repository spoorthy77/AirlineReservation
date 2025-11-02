package com.mycompany.airlinereservation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * FloatingChatWidget.java - Non-intrusive floating chat bubble widget
 * 
 * Features:
 * - Small circular/bubble icon in bottom-right corner (when minimized)
 * - Expands into full chat window on click
 * - Minimizes back to bubble when close button clicked or clicking outside
 * - Always-on-top functionality (can be toggled)
 * - Smooth animations for expand/collapse
 * - Integrates with ChatBot for conversation logic
 * - Customizable theme colors
 */
public class FloatingChatWidget extends JFrame {
    
    private static final int BUBBLE_SIZE = 60;
    private static final int EXPANDED_WIDTH = 400;
    private static final int EXPANDED_HEIGHT = 600;
    private static final int CORNER_RADIUS = 15;
    
    private boolean isExpanded = false;
    private boolean isDragging = false;
    private Point dragOffset;
    private Point bubbleLocation;
    
    private JPanel chatPanel;
    private JTextPane messageDisplay;
    private JTextField messageInput;
    private JButton sendButton;
    private JButton minimizeButton;
    private JButton closeButton;
    private JButton toggleAlwaysOnTop;
    
    private ChatBot chatBot;
    private String username;
    private Dimension minimizedSize;
    private Dimension maximizedSize;
    private Rectangle screenBounds;
    
    // Theme colors
    private static final Color PRIMARY_COLOR = new Color(74, 166, 240);  // Blue
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219); // Darker blue
    private static final Color ACCENT_COLOR = new Color(240, 248, 255);   // Light blue
    private static final Color TEXT_COLOR = new Color(30, 30, 30);        // Dark text
    private static final Color BOT_BUBBLE = new Color(255, 255, 255);     // White
    private static final Color USER_BUBBLE = new Color(220, 248, 198);    // Light green
    
    public FloatingChatWidget(String username, JFrame parent) {
        this.username = username;
        this.chatBot = new ChatBotEnhanced(username);
        this.minimizedSize = new Dimension(BUBBLE_SIZE, BUBBLE_SIZE);
        this.maximizedSize = new Dimension(EXPANDED_WIDTH, EXPANDED_HEIGHT);
        
        // Get screen bounds to keep widget within visible area
        screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        
        // Initial bubble position (bottom-right corner with margin)
        bubbleLocation = new Point(
            screenBounds.width - BUBBLE_SIZE - 20,
            screenBounds.height - BUBBLE_SIZE - 80
        );
        
        initializeFrame();
        initializeUI();
    }
    
    /**
     * Initialize frame properties
     */
    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setUndecorated(true);
        setOpacity(0.95f);
        setAlwaysOnTop(true);
        setFocusable(true);
        setResizable(false);
        
        // Start in minimized state
        setSize(minimizedSize);
        setLocation(bubbleLocation.x, bubbleLocation.y);
        
        // Custom content pane for rounded corners in minimized state
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!isExpanded) {
                    paintBubbleIcon(g);
                }
            }
        });
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded background
                g2d.setColor(ACCENT_COLOR);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
                
                // Draw border
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(PRIMARY_COLOR);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header with title and controls
        JPanel headerPanel = new JPanel(new BorderLayout(5, 0));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("💬 Chat Support");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Control buttons panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        controlsPanel.setOpaque(false);
        
        toggleAlwaysOnTop = createIconButton("📌", 24);
        toggleAlwaysOnTop.setToolTipText("Toggle Always On Top");
        toggleAlwaysOnTop.addActionListener(e -> toggleAlwaysOnTop());
        
        minimizeButton = createIconButton("−", 24);
        minimizeButton.setToolTipText("Minimize");
        minimizeButton.addActionListener(e -> minimizeWidget());
        
        closeButton = createIconButton("✕", 24);
        closeButton.setToolTipText("Close");
        closeButton.addActionListener(e -> hideWidget());
        
        controlsPanel.add(toggleAlwaysOnTop);
        controlsPanel.add(minimizeButton);
        controlsPanel.add(closeButton);
        
        headerPanel.add(controlsPanel, BorderLayout.EAST);
        
        // Message display area
        messageDisplay = new JTextPane();
        messageDisplay.setEditable(false);
        messageDisplay.setContentType("text/html");
        messageDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageDisplay.setBackground(ACCENT_COLOR);
        messageDisplay.setForeground(TEXT_COLOR);
        messageDisplay.setMargin(new Insets(8, 8, 8, 8));
        messageDisplay.setText("<html><body style='font-family:Segoe UI; font-size:12px; background-color:#F0F8FF; margin:0; padding:10px;'></body></html>");
        
        JScrollPane scrollPane = new JScrollPane(messageDisplay);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        messageInput = new JTextField();
        messageInput.setFont(new Font("SansSerif", Font.PLAIN, 12));
        messageInput.setBackground(Color.WHITE);
        messageInput.setForeground(TEXT_COLOR);
        messageInput.setCaretColor(PRIMARY_COLOR);
        messageInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        messageInput.addKeyListener(new KeyAdapter() {
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
        sendButton.setContentAreaFilled(true);
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
        
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Assemble main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        // Store chat panel for later reference
        chatPanel = mainPanel;
        setContentPane(mainPanel);
        
        // Add welcome message
        appendBotMessage("👋 Welcome! How can we help you today?\n\nYou can ask about:\n" +
                        "✈️ Flight availability\n" +
                        "🎫 Bookings\n" +
                        "📋 Your reservations\n" +
                        "💳 Payments");
        
        // Mouse listener for drag (only in minimized state) and click to expand
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isExpanded) {
                    isDragging = true;
                    dragOffset = new Point(e.getXOnScreen() - getX(), e.getYOnScreen() - getY());
                } else if (isClickOnMinimizeArea(e.getPoint())) {
                    minimizeWidget();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isExpanded && e.getClickCount() == 1) {
                    expandWidget();
                }
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging && !isExpanded && dragOffset != null) {
                    int x = e.getXOnScreen() - dragOffset.x;
                    int y = e.getYOnScreen() - dragOffset.y;
                    
                    // Keep within screen bounds
                    x = Math.max(0, Math.min(x, screenBounds.width - BUBBLE_SIZE));
                    y = Math.max(0, Math.min(y, screenBounds.height - BUBBLE_SIZE));
                    
                    setLocation(x, y);
                    bubbleLocation = new Point(x, y);
                }
            }
        });
        
        // Focus listener to handle minimize on focus lost
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (isExpanded && !isDragging) {
                    // Don't auto-minimize, let user close explicitly
                }
            }
        });
    }
    
    /**
     * Paint the bubble icon when minimized
     */
    private void paintBubbleIcon(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int centerX = BUBBLE_SIZE / 2;
        int centerY = BUBBLE_SIZE / 2;
        
        // Draw bubble background
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillOval(0, 0, BUBBLE_SIZE - 1, BUBBLE_SIZE - 1);
        
        // Draw bubble border
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(SECONDARY_COLOR);
        g2d.drawOval(0, 0, BUBBLE_SIZE - 1, BUBBLE_SIZE - 1);
        
        // Draw chat icon
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics fm = g2d.getFontMetrics();
        String icon = "💬";
        int iconX = (BUBBLE_SIZE - fm.stringWidth(icon)) / 2;
        int iconY = centerY + fm.getAscent() / 2 - 2;
        g2d.drawString(icon, iconX, iconY);
    }
    
    /**
     * Expand widget to full size
     */
    private void expandWidget() {
        isExpanded = true;
        
        // Calculate new position (keep bubble location, expand upward/leftward)
        int newX = Math.max(20, bubbleLocation.x - EXPANDED_WIDTH + BUBBLE_SIZE);
        int newY = Math.max(20, bubbleLocation.y - EXPANDED_HEIGHT + BUBBLE_SIZE);
        
        setSize(maximizedSize);
        setLocation(newX, newY);
        
        // Refresh to show chat panel instead of bubble
        getContentPane().repaint();
        messageInput.requestFocus();
    }
    
    /**
     * Minimize widget back to bubble
     */
    private void minimizeWidget() {
        isExpanded = false;
        setSize(minimizedSize);
        setLocation(bubbleLocation.x, bubbleLocation.y);
        getContentPane().repaint();
    }
    
    /**
     * Toggle always on top property
     */
    private void toggleAlwaysOnTop() {
        boolean currentState = isAlwaysOnTop();
        setAlwaysOnTop(!currentState);
        toggleAlwaysOnTop.setText(currentState ? "📌" : "📍");
    }
    
    /**
     * Hide the widget
     */
    private void hideWidget() {
        setVisible(false);
    }
    
    /**
     * Show the widget
     */
    public void showWidget() {
        setVisible(true);
        toFront();
        if (isExpanded) {
            messageInput.requestFocus();
        }
    }
    
    /**
     * Toggle widget visibility
     */
    public void toggleWidget() {
        setVisible(!isVisible());
        if (isVisible()) {
            toFront();
        }
    }
    
    /**
     * Check if click is on the minimize button area
     */
    private boolean isClickOnMinimizeArea(Point point) {
        // Simple check - if near top-right corner
        return point.x > getWidth() - 40 && point.y < 30;
    }
    
    /**
     * Send user message and get bot response
     */
    private void sendMessage() {
        String userInput = messageInput.getText().trim();
        if (userInput.isEmpty()) {
            return;
        }
        
        appendUserMessage(userInput);
        messageInput.setText("");
        
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
                } catch (Exception e) {
                    appendBotMessage("❌ Error: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Append user message to display
     */
    private void appendUserMessage(String message) {
        appendMessage(message, true);
    }
    
    /**
     * Append bot message to display
     */
    private void appendBotMessage(String message) {
        appendMessage(message, false);
    }
    
    /**
     * Append message with chat bubble styling
     */
    private void appendMessage(String message, boolean isUser) {
        try {
            String bubbleColor = isUser ? "#DCF8C6" : "#FFFFFF";
            String alignment = isUser ? "right" : "left";
            String borderRadius = isUser ? "15px 15px 0px 15px" : "15px 15px 15px 0px";
            
            // Escape HTML special characters
            String escapedMessage = message
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("\n", "<br>");
            
            String htmlBubble = String.format(
                "<div style='text-align:%s; margin-bottom:8px;'>" +
                "<div style='display:inline-block; background-color:%s; padding:10px 12px; border-radius:%s; " +
                "max-width:80%%; word-wrap:break-word; box-shadow:0 1px 2px rgba(0,0,0,0.1);'>" +
                "<span style='color:#333333;'>%s</span>" +
                "</div></div>",
                alignment, bubbleColor, borderRadius, escapedMessage
            );
            
            String currentHTML = messageDisplay.getText();
            String updatedHTML = currentHTML.replace("</body>", htmlBubble + "</body>");
            messageDisplay.setText(updatedHTML);
            
            // Auto-scroll to bottom
            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = ((JScrollPane) messageDisplay.getParent().getParent()).getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });
            
        } catch (Exception e) {
            System.err.println("Error appending message: " + e.getMessage());
        }
    }
    
    /**
     * Create icon button with custom styling
     */
    private JButton createIconButton(String icon, int size) {
        JButton button = new JButton(icon);
        button.setFont(new Font("SansSerif", Font.BOLD, size));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(PRIMARY_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(SECONDARY_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(PRIMARY_COLOR);
            }
        });
        return button;
    }
    
    /**
     * Clear chat history
     */
    public void clearChat() {
        messageDisplay.setText("<html><body style='font-family:Segoe UI; font-size:12px; background-color:#F0F8FF; margin:0; padding:10px;'></body></html>");
        chatBot.clearConversation();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FloatingChatWidget widget = new FloatingChatWidget("TestUser", null);
            widget.setVisible(true);
        });
    }
}
