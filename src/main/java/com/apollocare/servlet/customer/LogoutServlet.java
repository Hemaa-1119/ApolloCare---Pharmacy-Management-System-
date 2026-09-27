package com.apollocare.servlet.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles user logout.
 *
 * GET /logout → invalidate session, redirect to login page
 *
 * session.invalidate() removes all attributes from the session
 * and marks the session as invalid. Any subsequent attempt to use
 * the same session ID will create a new empty session.
 *
 * Demonstrates: session.invalidate(), clean logout flow.
 */
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();  // Clears all session attributes and invalidates session
        }

        // Redirect to login page with a logout confirmation indicator
        response.sendRedirect(request.getContextPath() + "/login?logout=true");
    }

    /** Allow POST logout too (e.g., from a form button) */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
