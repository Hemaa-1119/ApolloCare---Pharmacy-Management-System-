package com.apollocare.filter;

import com.apollocare.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * ADMIN AUTHORIZATION FILTER
 * Answers the question: "Does the logged-in user have ADMIN role?"
 *
 * Mapped in web.xml to: /admin/*
 *
 * IMPORTANT — Filter ordering in web.xml:
 * For /admin/* URLs, BOTH filters run:
 *   1. AuthenticationFilter → checks "is logged in?"
 *   2. AdminFilter (this)   → checks "is ADMIN?"
 *
 * By the time this filter runs, AuthenticationFilter has already
 * verified that a valid session and user exist. However, this filter
 * performs its own null-check defensively for correctness.
 *
 * A logged-in CUSTOMER attempting /admin/* is redirected to the
 * medicines page with an "unauthorized" error message.
 *
 * Demonstrates: Filter chaining, role-based authorization,
 *               separation of authentication and authorization.
 */
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // At this point, AuthenticationFilter has already confirmed the user is logged in.
        // Defensive null-check anyway.
        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"ADMIN".equals(user.getRole())) {
            // Logged in but NOT an admin — deny access
            httpResponse.sendRedirect(httpRequest.getContextPath() +
                                      "/medicines?error=unauthorized");
            return;
        }

        // User is authenticated AND is ADMIN — proceed
        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}
