package com.apollocare.servlet.customer;

import com.apollocare.model.User;
import com.apollocare.service.UserService;
import com.apollocare.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Handles customer self-registration.
 *
 * GET  /register → show the registration form
 * POST /register → validate, create account, redirect to login
 *
 * Flow:
 * register.jsp → POST → RegisterServlet → UserService → UserDAO → MySQL
 *             ↓ success: redirect to /login?registered=true
 *             ↓ failure: forward back to register.jsp with error
 *
 * Demonstrates: PRG pattern (Post-Redirect-Get), server-side validation,
 *               RequestDispatcher.forward(), response.sendRedirect()
 */
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    /** GET — show the empty registration form */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp")
               .forward(request, response);
    }

    /** POST — validate input, register user, redirect to login */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Read form parameters
        String name            = request.getParameter("name");
        String email           = request.getParameter("email");
        String password        = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone           = request.getParameter("phone");
        String address         = request.getParameter("address");

        // --- Server-side validation (independent of client-side JS validation) ---
        String error = validateRegistrationInput(name, email, password, confirmPassword, phone, address);

        if (error != null) {
            // Forward back to form with error message and repopulate fields
            request.setAttribute("error",   error);
            request.setAttribute("name",    name);
            request.setAttribute("email",   email);
            request.setAttribute("phone",   phone);
            request.setAttribute("address", address);
            request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp")
                   .forward(request, response);
            return;
        }

        // Build the user object (password is still plain text — service will hash it)
        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setPassword(password);
        user.setPhone(phone.trim());
        user.setAddress(address.trim());

        try {
            userService.register(user);
            // PRG: redirect to login with a success indicator
            response.sendRedirect(request.getContextPath() + "/login?registered=true");

        } catch (Exception e) {
            // Business rule violation (e.g., duplicate email) → redisplay form
            request.setAttribute("error",   e.getMessage());
            request.setAttribute("name",    name);
            request.setAttribute("email",   email);
            request.setAttribute("phone",   phone);
            request.setAttribute("address", address);
            request.getRequestDispatcher("/WEB-INF/views/customer/register.jsp")
                   .forward(request, response);
        }
    }

    /** Returns an error message string, or null if all fields are valid. */
    private String validateRegistrationInput(String name, String email, String password,
                                             String confirmPassword, String phone, String address) {
        if (ValidationUtil.isEmpty(name))               return "Name is required.";
        if (name.trim().length() < 2)                   return "Name must be at least 2 characters.";
        if (!ValidationUtil.isValidEmail(email))        return "Please enter a valid email address.";
        if (!ValidationUtil.isValidPassword(password))  return "Password must be at least 6 characters.";
        if (!password.equals(confirmPassword))          return "Passwords do not match.";
        if (!ValidationUtil.isValidPhone(phone))        return "Phone must be exactly 10 digits.";
        if (ValidationUtil.isEmpty(address))            return "Address is required.";
        return null;
    }
}
