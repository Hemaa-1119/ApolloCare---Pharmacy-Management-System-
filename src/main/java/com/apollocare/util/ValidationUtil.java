package com.apollocare.util;

/**
 * Server-side validation helpers used by Servlets.
 *
 * IMPORTANT: Client-side validation (validate.js + messages.json) runs first
 * in the browser for UX, but the server NEVER trusts it. These methods
 * re-validate every incoming request independently.
 */
public class ValidationUtil {

    /** Returns true if the value is null or blank. */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /** Validates email format using a simple regex. */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /** Phone must be exactly 10 digits. */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return phone.matches("^[0-9]{10}$");
    }

    /** Password must be at least 6 characters. */
    public static boolean isValidPassword(String password) {
        return !isEmpty(password) && password.length() >= 6;
    }

    /** Validates that a string represents a positive decimal number. */
    public static boolean isPositiveDecimal(String value) {
        if (isEmpty(value)) return false;
        try {
            return Double.parseDouble(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Validates that a string represents a non-negative integer (0 or more). */
    public static boolean isNonNegativeInt(String value) {
        if (isEmpty(value)) return false;
        try {
            return Integer.parseInt(value) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Validates that a string represents a positive integer (1 or more). */
    public static boolean isPositiveInt(String value) {
        if (isEmpty(value)) return false;
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private ValidationUtil() {}
}
