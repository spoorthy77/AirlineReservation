package com.mycompany.airlinereservation;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {

    // The panel that holds dynamic content (detail panels, forms, etc.)
    private JPanel dynamicContentPanel; 

    public HomePage() {
        setTitle("Airline Reservation - Home");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Background panel with custom painting ---
        JPanel bgPanel = new JPanel() {
            // NOTE: Ensure flight2.jpg is located at the specified path or change the path.
            // Using the path provided in the prompt's source code.
            ImageIcon bg = new ImageIcon("src/main/java/com/mycompany/flight2.jpg");
            Image img = (bg.getImage() == null) ? null : bg.getImage().getScaledInstance(1000, 600, Image.SCALE_SMOOTH);

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) {
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(30, 144, 255)); // Dodger Blue fallback
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        bgPanel.setLayout(new BorderLayout());

        // --- Welcome Label (Centered on the background) ---
        JPanel welcomeWrapper = new JPanel(new GridBagLayout());
        welcomeWrapper.setOpaque(false); 
        
        JLabel welcomeLabel = new JLabel("AIR INDIA WELCOMES YOU", SwingConstants.CENTER);
        ThemeManager.applyProfessionalLabelTheme(welcomeLabel, "title");
        // Override font size for welcome message
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36)); 
        
        welcomeWrapper.add(welcomeLabel);
        
        // --- Dynamic Content Panel (Initially Transparent/Empty) ---
        dynamicContentPanel = new JPanel();
        dynamicContentPanel.setOpaque(false); 
        dynamicContentPanel.setLayout(new BorderLayout()); 
        
        // Use JLayeredPane to stack the welcome message (bottom) and the dynamic content (top)
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane)); 
        
        layeredPane.add(dynamicContentPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(welcomeWrapper, JLayeredPane.DEFAULT_LAYER);
        
        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                welcomeWrapper.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                dynamicContentPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
            }
        });

        bgPanel.add(layeredPane, BorderLayout.CENTER);

        // --- Menu Bar ---
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // --- Top Right Buttons (Show and Logout) ---
        JButton showBtn = new JButton("Show");
        JButton logoutBtn = new JButton("Logout");
        
        logoutBtn.addActionListener(e -> {
            dispose();
            try {
                // Assuming 'Login' class exists in the same package
                Class<?> loginClass = Class.forName("com.mycompany.airlinereservation.Login");
                java.lang.reflect.Method mainMethod = loginClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) new String[]{});
            } catch (Exception ex) {
                System.err.println("Could not launch Login screen: " + ex.getMessage());
            }
        });

        JPanel topEastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topEastPanel.setOpaque(false);
        topEastPanel.add(showBtn); 
        
        bgPanel.add(topEastPanel, BorderLayout.NORTH); 

        add(bgPanel);
        setVisible(true);
    }

    // --- Menu Bar Helper Method (Updated for dual flight actions and BookFlight) ---
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.LIGHT_GRAY); 

        // --- Details Menu ---
        JMenu detailsMenu = new JMenu("Details");
        detailsMenu.setForeground(Color.BLACK); 
        detailsMenu.setFont(new Font("Arial", Font.BOLD, 14));

        // 🌟 ADMIN ACTION: Launches the form to ADD flights (FlightInfo.java)
        JMenuItem addFlightItem = new JMenuItem("Add Flight"); 
        
        // 🌟 USER ACTION: Launches the table VIEW (ViewFlights.java)
        JMenuItem viewFlightsItem = new JMenuItem("View Flights"); 
        
        JMenuItem addCustomerDetailsItem = new JMenuItem("Add Customer Details");
        JMenuItem bookFlightItem = new JMenuItem("Book Flight"); // Action listener updated below
        JMenuItem journeyDetailsItem = new JMenuItem("Journey Details");
        JMenuItem cancelTicketItem = new JMenuItem("Cancel Ticket");

        // Add all menu items to the Details menu
        detailsMenu.add(addFlightItem); // Admin action
        detailsMenu.add(viewFlightsItem); // User view action
        detailsMenu.addSeparator(); // Separator for visual clarity
        detailsMenu.add(addCustomerDetailsItem);
        detailsMenu.add(bookFlightItem);
        detailsMenu.add(journeyDetailsItem);
        detailsMenu.add(cancelTicketItem);

        // --- Tickets Menu ---
        JMenu ticketsMenu = new JMenu("Tickets");
        ticketsMenu.setForeground(Color.BLACK);
        ticketsMenu.setFont(new Font("Arial", Font.BOLD, 14));
        
        JMenuItem viewTicketsItem = new JMenuItem("View Tickets"); 
        ticketsMenu.add(viewTicketsItem);

        menuBar.add(detailsMenu);
        menuBar.add(ticketsMenu);

        // --- Action Listeners for Menu Items ---
        
        // ACTION 1: ADD Flight (Admin only)
        addFlightItem.addActionListener(e -> {
            clearContentPanel(); 
            new FlightInfo(); // Launches the FlightInfo form
        });
        
        // ACTION 2: VIEW Flights (User/Customer view)
        viewFlightsItem.addActionListener(e -> {
            clearContentPanel(); 
            new ViewFlightDetails(); // Corrected to launch the intended ViewFlights table
        });
        
        // EXISTING ACTION: Launches the AddCustomer form
        addCustomerDetailsItem.addActionListener(e -> {
            clearContentPanel(); 
            new AddCustomer(); 
        });
        
        // 🌟 UPDATED ACTION: Launches the BookFlight form
        bookFlightItem.addActionListener(e -> {
            clearContentPanel();
            new BookFlight(); // Launches the new booking form
        });
        
        journeyDetailsItem.addActionListener(e -> showDetailPanel("Journey Details", "Information about a specific journey: Flight Info, Customer Info, Seat Number."));
        cancelTicketItem.addActionListener(e -> showDetailPanel("Cancel Ticket", "Process to cancel ticket: Enter Ticket ID, Cancel Booking, Refund Status."));
        
        viewTicketsItem.addActionListener(e -> clearContentPanel());

        return menuBar;
    }

    // Method to display a specific detail panel (for placeholders)
    private void showDetailPanel(String title, String content) {
        dynamicContentPanel.removeAll(); 

        JPanel detailContent = new JPanel(new BorderLayout());
        detailContent.setBackground(new Color(0, 0, 0, 150)); 
        detailContent.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), 
            title,
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 18),
            Color.WHITE 
        ));

        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setOpaque(false); 
        ThemeManager.applyDarkTextAreaTheme(textArea);
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        detailContent.add(new JScrollPane(textArea), BorderLayout.CENTER); 

        JPanel paddedPanel = new JPanel(new GridBagLayout());
        paddedPanel.setOpaque(false);
        paddedPanel.add(detailContent);

        dynamicContentPanel.add(paddedPanel, BorderLayout.CENTER); 
        dynamicContentPanel.revalidate();
        dynamicContentPanel.repaint();
    }
    
    // Method to clear the content panel
    private void clearContentPanel() {
        dynamicContentPanel.removeAll();
        dynamicContentPanel.revalidate();
        dynamicContentPanel.repaint();
    }

    public static void main(String[] args) {
        // Apply built-in dark theme using UIManager defaults (no external dependency required)
        try {
            // Set system L&F first for consistent base
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Common dark colors
        Color darkBackground = ThemeManager.DARK_BG;
        Color panelBackground = ThemeManager.DARKER_BG;

        UIManager.put("Panel.background", panelBackground);
        UIManager.put("OptionPane.background", ThemeManager.LIGHT_BG);
        UIManager.put("Panel.foreground", ThemeManager.TEXT_BRIGHT);
    UIManager.put("Button.background", ThemeManager.ACCENT_BUTTON);
    // Force dark text by default on buttons (light surfaces). Specific dark buttons
    // are styled explicitly via ThemeManager.applyDarkButtonTheme where needed.
    UIManager.put("Button.foreground", ThemeManager.PURE_BLACK);
        UIManager.put("MenuBar.background", panelBackground);
        UIManager.put("MenuBar.foreground", ThemeManager.TEXT_BRIGHT);
        UIManager.put("Menu.background", panelBackground);
        UIManager.put("Menu.foreground", ThemeManager.TEXT_BRIGHT);
        UIManager.put("MenuItem.background", panelBackground);
        UIManager.put("MenuItem.foreground", ThemeManager.TEXT_BRIGHT);
        UIManager.put("Table.background", ThemeManager.DARK_BG);
        UIManager.put("Table.foreground", ThemeManager.PURE_WHITE);
        UIManager.put("Table.selectionBackground", ThemeManager.BUTTON_HOVER);
        UIManager.put("Table.selectionForeground", ThemeManager.PURE_WHITE);
        UIManager.put("TableHeader.background", ThemeManager.DARKER_BG);
        UIManager.put("TableHeader.foreground", ThemeManager.PURE_WHITE);
        UIManager.put("TextField.background", ThemeManager.PURE_WHITE);
        UIManager.put("TextField.foreground", ThemeManager.PURE_BLACK);
        UIManager.put("PasswordField.background", ThemeManager.PURE_WHITE);
        UIManager.put("PasswordField.foreground", ThemeManager.PURE_BLACK);
        UIManager.put("TextArea.background", ThemeManager.DARK_BG);
        UIManager.put("TextArea.foreground", ThemeManager.TEXT_BRIGHT);
        UIManager.put("TextPane.background", ThemeManager.DARK_BG);
        UIManager.put("TextPane.foreground", ThemeManager.TEXT_BRIGHT);
        UIManager.put("EditorPane.background", ThemeManager.DARK_BG);
        UIManager.put("EditorPane.foreground", ThemeManager.TEXT_BRIGHT);
        UIManager.put("ScrollPane.background", darkBackground);
    // Default labels should be readable on dark panels; a global enforcer flips to dark on light surfaces.
    UIManager.put("Label.foreground", ThemeManager.PURE_WHITE);
        UIManager.put("ToggleButton.background", ThemeManager.DARKER_BG);
        UIManager.put("ToolTip.background", ThemeManager.DARKER_BG);
        UIManager.put("ToolTip.foreground", ThemeManager.TEXT_BRIGHT);
        UIManager.put("TitledBorder.titleColor", ThemeManager.TEXT_BRIGHT);
    UIManager.put("OptionPane.messageForeground", ThemeManager.PURE_BLACK);
    // Force dark text on light combo/list by default; dark variants are rendered explicitly.
    UIManager.put("ComboBox.foreground", ThemeManager.PURE_BLACK);
    UIManager.put("List.foreground", ThemeManager.PURE_BLACK);

        // Install a global enforcer that forces dark text on any light-background components
        // created after startup (dialogs, dynamically created forms, etc.).
        ThemeManager.installGlobalHighContrastEnforcer();

        // Seed initial data (admin account) before showing GUI
        DataSeeder.seedAdmin();

        // Show a simple custom launcher dialog (Admin or Customer). Using a custom JDialog
        // so the dark theme colors render correctly for buttons and labels.
        SwingUtilities.invokeLater(() -> {
            final int[] result = { -1 }; // 0=Admin,1=Customer,-1=cancel

            JDialog dlg = new JDialog((Frame) null, "Airline Reservation — Launcher", true);
            dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dlg.setSize(420, 160);
            dlg.setResizable(false);
            dlg.setLocationRelativeTo(null);

            JPanel content = new JPanel(new BorderLayout(10, 10));
            content.setBackground(ThemeManager.LIGHT_BG);
            content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            // Use an opaque label with explicit background/foreground to avoid LAF inconsistencies
            JLabel msg = new JLabel("Welcome — choose your role: (Admins manage flights; Customers book flights)");
            ThemeManager.applyLightLabelTheme(msg);
            msg.setFont(new Font("Dialog", Font.BOLD, 14));
            msg.setOpaque(true);
            msg.setBackground(ThemeManager.LIGHT_BG);
            msg.setHorizontalAlignment(SwingConstants.CENTER);
            content.add(msg, BorderLayout.CENTER);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 6));
            ThemeManager.applyLightPanelTheme(btns);

            JButton adminBtn = new JButton("Admin");
            JButton customerBtn = new JButton("Customer");

            // Make buttons opaque and explicitly set background/foreground so text is always visible
            ThemeManager.applyLightButtonTheme(adminBtn);
            ThemeManager.applyLightButtonTheme(customerBtn);

            java.awt.event.ActionListener selectionListener = event -> {
                Object source = event.getSource();
                if (source == adminBtn) {
                    result[0] = 0;
                } else if (source == customerBtn) {
                    result[0] = 1;
                }
                dlg.dispose();
            };

            adminBtn.addActionListener(selectionListener);
            customerBtn.addActionListener(selectionListener);

            btns.add(adminBtn);
            btns.add(customerBtn);

            content.add(btns, BorderLayout.SOUTH);

            dlg.setContentPane(content);
            dlg.setVisible(true);

            if (result[0] == 0) {
                new Login("Admin");
            } else if (result[0] == 1) {
                new Login("Customer");
            } else {
                System.exit(0);
            }
        });
    }
}