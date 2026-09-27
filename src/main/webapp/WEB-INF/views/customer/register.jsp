<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-container" style="max-width:520px; margin:40px auto;">
    <div class="form-card">

        <div style="text-align:center; margin-bottom:24px;">
            <i class="bi bi-person-plus-fill text-primary" style="font-size:2rem;"></i>
            <h1 style="font-size:1.4rem; font-weight:700; margin-top:8px;">Create Account</h1>
            <p style="color:var(--text-muted); font-size:0.9rem;">Join ApolloCare and order medicines online</p>
        </div>

        <%-- Server-side error message --%>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
        <div class="alert alert-danger">
            <i class="bi bi-exclamation-circle me-1"></i>
            <%= error %>
        </div>
        <% } %>

        <%-- Registration Form --%>
        <form action="<%= contextPath %>/register" method="post"
              data-validate-form="registerForm" novalidate>

            <div class="form-group">
                <label class="form-label" for="name">Full Name</label>
                <input type="text" id="name" name="name" class="form-control"
                       placeholder="John Doe"
                       value="<%= request.getAttribute("name") != null ? request.getAttribute("name") : "" %>">
            </div>

            <div class="form-group">
                <label class="form-label" for="email">Email Address</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="you@example.com"
                       value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>">
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Minimum 6 characters">
            </div>

            <div class="form-group">
                <label class="form-label" for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                       placeholder="Re-enter your password">
            </div>

            <div class="form-group">
                <label class="form-label" for="phone">Phone Number</label>
                <input type="tel" id="phone" name="phone" class="form-control"
                       placeholder="10-digit mobile number" maxlength="10"
                       value="<%= request.getAttribute("phone") != null ? request.getAttribute("phone") : "" %>">
            </div>

            <div class="form-group">
                <label class="form-label" for="address">Delivery Address</label>
                <textarea id="address" name="address" class="form-control" rows="2"
                          placeholder="Full address for medicine delivery"><%= request.getAttribute("address") != null ? request.getAttribute("address") : "" %></textarea>
            </div>

            <button type="submit" class="btn btn-primary w-100 mt-2">
                <i class="bi bi-person-plus me-1"></i>Create Account
            </button>

        </form>

        <div style="text-align:center; margin-top:20px; font-size:0.88rem; color:var(--text-muted);">
            Already have an account?
            <a href="<%= contextPath %>/login">Sign in</a>
        </div>

    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
