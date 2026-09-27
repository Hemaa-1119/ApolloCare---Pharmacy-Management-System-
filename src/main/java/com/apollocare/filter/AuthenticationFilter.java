package com.apollocare.filter;

import com.apollocare.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * AUTHENTICATION FILTER
 * Answers the question: "Is the user logged in?"
 *
 * Mapped in web.xml to:
 *   /cart, /checkout, /order/*, /admin/*
 *
 * If no active session or no "user" attribute exists,
 * the request is redirected to the login page.
 *
 * Key point: request.getSession(false) is used deliberately.
 * getSession(true) would CREATE a new session for every unauthenticated
 * request, wasting server memory. getSession(false) returns null if
 * no session exists — the correct approach for a filter.
 *
 * Demonstrates: Filter interface, HttpSession, request.getSession(false)
 */
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // getSession(false) — returns existing session or null; never creates a new one
        HttpSession session = httpRequest.getSession(false);

        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            // Not logged in — redirect to login page
            // Store the original URL so the login page can redirect back after login
            String requestedURL = httpRequest.getRequestURI();
            httpResponse.sendRedirect(httpRequest.getContextPath() +
                                      "/login?redirect=" + requestedURL);
            return;  // Stop the filter chain — do not pass to the servlet
        }

        // Logged in — continue to the next filter or servlet
        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}
