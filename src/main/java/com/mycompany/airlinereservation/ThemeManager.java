package com.mycompany.airlinereservation;

import java.awt.*;

/**
 * Centralized theme/color management for consistent UI across the application
 * Enhanced contrast: darker backgrounds and lighter text for better readability
 */
public class ThemeManager {
    private static volatile boolean CONTRAST_ENFORCER_INSTALLED = false;

    // --- BACKGROUND COLORS (Purple-Blue Gradient Theme) ---
    public static final Color DARK_BG = new Color(45, 45, 75);          // Deep purple-blue background
    public static final Color DARKER_BG = new Color(35, 35, 65);        // Darker shade for panels
    public static final Color INPUT_BG = new Color(55, 55, 85);         // Lighter shade for input areas
    public static final Color PANEL_BG = new Color(50, 50, 80);         // Main panel color
    public static final Color DIALOG_BG = new Color(60, 60, 90);        // Dialog background
    public static final Color DIALOG_HEADER = new Color(70, 70, 100);   // Dialog header

    // --- LIGHT SURFACES FOR HIGH-CONTRAST STATES ---
    public static final Color LIGHT_BG = new Color(245, 247, 252);      // Soft light background for dialogs
    public static final Color LIGHT_PANEL_BG = new Color(235, 238, 245); // Light panel background
    public static final Color LIGHT_BUTTON_BG = new Color(226, 232, 245); // Light button fill
    public static final Color LIGHT_BUTTON_BORDER = new Color(168, 182, 210); // Light button border
    public static final Color LIGHT_BORDER = new Color(176, 188, 212);   // Border for light components

    // --- TEXT COLORS (Dark Theme) ---
    public static final Color TEXT_PRIMARY = new Color(220, 220, 240);  // Light gray-white for primary text
    public static final Color TEXT_BRIGHT = new Color(240, 245, 250);   // Off-white for headings
    public static final Color TEXT_SECONDARY = new Color(200, 210, 225); // Light blue-gray for secondary
    public static final Color TEXT_DARK = new Color(180, 190, 210);     // Muted gray-blue for hints

    // --- TEXT COLORS (Light Surfaces) ---
    public static final Color TEXT_ON_LIGHT_PRIMARY = new Color(0, 0, 0);       // Pure black for maximum contrast
    public static final Color TEXT_ON_LIGHT_SECONDARY = new Color(51, 51, 51);  // Dark gray fallback for secondary text

    // --- BASE COLORS ---
    public static final Color PURE_WHITE = new Color(255, 255, 255);
    public static final Color PURE_BLACK = new Color(0, 0, 0);
    public static final Color VERY_DARK_GRAY = new Color(51, 51, 51);

    // --- ACCENT COLORS (Dark Theme) ---
    public static final Color ACCENT_BLUE = new Color(100, 160, 210);   // Muted blue for highlights
    public static final Color ACCENT_BORDER = new Color(120, 140, 180); // Subtle blue-gray for borders
    public static final Color ACCENT_BUTTON = new Color(240, 240, 240);  // Light gray for fetch buttons
    public static final Color ACCENT_BUTTON_TEXT = new Color(33, 33, 33); // Very dark gray for button text

    public static final Color PRIMARY_BUTTON_BG = new Color(198, 218, 255);   // Soft blue for primary actions
    public static final Color PRIMARY_BUTTON_HOVER = new Color(180, 205, 250); // Hover state for primary actions
    public static final Color PRIMARY_BUTTON_BORDER = new Color(132, 162, 220); // Border for primary actions
    public static final Color PRIMARY_BUTTON_TEXT = new Color(0, 0, 0);        // Pure black text on primary buttons

    // --- UTILITY COLORS ---
    public static final Color ERROR_RED = new Color(220, 100, 100);     // Muted red for errors
    public static final Color SUCCESS_GREEN = new Color(120, 180, 120); // Muted green for success
    public static final Color BUTTON_HOVER = new Color(230, 230, 230);  // Slightly darker gray for hover blue for hover states


    /**
     * Apply dark theme styling to a JTextField
     */
    public static void applyDarkTextFieldTheme(javax.swing.JTextField field) {
        styleTextComponent(field, PURE_WHITE, PURE_BLACK, LIGHT_BORDER);
    }

    public static void applyDarkPasswordFieldTheme(javax.swing.JPasswordField field) {
        styleTextComponent(field, PURE_WHITE, PURE_BLACK, LIGHT_BORDER);
    }

    /**
     * Apply dark-surface theme to text inputs: dark background with pure-white text.
     * Use on forms that sit on dark panels so entered text remains readable.
     */
    public static void applyDarkSurfaceTextFieldTheme(javax.swing.JTextField field) {
        styleTextComponent(field, INPUT_BG, PURE_WHITE, ACCENT_BORDER);
    }

    public static void applyDarkSurfacePasswordFieldTheme(javax.swing.JPasswordField field) {
        styleTextComponent(field, INPUT_BG, PURE_WHITE, ACCENT_BORDER);
    }

    public static void applyHighContrastComboBoxTheme(javax.swing.JComboBox<?> combo) {
        if (combo == null) {
            return;
        }
        Runnable apply = () -> {
            combo.setBackground(PURE_WHITE);
            combo.setForeground(PURE_BLACK);
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            combo.setBorder(javax.swing.BorderFactory.createLineBorder(LIGHT_BORDER));
            combo.setOpaque(true);
            if (!(combo.getRenderer() instanceof javax.swing.DefaultListCellRenderer) || combo.getClientProperty("theme.light.renderer") == null) {
                combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
                    @Override
                    public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        setForeground(isSelected ? PURE_WHITE : PURE_BLACK);
                        setBackground(isSelected ? ACCENT_BUTTON : PURE_WHITE);
                        return c;
                    }
                });
                combo.putClientProperty("theme.light.renderer", Boolean.TRUE);
            }
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    public static void applyDarkComboBoxTheme(javax.swing.JComboBox<?> combo) {
        if (combo == null) {
            return;
        }
        Runnable apply = () -> {
            combo.setBackground(DARKER_BG);
            combo.setForeground(PURE_WHITE);
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            combo.setBorder(javax.swing.BorderFactory.createLineBorder(ACCENT_BORDER));
            combo.setOpaque(true);
            if (!(combo.getRenderer() instanceof javax.swing.DefaultListCellRenderer) || combo.getClientProperty("theme.dark.renderer") == null) {
                combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
                    @Override
                    public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        setForeground(PURE_WHITE);
                        setBackground(isSelected ? BUTTON_HOVER : DARKER_BG);
                        return c;
                    }
                });
                combo.putClientProperty("theme.dark.renderer", Boolean.TRUE);
            }
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    public static void applyLightLabelTheme(final javax.swing.JLabel label) {
        if (label == null) {
            return;
        }
        Runnable apply = () -> {
            label.setForeground(TEXT_ON_LIGHT_PRIMARY);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setOpaque(false);
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    public static void applyLightPanelTheme(final javax.swing.JPanel panel) {
        if (panel == null) {
            return;
        }
        Runnable apply = () -> {
            panel.setBackground(LIGHT_PANEL_BG);
            panel.setOpaque(true);
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    public static void applyLightButtonTheme(final javax.swing.JButton button) {
        if (button == null) {
            return;
        }
        Runnable apply = () -> {
            button.setOpaque(true);
            button.setBorderPainted(true);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 13));
            button.setBackground(LIGHT_BUTTON_BG);
            button.setForeground(PURE_BLACK); // FORCE pure black text for maximum contrast
            button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(LIGHT_BUTTON_BORDER),
                javax.swing.BorderFactory.createEmptyBorder(6, 18, 6, 18)
            ));
            
            // Add hover effect that maintains black text
            if (!Boolean.TRUE.equals(button.getClientProperty("theme.light.hover"))) {
                button.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        button.setBackground(new Color(220, 225, 235)); // Slightly darker on hover
                        button.setForeground(PURE_BLACK); // Maintain pure black text
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        button.setBackground(LIGHT_BUTTON_BG); // Return to original
                        button.setForeground(PURE_BLACK); // Maintain pure black text
                    }
                });
                button.putClientProperty("theme.light.hover", Boolean.TRUE);
            }
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    /**
     * FORCE DARK TEXT: Specifically for dialog buttons that must have dark text on light backgrounds
     * This method guarantees maximum contrast for button text visibility
     */
    public static void applyHighContrastDialogButtonTheme(final javax.swing.JButton button) {
        if (button == null) {
            return;
        }
        Runnable apply = () -> {
            button.setOpaque(true);
            button.setBorderPainted(true);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Slightly larger for dialogs
            button.setBackground(new Color(245, 245, 245)); // Very light gray background
            button.setForeground(PURE_BLACK); // FORCE pure black text
            button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(140, 140, 140), 2), // Medium gray border
                javax.swing.BorderFactory.createEmptyBorder(8, 20, 8, 20) // Slightly larger padding
            ));
            
            // Add hover effect that maintains black text
            if (!Boolean.TRUE.equals(button.getClientProperty("theme.dialog.hover"))) {
                button.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        button.setBackground(new Color(230, 230, 230)); // Darker on hover
                        button.setForeground(PURE_BLACK); // FORCE maintain black text
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        button.setBackground(new Color(245, 245, 245)); // Return to light
                        button.setForeground(PURE_BLACK); // FORCE maintain black text
                    }
                });
                button.putClientProperty("theme.dialog.hover", Boolean.TRUE);
            }
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    private static void styleTextComponent(final javax.swing.text.JTextComponent component, final Color background, final Color foreground, final Color baseBorder) {
        if (component == null) {
            return;
        }
        Runnable apply = () -> {
            component.setOpaque(true);
            component.setBackground(background);
            component.setForeground(foreground);
            component.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            component.setCaretColor(foreground);
            component.setSelectedTextColor(PURE_WHITE);
            component.setSelectionColor(ACCENT_BUTTON);
            component.setDisabledTextColor(foreground.darker());
            component.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(baseBorder, 2),  // Thicker border
                javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }

        if (component.getClientProperty("theme.focus.listener") == null) {
            component.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    component.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(ACCENT_BLUE),
                        javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)
                    ));
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    component.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(baseBorder),
                        javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)
                    ));
                }
            });
            component.putClientProperty("theme.focus.listener", Boolean.TRUE);
        }
    }

    public static void applyDarkButtonTheme(final javax.swing.JButton button, final boolean isPrimary) {
        if (button == null) {
            return;
        }
        Runnable apply = () -> {
            button.setOpaque(true);
            button.setBorderPainted(true);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 13));

            // Check if this is a fetch button by looking at the text
            boolean isFetchButton = button.getText() != null && 
                                  (button.getText().contains("Fetch") || 
                                   button.getText().startsWith("Get") || 
                                   button.getText().startsWith("Load"));

            if (isFetchButton) {
                // High contrast theme for fetch buttons
                button.setBackground(ACCENT_BUTTON); // Light gray background
                button.setForeground(ACCENT_BUTTON_TEXT); // Very dark gray text
                button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(new Color(180, 180, 180), 2), // Medium gray border
                    javax.swing.BorderFactory.createEmptyBorder(6, 18, 6, 18)
                ));
                
                // Add hover effect that maintains contrast
                if (!Boolean.TRUE.equals(button.getClientProperty("theme.fetch.hover"))) {
                    button.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                            button.setBackground(BUTTON_HOVER);
                            button.setForeground(ACCENT_BUTTON_TEXT);
                        }
                        
                        @Override
                        public void mouseExited(java.awt.event.MouseEvent evt) {
                            button.setBackground(ACCENT_BUTTON);
                            button.setForeground(ACCENT_BUTTON_TEXT);
                        }
                    });
                    button.putClientProperty("theme.fetch.hover", Boolean.TRUE);
                }
            } else {
                if (isPrimary) {
                    button.setBackground(PRIMARY_BUTTON_BG);
                    button.setForeground(PRIMARY_BUTTON_TEXT);
                    button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(PRIMARY_BUTTON_BORDER, 2),
                        javax.swing.BorderFactory.createEmptyBorder(6, 18, 6, 18)
                    ));

                    if (!Boolean.TRUE.equals(button.getClientProperty("theme.primary.hover"))) {
                        button.addMouseListener(new java.awt.event.MouseAdapter() {
                            @Override
                            public void mouseEntered(java.awt.event.MouseEvent evt) {
                                button.setBackground(PRIMARY_BUTTON_HOVER);
                                button.setForeground(PRIMARY_BUTTON_TEXT);
                            }

                            @Override
                            public void mouseExited(java.awt.event.MouseEvent evt) {
                                button.setBackground(PRIMARY_BUTTON_BG);
                                button.setForeground(PRIMARY_BUTTON_TEXT);
                            }
                        });
                        button.putClientProperty("theme.primary.hover", Boolean.TRUE);
                    }
                } else {
                    button.setBackground(DARKER_BG);
                    button.setForeground(PURE_WHITE);
                    button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(ACCENT_BORDER, 2),
                        javax.swing.BorderFactory.createEmptyBorder(6, 18, 6, 18)
                    ));
                }
            }
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    public static void applyDarkLabelTheme(final javax.swing.JLabel label) {
        if (label != null) {
            Runnable apply = () -> {
                label.setForeground(PURE_WHITE);  // Pure white for maximum contrast against dark background
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));  // Made bold for better visibility
                label.setBackground(DARK_BG);
                label.setOpaque(false);
            };
            if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                apply.run();
            } else {
                javax.swing.SwingUtilities.invokeLater(apply);
            }
        }
    }

    /**
     * FORCE CONTRAST: Apply pure white (#FFFFFF) text to all labels for maximum visibility
     * Use this for critical header text and static labels in dark dialogs
     */
    public static void applyHighContrastLabelTheme(final javax.swing.JLabel label) {
        if (label != null) {
            Runnable apply = () -> {
                label.setForeground(PURE_WHITE);  // FORCE pure white (#FFFFFF)
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));  // Larger, bold font for readability
                label.setBackground(DARK_BG);
                label.setOpaque(false);
                
                // Force text anti-aliasing for crisp rendering
                label.putClientProperty("text.antialiasing", java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            };
            if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                apply.run();
            } else {
                javax.swing.SwingUtilities.invokeLater(apply);
            }
        }
    }

    /**
     * FORCE CONTRAST: Apply pure white text to headers with larger, bold font
     */
    public static void applyHighContrastHeaderTheme(final javax.swing.JLabel header) {
        if (header != null) {
            Runnable apply = () -> {
                header.setForeground(PURE_WHITE);  // FORCE pure white (#FFFFFF)
                header.setFont(new Font("Segoe UI", Font.BOLD, 18));  // Large header font
                header.setBackground(DARK_BG);
                header.setOpaque(false);
                header.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                
                // Force text anti-aliasing for crisp rendering
                header.putClientProperty("text.antialiasing", java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            };
            if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                apply.run();
            } else {
                javax.swing.SwingUtilities.invokeLater(apply);
            }
        }
    }

    /**
     * FORCE CONTRAST: Apply pure white text to welcome messages and informational text
     */
    public static void applyHighContrastWelcomeTheme(final javax.swing.JLabel welcomeLabel) {
        if (welcomeLabel != null) {
            Runnable apply = () -> {
                welcomeLabel.setForeground(PURE_WHITE);  // FORCE pure white (#FFFFFF)
                welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));  // Readable font for welcome text
                welcomeLabel.setBackground(DARK_BG);
                welcomeLabel.setOpaque(false);
                
                // Force text anti-aliasing for crisp rendering
                welcomeLabel.putClientProperty("text.antialiasing", java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            };
            if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                apply.run();
            } else {
                javax.swing.SwingUtilities.invokeLater(apply);
            }
        }
    }

    /**
     * MASS APPLY: Force pure white text to all child labels in a container
     * Useful for applying consistent high-contrast text across entire dialog panels
     */
    public static void forceHighContrastTextInContainer(final java.awt.Container container) {
        if (container == null) {
            return;
        }
        
        Runnable apply = () -> {
            forceHighContrastTextInContainerRecursive(container);
        };
        
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }
    
    private static void forceHighContrastTextInContainerRecursive(java.awt.Container container) {
        for (java.awt.Component comp : container.getComponents()) {
            if (comp instanceof javax.swing.JLabel label) {
                // Force pure white text for ALL labels
                label.setForeground(PURE_WHITE);
                Font currentFont = label.getFont();
                if (currentFont != null) {
                    // Maintain existing font family and style, but ensure it's at least 12pt
                    int fontSize = Math.max(currentFont.getSize(), 12);
                    label.setFont(new Font(currentFont.getName(), currentFont.getStyle(), fontSize));
                } else {
                    label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                }
                label.putClientProperty("text.antialiasing", java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            
            // Recursively apply to nested containers
            if (comp instanceof java.awt.Container childContainer) {
                forceHighContrastTextInContainerRecursive(childContainer);
            }
        }
    }

    /**
     * EMERGENCY FIX: Aggressively force white text on ALL labels in container
     * This method directly iterates and sets foreground immediately without threading
     */
    public static void emergencyForceWhiteText(java.awt.Container container) {
        if (container == null) {
            return;
        }
        
        emergencyForceWhiteTextRecursive(container);
    }
    
    private static void emergencyForceWhiteTextRecursive(java.awt.Container container) {
        try {
            for (java.awt.Component comp : container.getComponents()) {
                if (comp instanceof javax.swing.JLabel label) {
                    // FORCE pure white text immediately
                    label.setForeground(PURE_WHITE);
                    label.setOpaque(false);
                    
                    // Make font bold for better visibility
                    Font currentFont = label.getFont();
                    if (currentFont != null) {
                        label.setFont(new Font(currentFont.getName(), Font.BOLD, Math.max(currentFont.getSize(), 13)));
                    } else {
                        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    }
                    
                    // Force repaint
                    label.repaint();
                }
                
                // Recursively apply to nested containers
                if (comp instanceof java.awt.Container childContainer) {
                    emergencyForceWhiteTextRecursive(childContainer);
                }
            }
        } catch (Exception e) {
            // Defensive: never let styling crash the UI
            System.err.println("Error in emergencyForceWhiteText: " + e.getMessage());
        }
    }

    public static void applyDarkPanelTheme(final javax.swing.JPanel panel) {
        if (panel != null) {
            Runnable apply = () -> {
                panel.setBackground(DARK_BG);
                panel.setOpaque(true);
            };
            if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                apply.run();
            } else {
                javax.swing.SwingUtilities.invokeLater(apply);
            }
        }
    }

    public static void applyDarkTextAreaTheme(javax.swing.JTextArea area) {
        styleTextComponent(area, PURE_WHITE, PURE_BLACK, LIGHT_BORDER);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    public static void showStyledDialog(javax.swing.JFrame parent, String message, String title, int messageType) {
        // Create main panel with gradient-like effect
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);  // White background for dialog
        
        // Create header panel
        javax.swing.JPanel headerPanel = new javax.swing.JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        javax.swing.JLabel titleLabel = new javax.swing.JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK);  // Black text for title
        headerPanel.add(titleLabel);
        panel.add(headerPanel);
        
        // Create message panel
        javax.swing.JPanel messagePanel = new javax.swing.JPanel();
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20));
        javax.swing.JLabel messageLabel = new javax.swing.JLabel("<html><div style='text-align: center; width: 200px; color: #000000;'>" + message + "</div></html>");
        messageLabel.setForeground(Color.BLACK);  // Black text for message
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messagePanel.add(messageLabel);
        panel.add(messagePanel);
        
        // Add decorative border
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));

        // Create and show the styled dialog
        javax.swing.JOptionPane pane = new javax.swing.JOptionPane(
            panel,
            messageType,
            javax.swing.JOptionPane.DEFAULT_OPTION
        );
        pane.setBackground(Color.WHITE);
        
        javax.swing.JDialog dialog = pane.createDialog(parent, "");
        dialog.setBackground(Color.WHITE);
        
        // Make sure all components in the dialog use our theme
        java.awt.Component[] components = pane.getComponents();
        for (java.awt.Component comp : components) {
            if (comp instanceof javax.swing.JPanel) {
                comp.setBackground(Color.WHITE);
            }
            // Style all buttons with high contrast theme
            if (comp instanceof javax.swing.JButton btn) {
                btn.setBackground(new Color(240, 240, 240)); // Light gray background
                btn.setForeground(PURE_BLACK);  // Pure black text for maximum contrast
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Bold, slightly larger font
                btn.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(new Color(180, 180, 180), 2),
                    javax.swing.BorderFactory.createEmptyBorder(6, 18, 6, 18)
                ));
            }
        }
        
        applyDialogButtonTheme(dialog.getContentPane());

        // Ensure buttons created lazily by JOptionPane are also styled with dark text
        // This covers cases where the button(s) are realized only after setVisible or packing.
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                applyDialogButtonTheme(dialog.getContentPane());
            }
        });
        // Also queue a styling pass on the EDT to run after component hierarchy is ready
        javax.swing.SwingUtilities.invokeLater(() -> applyDialogButtonTheme(dialog.getContentPane()));

        dialog.setVisible(true);
    }

    /**
     * Returns true if the color is considered a light color based on luminance.
     */
    private static boolean isLight(java.awt.Color c) {
        if (c == null) return false;
        double r = c.getRed() / 255.0;
        double g = c.getGreen() / 255.0;
        double b = c.getBlue() / 255.0;
        double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        return luminance >= 0.75; // conservative: only flip for clearly light backgrounds
    }

    /**
     * Force dark text on a component when it sits on a light background.
     * This is an aggressive fix to ensure visibility for buttons, inputs, and labels
     * that render with light backgrounds due to LAF defaults or NetBeans GUI builder.
     */
    private static void forceDarkTextIfLightBackground(java.awt.Component comp) {
        if (comp == null) return;
        try {
            java.awt.Color bg = comp.getBackground();
            boolean light = isLight(bg);

            if (light) {
                if (comp instanceof javax.swing.AbstractButton btn) {
                    btn.setForeground(PURE_BLACK);
                } else if (comp instanceof javax.swing.JLabel label) {
                    label.setForeground(PURE_BLACK);
                } else if (comp instanceof javax.swing.text.JTextComponent text) {
                    text.setForeground(PURE_BLACK);
                    text.setCaretColor(PURE_BLACK);
                    // keep the user's chosen background; only ensure border remains visible
                } else if (comp instanceof javax.swing.JComboBox<?> combo) {
                    combo.setForeground(PURE_BLACK);
                    combo.setBackground(PURE_WHITE);
                    applyHighContrastComboBoxTheme(combo);
                } else if (comp instanceof javax.swing.JList<?> list) {
                    list.setForeground(PURE_BLACK);
                } else if (comp instanceof javax.swing.JTable table) {
                    table.setForeground(PURE_BLACK);
                }
            }
        } catch (Throwable ignore) {
            // Defensive: never let styling crash the UI thread
        }
    }

    /**
     * Recursively enforce dark text on light surfaces within the given container.
     */
    public static void enforceHighContrastOnLightSurfaces(java.awt.Container container) {
        if (container == null) return;
        for (java.awt.Component c : container.getComponents()) {
            forceDarkTextIfLightBackground(c);
            if (c instanceof java.awt.Container child) {
                enforceHighContrastOnLightSurfaces(child);
            }
        }
    }

    /**
     * Install a global hook that re-applies contrast enforcement whenever a window opens
     * or components are shown. This guarantees that late-created buttons like JOptionPane
     * "OK" will receive dark text on light backgrounds.
     */
    public static void installGlobalHighContrastEnforcer() {
        if (CONTRAST_ENFORCER_INSTALLED) return;
        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            Object src = event.getSource();
            if (src instanceof java.awt.Window w) {
                javax.swing.SwingUtilities.invokeLater(() -> enforceHighContrastOnLightSurfaces(w));
            } else if (src instanceof java.awt.Container c) {
                javax.swing.SwingUtilities.invokeLater(() -> enforceHighContrastOnLightSurfaces(c));
            }
        }, java.awt.AWTEvent.WINDOW_EVENT_MASK | java.awt.AWTEvent.COMPONENT_EVENT_MASK | java.awt.AWTEvent.CONTAINER_EVENT_MASK);
        CONTRAST_ENFORCER_INSTALLED = true;
    }

    public static void showInfo(javax.swing.JFrame parent, String message) {
        showStyledDialog(parent, message, "Information", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(javax.swing.JFrame parent, String message) {
        showStyledDialog(parent, message, "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(javax.swing.JFrame parent, String message) {
        showStyledDialog(parent, message, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    public static void applyDarkTableTheme(javax.swing.JTable table) {
        if (table == null) {
            return;
        }

        final Font headerFont = new Font("Segoe UI", Font.BOLD, 14);  // Slightly larger for headers
        final Font cellFont = new Font("Segoe UI", Font.PLAIN, 13);   // Slightly larger for cells

        javax.swing.table.JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setOpaque(true);
            header.setBackground(DARKER_BG);
            header.setForeground(PURE_WHITE);  // FORCE pure white headers
            header.setFont(headerFont);
            header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, ACCENT_BORDER));
            header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    setBackground(DARKER_BG);
                    setForeground(PURE_WHITE);  // FORCE pure white header text
                    setFont(headerFont);
                    return c;
                }
            });
        }

        table.setBackground(DARK_BG);
        table.setForeground(PURE_WHITE);  // FORCE pure white table text
        table.setFont(cellFont);
        table.setSelectionBackground(BUTTON_HOVER);
        table.setSelectionForeground(PURE_WHITE);  // FORCE pure white selected text
        table.setGridColor(ACCENT_BORDER);
        table.setRowHeight(30);  // Slightly taller for better readability
        table.setOpaque(true);

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setForeground(PURE_WHITE);  // FORCE pure white for ALL table cell text
                setFont(cellFont);
                if (isSelected) {
                    setBackground(BUTTON_HOVER);
                    setForeground(PURE_WHITE);  // Ensure selection maintains white text
                } else {
                    setBackground(row % 2 == 0 ? DARK_BG : INPUT_BG);
                }
                return c;
            }
        });
    }

    private static void applyDialogButtonTheme(java.awt.Container container) {
        if (container == null) {
            return;
        }

        for (java.awt.Component comp : container.getComponents()) {
            if (comp instanceof javax.swing.JButton btn) {
                // High contrast theme for dialog buttons
                btn.setBackground(new Color(240, 240, 240)); // Light gray background
                btn.setForeground(PURE_BLACK); // Pure black text for maximum contrast
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Slightly larger, bold font
                btn.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(new Color(180, 180, 180), 2), // Medium gray border, 2px
                    javax.swing.BorderFactory.createEmptyBorder(6, 18, 6, 18)
                ));
                
                // Add hover effect that maintains contrast
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        btn.setBackground(new Color(230, 230, 230)); // Slightly darker on hover
                        btn.setForeground(PURE_BLACK); // Maintain pure black text
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        btn.setBackground(new Color(240, 240, 240)); // Return to original color
                        btn.setForeground(PURE_BLACK); // Maintain pure black text
                    }
                });
            }

            if (comp instanceof java.awt.Container child) {
                applyDialogButtonTheme(child);
            }
        }
    }
    
    /**
     * COMPREHENSIVE DIALOG FIX: Apply high-contrast theme to all components in a dialog
     * This method ensures ALL text elements are pure white for maximum visibility
     */
    public static void applyHighContrastDialogTheme(java.awt.Container container) {
        if (container == null) {
            return;
        }
        
        Runnable apply = () -> {
            applyHighContrastDialogThemeRecursive(container);
        };
        
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }
    
    private static void applyHighContrastDialogThemeRecursive(java.awt.Container container) {
        // Set container background to dark
        if (container instanceof javax.swing.JPanel panel) {
            panel.setBackground(DARK_BG);
            panel.setOpaque(true);
        } else if (container instanceof javax.swing.JFrame frame) {
            frame.getContentPane().setBackground(DARK_BG);
        }
        
        for (java.awt.Component comp : container.getComponents()) {
            // Force pure white text on ALL labels
            if (comp instanceof javax.swing.JLabel label) {
                label.setForeground(PURE_WHITE);
                Font currentFont = label.getFont();
                if (currentFont != null) {
                    int fontSize = Math.max(currentFont.getSize(), 12);
                    int style = currentFont.getStyle() | Font.BOLD; // Make bold for better visibility
                    label.setFont(new Font(currentFont.getName(), style, fontSize));
                } else {
                    label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                }
                label.putClientProperty("text.antialiasing", java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            
            // Apply dark surface theme to all text inputs
            if (comp instanceof javax.swing.JTextField textField) {
                applyDarkSurfaceTextFieldTheme(textField);
            } else if (comp instanceof javax.swing.JPasswordField passwordField) {
                applyDarkSurfacePasswordFieldTheme(passwordField);
            } else if (comp instanceof javax.swing.JTextArea textArea) {
                applyDarkTextAreaTheme(textArea);
            }
            
            // Apply dark theme to combo boxes
            if (comp instanceof javax.swing.JComboBox<?> combo) {
                applyDarkComboBoxTheme(combo);
            }
            
            // Apply dark theme to buttons
            if (comp instanceof javax.swing.JButton button) {
                applyDarkButtonTheme(button, false); // Default to secondary style
            }
            
            // Apply dark theme to tables
            if (comp instanceof javax.swing.JTable table) {
                applyDarkTableTheme(table);
            }
            
            // Recursively apply to nested containers
            if (comp instanceof java.awt.Container childContainer) {
                applyHighContrastDialogThemeRecursive(childContainer);
            }
        }
    }

    // ===================================================================================
    // COMPREHENSIVE FRONTEND ENHANCEMENT: CONTRAST, READABILITY, AND POLISH
    // ===================================================================================

    /**
     * UNIVERSAL CONTRAST: Enhanced table theme ensuring ALL table text is pure white on dark backgrounds
     * Fixes invisibility of headers, data, and column labels
     */
    public static void applyUniversalTableContrastTheme(javax.swing.JTable table) {
        if (table == null) {
            return;
        }

        final Font headerFont = new Font("Segoe UI", Font.BOLD, 15);  // Larger, bold headers for hierarchy
        final Font cellFont = new Font("Segoe UI", Font.PLAIN, 13);   // Clean, readable cell text

        // FORCE WHITE-ON-DARK for table headers
        javax.swing.table.JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setOpaque(true);
            header.setBackground(DARKER_BG);
            header.setForeground(PURE_WHITE);  // FORCE pure white (#FFFFFF)
            header.setFont(headerFont);
            header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, PURE_WHITE));
            header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    setBackground(DARKER_BG);
                    setForeground(PURE_WHITE);  // FORCE pure white header text
                    setFont(headerFont);
                    setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 1, ACCENT_BORDER));
                    return c;
                }
            });
        }

        // FORCE WHITE-ON-DARK for all table cell data
        table.setBackground(DARK_BG);
        table.setForeground(PURE_WHITE);  // FORCE pure white table text
        table.setFont(cellFont);
        table.setSelectionBackground(new Color(100, 130, 180)); // Highlighted selection
        table.setSelectionForeground(PURE_WHITE);  // FORCE pure white selected text
        table.setGridColor(new Color(120, 120, 140)); // Subtle grid lines
        table.setRowHeight(32);  // Taller rows for better readability
        table.setOpaque(true);
        table.setShowGrid(true);

        // Enhanced cell renderer with alternating row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setForeground(PURE_WHITE);  // FORCE pure white for ALL table cell text
                setFont(cellFont);
                if (isSelected) {
                    setBackground(new Color(100, 130, 180));
                    setForeground(PURE_WHITE);  // Ensure selection maintains white text
                } else {
                    setBackground(row % 2 == 0 ? DARK_BG : new Color(55, 55, 85)); // Alternating rows
                }
                setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)); // Internal padding
                return c;
            }
        });
    }

    /**
     * UNIVERSAL CONTRAST: Enhanced input field theme with clear visual borders and high contrast
     * Ensures white input backgrounds with pure black text for maximum readability
     */
    public static void applyUniversalInputContrastTheme(javax.swing.text.JTextComponent component) {
        if (component == null) {
            return;
        }

        Runnable apply = () -> {
            component.setOpaque(true);
            component.setBackground(PURE_WHITE);  // White background for input clarity
            component.setForeground(PURE_BLACK); // Pure black text for maximum contrast
            component.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Slightly larger font
            component.setCaretColor(PURE_BLACK);
            component.setSelectedTextColor(PURE_WHITE);
            component.setSelectionColor(new Color(100, 130, 180));

            // Enhanced border with subtle dark outline
            component.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(60, 60, 60), 2),  // Dark border for definition
                javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10) // Generous padding
            ));

            // Add focus enhancement
            component.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    component.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(ACCENT_BLUE, 2),
                        javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
                    ));
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    component.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(new Color(60, 60, 60), 2),
                        javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
                    ));
                }
            });
        };

        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    /**
     * UNIVERSAL CONTRAST: Enhanced combo box theme ensuring dropdown text is always visible
     */
    public static void applyUniversalComboBoxContrastTheme(javax.swing.JComboBox<?> combo) {
        if (combo == null) {
            return;
        }

        Runnable apply = () -> {
            combo.setBackground(PURE_WHITE);
            combo.setForeground(PURE_BLACK);  // FORCE pure black text
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            combo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(60, 60, 60), 2),
                javax.swing.BorderFactory.createEmptyBorder(2, 8, 2, 8)
            ));
            combo.setOpaque(true);

            // Enhanced renderer for dropdown items
            combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
                @Override
                public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    if (isSelected) {
                        setForeground(PURE_WHITE);
                        setBackground(new Color(100, 130, 180));
                    } else {
                        setForeground(PURE_BLACK);
                        setBackground(PURE_WHITE);
                    }
                    setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
                    return c;
                }
            });
        };

        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    /**
     * PROFESSIONAL POLISH: Enhanced button theme with clear hierarchy and hover states
     */
    public static void applyProfessionalButtonTheme(javax.swing.JButton button, boolean isPrimary) {
        if (button == null) {
            return;
        }

        Runnable apply = () -> {
            button.setOpaque(true);
            button.setBorderPainted(true);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Larger, bold font for importance

            if (isPrimary) {
                // Primary actions: prominent styling
                button.setBackground(new Color(200, 220, 255));
                button.setForeground(PURE_BLACK);
                button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(new Color(130, 160, 220), 2),
                    javax.swing.BorderFactory.createEmptyBorder(8, 20, 8, 20)
                ));
            } else {
                // Secondary actions: subtle but clear
                button.setBackground(new Color(240, 240, 240));
                button.setForeground(PURE_BLACK);
                button.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(new Color(180, 180, 180), 2),
                    javax.swing.BorderFactory.createEmptyBorder(6, 18, 6, 18)
                ));
            }

            // Enhanced hover effects
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                Color originalBg = button.getBackground();
                Color hoverBg = isPrimary ? new Color(180, 205, 250) : new Color(225, 225, 225);

                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(hoverBg);
                    button.setForeground(PURE_BLACK); // Maintain black text
                    button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(originalBg);
                    button.setForeground(PURE_BLACK); // Maintain black text
                    button.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                }
            });
        };

        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    /**
     * VISUAL HIERARCHY: Enhanced label theme with proper typography hierarchy
     */
    public static void applyProfessionalLabelTheme(javax.swing.JLabel label, String hierarchy) {
        if (label == null) {
            return;
        }

        Runnable apply = () -> {
            label.setForeground(PURE_WHITE);  // FORCE pure white on dark backgrounds
            label.setOpaque(false);

            switch (hierarchy.toLowerCase()) {
                case "title":
                    label.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    break;
                case "header":
                    label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    break;
                case "subheader":
                    label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    break;
                default: // "body" or any other
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    break;
            }

            // Force text anti-aliasing for crisp rendering
            label.putClientProperty("text.antialiasing", java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        };

        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    /**
     * COMPREHENSIVE APPLICATION: Apply complete professional theme to an entire dialog/form
     * This method implements all contrast fixes, input clarity, and professional polish
     */
    public static void applyComprehensiveProfessionalTheme(java.awt.Container container) {
        if (container == null) {
            return;
        }

        Runnable apply = () -> {
            applyComprehensiveProfessionalThemeRecursive(container);
        };

        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            apply.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(apply);
        }
    }

    private static void applyComprehensiveProfessionalThemeRecursive(java.awt.Container container) {
        // Set dark background for container
        if (container instanceof javax.swing.JPanel panel) {
            panel.setBackground(DARK_BG);
            panel.setOpaque(true);
        } else if (container instanceof javax.swing.JFrame frame) {
            frame.getContentPane().setBackground(DARK_BG);
        }

        for (java.awt.Component comp : container.getComponents()) {
            // UNIVERSAL CONTRAST: Force pure white text on all labels
            if (comp instanceof javax.swing.JLabel label) {
                // Determine hierarchy based on font size or text content
                Font currentFont = label.getFont();
                String hierarchy = "body";
                if (currentFont != null && currentFont.getSize() >= 16) {
                    hierarchy = "title";
                } else if (currentFont != null && currentFont.getSize() >= 14) {
                    hierarchy = "header";
                }
                applyProfessionalLabelTheme(label, hierarchy);
            }

            // INPUT FIELD CLARITY: Apply universal input contrast
            if (comp instanceof javax.swing.JTextField textField) {
                applyUniversalInputContrastTheme(textField);
            } else if (comp instanceof javax.swing.JPasswordField passwordField) {
                applyUniversalInputContrastTheme(passwordField);
            }

            // DROPDOWN CLARITY: Apply universal combo box contrast
            if (comp instanceof javax.swing.JComboBox<?> combo) {
                applyUniversalComboBoxContrastTheme(combo);
            }

            // PROFESSIONAL BUTTONS: Apply enhanced button styling
            if (comp instanceof javax.swing.JButton button) {
                String buttonText = button.getText();
                boolean isPrimary = buttonText != null && (
                    buttonText.toLowerCase().contains("login") ||
                    buttonText.toLowerCase().contains("register") ||
                    buttonText.toLowerCase().contains("book") ||
                    buttonText.toLowerCase().contains("pay") ||
                    buttonText.toLowerCase().contains("ok")
                );
                applyProfessionalButtonTheme(button, isPrimary);
            }

            // TABLE ENHANCEMENT: Apply universal table contrast
            if (comp instanceof javax.swing.JTable table) {
                applyUniversalTableContrastTheme(table);
            }

            // Recursively apply to nested containers
            if (comp instanceof java.awt.Container childContainer) {
                applyComprehensiveProfessionalThemeRecursive(childContainer);
            }
        }
    }

    /**
     * MARGIN AND PADDING: Apply consistent spacing throughout a container
     */
    public static void applyConsistentSpacing(java.awt.Container container) {
        if (container == null) {
            return;
        }

        if (container instanceof javax.swing.JPanel panel) {
            // Add consistent border padding to panels
            panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20));
        }

        // Apply consistent gaps to layout managers
        if (container.getLayout() instanceof java.awt.GridBagLayout) {
            // GridBagLayout spacing is handled by constraints, not the layout itself
        } else if (container.getLayout() instanceof java.awt.FlowLayout flowLayout) {
            flowLayout.setHgap(10);
            flowLayout.setVgap(8);
        } else if (container.getLayout() instanceof java.awt.BorderLayout) {
            // BorderLayout doesn't have gap settings, use component borders instead
        }
    }

    /**
     * PLACEHOLDER TEXT: Add placeholder text support to text fields
     */
    public static void addPlaceholderText(javax.swing.JTextField textField, String placeholderText) {
        if (textField == null || placeholderText == null) {
            return;
        }

        final Color placeholderColor = new Color(120, 120, 120); // Medium gray
        final Color normalColor = textField.getForeground();

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholderText)) {
                    textField.setText("");
                    textField.setForeground(normalColor);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholderText);
                    textField.setForeground(placeholderColor);
                }
            }
        });

        // Set initial placeholder state
        if (textField.getText().isEmpty()) {
            textField.setText(placeholderText);
            textField.setForeground(placeholderColor);
        }
    }
}