<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.User, java.util.List, com.apollocare.model.CartItem" %>
<%
    // Retrieve the logged-in user from session (null if not logged in)
    User currentUser = null;
    jakarta.servlet.http.HttpSession currentSession = request.getSession(false);
    if (currentSession != null) {
        currentUser = (User) currentSession.getAttribute("user");
    }

    // Cart item count for the cart badge
    int cartCount = 0;
    if (currentSession != null) {
        List<CartItem> sessionCart = (List<CartItem>) currentSession.getAttribute("cart");
        if (sessionCart != null) cartCount = sessionCart.size();
    }

    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="contextPath" content="<%= contextPath %>">
    <title>ApolloCare - Online Pharmacy</title>

    <!-- Bootstrap 5 CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <!-- Custom Styles -->
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">

    <!-- Set contextPath for validate.js -->
    <script>const contextPath = '<%= contextPath %>';</script>
</head>
<body>

<!-- ================================================================
     NAVIGATION BAR
     ================================================================ -->
<nav class="navbar navbar-expand-lg">
    <div class="container-fluid px-4">

        <!-- Brand -->
        <a class="navbar-brand" href="<%= contextPath %>/medicines">
            <i class="bi bi-heart-pulse-fill me-1"></i>Apollo<span>Care</span>
        </a>

        <!-- Mobile Toggle -->
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarContent" aria-expanded="false">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">

            <!-- Search (customer view only) -->
            <% if (currentUser == null || !currentUser.isAdmin()) { %>
            <form class="d-flex mx-auto" style="max-width:400px;width:100%;"
                  action="<%= contextPath %>/medicines" method="get">
                <input class="form-control me-2" type="search" name="keyword"
                       placeholder="Search medicines..." aria-label="Search"
                       value="">
                <button class="btn btn-outline-primary" type="submit">
                    <i class="bi bi-search"></i>
                </button>
            </form>
            <% } %>

            <!-- Right-side nav links -->
            <ul class="navbar-nav ms-auto align-items-center gap-1">

                <% if (currentUser == null) { %>
                <!-- Guest Links -->
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/medicines">
                        <i class="bi bi-grid me-1"></i>Medicines
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/login">
                        <i class="bi bi-box-arrow-in-right me-1"></i>Login
                    </a>
                </li>
                <li class="nav-item">
                    <a class="btn btn-primary btn-sm" href="<%= contextPath %>/register">
                        Register
                    </a>
                </li>

                <% } else if (currentUser.isAdmin()) { %>
                <!-- Admin Links -->
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/admin/dashboard">
                        <i class="bi bi-speedometer2 me-1"></i>Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/admin/medicines">
                        <i class="bi bi-capsule me-1"></i>Medicines
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/admin/orders">
                        <i class="bi bi-bag me-1"></i>Orders
                    </a>
                </li>
                <li class="nav-item">
                    <span class="nav-link text-muted">
                        <i class="bi bi-person-circle me-1"></i><%= currentUser.getName() %>
                        <small class="badge bg-primary ms-1">Admin</small>
                    </span>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-danger" href="<%= contextPath %>/logout">
                        <i class="bi bi-box-arrow-right me-1"></i>Logout
                    </a>
                </li>

                <% } else { %>
                <!-- Customer Links -->
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/medicines">
                        <i class="bi bi-grid me-1"></i>Medicines
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/order/history">
                        <i class="bi bi-clock-history me-1"></i>My Orders
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link cart-icon" href="<%= contextPath %>/cart">
                        <i class="bi bi-cart3 fs-5"></i>
                        <% if (cartCount > 0) { %>
                        <span class="cart-badge"><%= cartCount %></span>
                        <% } %>
                    </a>
                </li>
                <li class="nav-item">
                    <span class="nav-link text-muted">
                        <i class="bi bi-person-circle me-1"></i><%= currentUser.getName() %>
                    </span>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-danger" href="<%= contextPath %>/logout">
                        <i class="bi bi-box-arrow-right me-1"></i>Logout
                    </a>
                </li>
                <% } %>

            </ul>
        </div>
    </div>
</nav>
<!-- End Navbar -->
