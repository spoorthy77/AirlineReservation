package com.mycompany.airlinereservation;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserDashboard extends JFrame {

    // Use a background image located in project (flight1.jpg or flight2.jpg)
    private static final String BG_PATH = "src/main/java/com/mycompany/flight1.jpg";
    private final ChatBotIcon chatBotIcon;
    
    // ✅ Store the authenticated username for session-based operations
    private String authenticatedUsername;

    public UserDashboard(String username) {
        // ✅ ENHANCEMENT: Validate that the username matches the session
        String sessionUsername = SessionManager.getInstance().getCurrentUser();
        
        if (sessionUsername == null || !sessionUsername.equals(username)) {
            System.err.println("❌ SECURITY WARNING: UserDashboard username mismatch!");
            System.err.println("   Expected: " + sessionUsername + ", Got: " + username);
            ThemeManager.showError(null, "❌ Security Error: Username mismatch. Please log in again.");
            System.exit(1);
        }
        
        this.authenticatedUsername = username;
        
        setTitle("User Dashboard - Welcome " + username);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background panel with gradient overlay and background image
        JPanel background = new JPanel() {
            private Image bg = new ImageIcon(BG_PATH).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (bg != null) {
                    // Scale image to fill the panel
                    g2d.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
                
                // Apply blue-white gradient overlay for enhanced contrast
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185, 100),  // Modern blue with transparency
                    0, getHeight(), new Color(52, 73, 94, 120)  // Darker blue with more transparency
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setLayout(new BorderLayout());

        // Title label at top center
        JLabel titleLabel = new JLabel("AIRLINE RESERVATION DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(ThemeManager.PURE_WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        // Add shadow effect using a slight offset label in dark color
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow text slightly offset
                g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString("AIRLINE RESERVATION DASHBOARD", 35, 37);
                
                // Draw white text on top
                g2d.setColor(ThemeManager.PURE_WHITE);
                g2d.drawString("AIRLINE RESERVATION DASHBOARD", 34, 36);
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        titlePanel.setPreferredSize(new Dimension(900, 50));

        // Header with welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomeLabel.setForeground(ThemeManager.PURE_WHITE);
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        header.add(welcomeLabel);

        // Center buttons stacked vertically with spacing
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        // Helper to create styled button panels
        center.add(createButtonPanel("View Available Flights", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                new ViewFlightDetails();
            }
        }));
        center.add(Box.createVerticalStrut(18));
        center.add(createButtonPanel("View Bookings", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                new ViewBookings();
            }
        }));
        center.add(Box.createVerticalStrut(18));
        center.add(createButtonPanel("Book a Flight", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                new BookFlight();
            }
        }));
        center.add(Box.createVerticalStrut(18));
        center.add(createButtonPanel("Cancel Ticket", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                new Cancel();
            }
        }));
        center.add(Box.createVerticalStrut(18));
        center.add(createButtonPanel("Logout", new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // ✅ ENHANCEMENT: Clear the session when logging out
                SessionManager.getInstance().clearSession();
                dispose();
                SwingUtilities.invokeLater(Login::new);
            }
        }));

        // Add some padding around center
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(10, 60, 40, 60));
        centerWrapper.add(center);

    // Combine title and welcome header (left)
    JPanel topLeft = new JPanel();
    topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.Y_AXIS));
    topLeft.setOpaque(false);
    topLeft.add(titlePanel);
    topLeft.add(header);

    JPanel topBar = new JPanel(new BorderLayout());
    topBar.setOpaque(false);
    topBar.add(topLeft, BorderLayout.CENTER);

    background.add(topBar, BorderLayout.NORTH);
        background.add(centerWrapper, BorderLayout.CENTER);

        setContentPane(background);
        
        // Initialize floating chatbot icon
        chatBotIcon = new ChatBotIcon(username);
        
        // Add chatbot icon to a fixed position panel (bottom-right)
        JPanel iconPanel = new JPanel(null);  // Null layout for absolute positioning
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(70, 70));
        chatBotIcon.setBounds(0, 0, 60, 60);
        iconPanel.add(chatBotIcon);
        
        // Create a panel to hold the main content and chatbot icon
        JPanel contentWithIcon = new JPanel(new BorderLayout());
        contentWithIcon.setOpaque(false);
        contentWithIcon.add(background, BorderLayout.CENTER);
        
        // Add icon panel to bottom-right
        JPanel bottomRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomRightPanel.setOpaque(false);
        bottomRightPanel.add(chatBotIcon);
        
        // Overlay panel that contains both content and icon
        JLayeredPane layered = new JLayeredPane();
        layered.setLayout(new BorderLayout());
        layered.add(background, BorderLayout.CENTER, 0);
        layered.add(bottomRightPanel, BorderLayout.SOUTH, 1);
        
        setContentPane(layered);
        
        setVisible(true);
    }
    
    private JPanel createButtonPanel(String text, java.awt.event.ActionListener action) {
        RoundedButton btn = new RoundedButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(420, 60));
        btn.setMaximumSize(new Dimension(Short.MAX_VALUE, 60));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        
        // Add hover effect listener
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setHovered(true);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setHovered(false);
                btn.repaint();
            }
        });
        
        btn.addActionListener(action);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        panel.add(btn);
        return panel;
    }

    /**
     * Custom rounded button with modern styling and hover effects
     */
    private static class RoundedButton extends JButton {
        private boolean hovered = false;
        private static final int RADIUS = 12;

        public RoundedButton(String text) {
            super(text);
            setUI(new BasicButtonUI());
        }

        public void setHovered(boolean hovered) {
            this.hovered = hovered;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Draw rounded background
            Color bgColor = hovered ? 
                new Color(52, 152, 219) :  // Darker blue on hover
                new Color(74, 166, 240);    // Modern blue default
            
            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, 0, width, height, RADIUS, RADIUS);

            // Draw border
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.drawRoundRect(0, 0, width - 1, height - 1, RADIUS, RADIUS);

            // Draw text with shadow effect for better readability
            if (getText() != null && !getText().isEmpty()) {
                FontMetrics fm = g2d.getFontMetrics(getFont());
                int textX = (width - fm.stringWidth(getText())) / 2;
                int textY = ((height - fm.getHeight()) / 2) + fm.getAscent();

                // Shadow text (dark, slightly offset)
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setFont(getFont());
                g2d.drawString(getText(), textX + 1, textY + 1);

                // Main text (white, bold)
                g2d.setColor(ThemeManager.PURE_WHITE);
                g2d.drawString(getText(), textX, textY);
            }
        }

        @Override
        public void paintBorder(Graphics g) {
            // Don't paint default border
        }
    }
}
