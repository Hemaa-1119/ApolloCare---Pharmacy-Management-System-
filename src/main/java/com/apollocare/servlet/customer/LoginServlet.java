package com.apollocare.servlet.customer;

import com.apollocare.model.User;
import com.apollocare.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles user login.
 *
 * GET  /login → show the login form
 * POST /login → authenticate, create session, redirect to appropriate page
 *
 * Flow:
 * login.jsp → POST → LoginServlet → UserService → UserDAO → MySQL
 *           ↓ success: create HttpSession → redirect to /medicines or /admin/dashboard
 *           ↓ failure: forward back to login.jsp with error
 *
 * Thread safety note:
 * Do NOT store user data in Servlet instance variables.
 * Each request creates its own local variables (name, email etc.) on the stack.
 * The HttpSession is per-user, not shared across threads.
 *
 * Demonstrates: HttpSession creation, session.setAttribute(),
 *               forward vs redirect, thread safety awareness.
 */
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    /** GET — show the login form */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If user is already logged in, skip the login page
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null && existingSession.getAttribute("user") != null) {
            User user = (User) existingSession.getAttribute("user");
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/medicines");
            }
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp")
               .forward(request, response);
    }

    /** POST — authenticate and create session */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        // Basic null checks
        if (email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Email and password are required.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp")
                   .forward(request, response);
            return;
        }

        try {
            User user = userService.login(email.trim(), password);

            // --- Create a new HttpSession ---
            // invalidate() any existing session first to prevent session fixation
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) oldSession.invalidate();

            HttpSession session = request.getSession(true);  // create new session
            session.setAttribute("user", user);

            // Redirect to the page the user originally tried to access (if any)
            String redirect = request.getParameter("redirect");

            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else if (redirect != null && !redirect.isEmpty() &&
                       !redirect.contains("/admin")) {
                response.sendRedirect(redirect);
            } else {
                response.sendRedirect(request.getContextPath() + "/medicines");
            }

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/customer/login.jsp")
                   .forward(request, response);
        }
    }
}
