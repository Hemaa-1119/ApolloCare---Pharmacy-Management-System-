<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-container" style="max-width:480px; margin:40px auto;">
    <div class="form-card">

        <div style="text-align:center; margin-bottom:24px;">
            <i class="bi bi-heart-pulse-fill text-primary" style="font-size:2rem;"></i>
            <h1 style="font-size:1.4rem; font-weight:700; margin-top:8px;">Welcome Back</h1>
            <p style="color:var(--text-muted); font-size:0.9rem;">Sign in to your ApolloCare account</p>
        </div>

        <%-- Success message after registration --%>
        <% if ("true".equals(request.getParameter("registered"))) { %>
        <div class="alert alert-success">
            <i class="bi bi-check-circle me-1"></i>
            Registration successful! Please log in.
        </div>
        <% } %>

        <%-- Success message after logout --%>
        <% if ("true".equals(request.getParameter("logout"))) { %>
        <div class="alert alert-info">
            <i class="bi bi-info-circle me-1"></i>
            You have been logged out successfully.
        </div>
        <% } %>

        <%-- Server-side error message --%>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
        <div class="alert alert-danger">
            <i class="bi bi-exclamation-circle me-1"></i>
            <%= error %>
        </div>
        <% } %>

        <% if ("unauthorized".equals(request.getParameter("error"))) { %>
        <div class="alert alert-danger">
            <i class="bi bi-shield-exclamation me-1"></i>
            You are not authorized to access that page.
        </div>
        <% } %>

        <%-- Login Form (data-validate-form connects to messages.json rules) --%>
        <form action="<%= contextPath %>/login" method="post"
              data-validate-form="loginForm" novalidate>

            <div class="form-group">
                <label class="form-label" for="email">Email Address</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="you@example.com"
                       value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>">
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Your password">
            </div>

            <button type="submit" class="btn btn-primary w-100 mt-2">
                <i class="bi bi-box-arrow-in-right me-1"></i>Login
            </button>

        </form>

        <div style="text-align:center; margin-top:20px; font-size:0.88rem; color:var(--text-muted);">
            Don't have an account?
            <a href="<%= contextPath %>/register">Create one</a>
        </div>

    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
