package com.apollocare.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password hashing and verification using BCrypt.
 *
 * Why BCrypt?
 * - Automatically generates a salt on each hash, so two hashes of the
 *   same password are always different.
 * - The salt is embedded in the stored hash, so verification works
 *   without storing the salt separately.
 * - Work factor (cost=10) makes brute-force attacks expensive.
 */
public class PasswordUtil {

    private static final int BCRYPT_COST = 10;

    /**
     * Hashes a plain-text password.
     * Call this during user registration before storing the password in the DB.
     *
     * @param plainPassword raw password from the registration form
     * @return BCrypt hash string (includes embedded salt)
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_COST));
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     * Call this during login.
     *
     * @param plainPassword  password entered at login
     * @param hashedPassword hash stored in the DB
     * @return true if the password matches
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * Utility main method — run this once to generate a BCrypt hash
     * for any password (useful for seeding admin users in SQL scripts).
     */
    public static void main(String[] args) {
        String password = "Admin@123";
        System.out.println("BCrypt hash for '" + password + "':");
        System.out.println(hashPassword(password));
    }

    private PasswordUtil() {}
}
