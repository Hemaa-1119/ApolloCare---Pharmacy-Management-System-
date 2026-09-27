package com.apollocare.service;

import com.apollocare.dao.UserDAO;
import com.apollocare.model.User;
import com.apollocare.util.PasswordUtil;

import java.sql.SQLException;

/**
 * Business logic for user registration and authentication.
 *
 * The Servlet calls this service, not the DAO directly.
 * Business rules (e.g. "email must be unique", "hash the password")
 * belong here, not in the DAO or Servlet.
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Registers a new CUSTOMER account.
     *
     * Steps:
     * 1. Check that the email is not already registered.
     * 2. Hash the plain-text password with BCrypt.
     * 3. Set role to CUSTOMER.
     * 4. Persist via UserDAO.
     *
     * @throws Exception with a user-friendly message on validation failure
     */
    public void register(User user) throws Exception {
        // Business rule: email must be unique
        User existing = userDAO.findByEmail(user.getEmail());
        if (existing != null) {
            throw new Exception("This email address is already registered. Please log in.");
        }

        // Hash password before storing
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        user.setRole("CUSTOMER");

        userDAO.saveUser(user);
    }

    /**
     * Authenticates a user by email and password.
     *
     * Steps:
     * 1. Find user by email.
     * 2. Verify the plain-text password against the stored BCrypt hash.
     * 3. Return the User object on success.
     *
     * @throws Exception with a user-friendly message if credentials are wrong
     */
    public User login(String email, String password) throws Exception {
        User user = userDAO.findByEmail(email);

        if (user == null || !PasswordUtil.checkPassword(password, user.getPassword())) {
            // Deliberately vague: do not reveal whether the email exists
            throw new Exception("Invalid email address or password.");
        }

        return user;
    }

    /** Returns total customer count for admin dashboard. */
    public int getCustomerCount() throws SQLException {
        return userDAO.countCustomers();
    }
}
