package com.mycompany.airlinereservation;

/**
 * SessionManager.java
 * 
 * Singleton class for managing user session information.
 * Stores the currently authenticated user's username and provides access to it
 * throughout the application.
 * 
 * Usage:
 *   - After successful login: SessionManager.getInstance().setCurrentUser("username")
 *   - To get current user: String username = SessionManager.getInstance().getCurrentUser()
 *   - On logout: SessionManager.getInstance().clearSession()
 * 
 * This ensures data isolation - each dashboard view only shows data for the logged-in user.
 */
public class SessionManager {
    
    private static SessionManager instance;
    private String currentUsername;
    
    /**
     * Private constructor to prevent direct instantiation
     */
    private SessionManager() {
        this.currentUsername = null;
    }
    
    /**
     * Gets the singleton instance of SessionManager
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Sets the currently logged-in user
     * @param username The username of the authenticated user
     */
    public void setCurrentUser(String username) {
        this.currentUsername = username;
        System.out.println("✅ SessionManager: User '" + username + "' logged in");
    }
    
    /**
     * Gets the currently logged-in user
     * @return The username of the current user, or null if no user is logged in
     */
    public String getCurrentUser() {
        return this.currentUsername;
    }
    
    /**
     * Checks if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return this.currentUsername != null && !this.currentUsername.trim().isEmpty();
    }
    
    /**
     * Clears the session (used on logout)
     */
    public void clearSession() {
        if (this.currentUsername != null) {
            System.out.println("✅ SessionManager: User '" + this.currentUsername + "' logged out");
        }
        this.currentUsername = null;
    }
    
    /**
     * Gets the current user with a null-check message
     * Useful for debugging when user is not logged in
     * @return The current username
     * @throws IllegalStateException if no user is logged in
     */
    public String getCurrentUserOrThrow() {
        if (!isUserLoggedIn()) {
            throw new IllegalStateException("❌ No user is currently logged in. Please log in first.");
        }
        return this.currentUsername;
    }
}
