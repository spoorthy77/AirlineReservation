package com.mycompany.airlinereservation;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for hashing and verifying passwords using BCrypt.
 */
public class PasswordUtils {

    // Work factor for BCrypt (10-12 is reasonable). 12 is stronger but slower.
    private static final int LOG_ROUNDS = 12;

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) return null;
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean verifyPassword(String plainPassword, String hashed) {
        if (plainPassword == null || hashed == null) return false;
        try {
            return BCrypt.checkpw(plainPassword, hashed);
        } catch (Exception ex) {
            return false;
        }
    }
}
