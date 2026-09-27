<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.Order, java.util.List, java.time.format.DateTimeFormatter" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    int medicineCount = request.getAttribute("medicineCount") != null ? (int)request.getAttribute("medicineCount") : 0;
    int customerCount = request.getAttribute("customerCount") != null ? (int)request.getAttribute("customerCount") : 0;
    int orderCount    = request.getAttribute("orderCount")    != null ? (int)request.getAttribute("orderCount")    : 0;
    List<Order> recentOrders = (List<Order>) request.getAttribute("recentOrders");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
%>

<div class="admin-layout">

    <%-- Admin Sidebar --%>
    <nav class="admin-sidebar">
        <div class="admin-sidebar-title">Main Menu</div>
        <a class="admin-nav-link active" href="<%= contextPath %>/admin/dashboard">
            <i class="bi bi-speedometer2"></i>Dashboard
        </a>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/medicines">
            <i class="bi bi-capsule"></i>Medicines
        </a>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/medicine/add">
            <i class="bi bi-plus-circle"></i>Add Medicine
        </a>
        <div class="admin-sidebar-title">Orders</div>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/orders">
            <i class="bi bi-bag"></i>All Orders
        </a>
        <div class="admin-sidebar-title">Account</div>
        <a class="admin-nav-link" href="<%= contextPath %>/medicines">
            <i class="bi bi-globe"></i>View Store
        </a>
        <a class="admin-nav-link" href="<%= contextPath %>/logout"
           style="color:var(--danger);">
            <i class="bi bi-box-arrow-right"></i>Logout
        </a>
    </nav>

    <div class="admin-content">
        <h1 class="page-title">
            <i class="bi bi-speedometer2 me-2 text-primary"></i>Dashboard
        </h1>

        <%-- Stat Cards --%>
        <div class="row g-3 mb-4">
            <div class="col-sm-4">
                <div class="stat-card">
                    <div style="font-size:2rem; color:var(--primary); margin-bottom:8px;">
                        <i class="bi bi-capsule"></i>
                    </div>
                    <div class="stat-number"><%= medicineCount %></div>
                    <div class="stat-label">Total Medicines</div>
                </div>
            </div>
            <div class="col-sm-4">
                <div class="stat-card">
                    <div style="font-size:2rem; color:var(--success); margin-bottom:8px;">
                        <i class="bi bi-people"></i>
                    </div>
                    <div class="stat-number" style="color:var(--success);"><%= customerCount %></div>
                    <div class="stat-label">Customers</div>
                </div>
            </div>
            <div class="col-sm-4">
                <div class="stat-card">
                    <div style="font-size:2rem; color:var(--warning); margin-bottom:8px;">
                        <i class="bi bi-bag"></i>
                    </div>
                    <div class="stat-number" style="color:var(--warning);"><%= orderCount %></div>
                    <div class="stat-label">Total Orders</div>
                </div>
            </div>
        </div>

        <%-- Quick Actions --%>
        <div style="display:flex; gap:10px; flex-wrap:wrap; margin-bottom:28px;">
            <a href="<%= contextPath %>/admin/medicine/add" class="btn btn-primary">
                <i class="bi bi-plus-circle me-1"></i>Add Medicine
            </a>
            <a href="<%= contextPath %>/admin/medicines" class="btn btn-outline-primary">
                <i class="bi bi-list me-1"></i>Manage Medicines
            </a>
            <a href="<%= contextPath %>/admin/orders" class="btn btn-outline-primary">
                <i class="bi bi-bag me-1"></i>View All Orders
            </a>
        </div>

        <%-- Recent Orders --%>
        <h2 style="font-size:1rem; font-weight:700; margin-bottom:12px;">Recent Orders</h2>
        <% if (recentOrders == null || recentOrders.isEmpty()) { %>
        <p style="color:var(--text-muted);">No orders yet.</p>
        <% } else { %>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Order #</th>
                    <th>Customer</th>
                    <th>Date</th>
                    <th style="text-align:right;">Amount</th>
                    <th style="text-align:center;">Status</th>
                    <th style="text-align:center;">Action</th>
                </tr>
            </thead>
            <tbody>
                <% for (Order o : recentOrders) { %>
                <tr>
                    <td><strong>#<%= o.getOrderId() %></strong></td>
                    <td><%= o.getUserName() != null ? o.getUserName() : "—" %></td>
                    <td style="font-size:0.85rem;">
                        <%= o.getOrderDate() != null ? o.getOrderDate().format(fmt) : "N/A" %>
                    </td>
                    <td style="text-align:right; font-weight:600;">
                        &#8377;<%= String.format("%.2f", o.getTotalAmount()) %>
                    </td>
                    <td style="text-align:center;">
                        <span class="status-badge status-<%= o.getStatus() %>"><%= o.getStatus() %></span>
                    </td>
                    <td style="text-align:center;">
                        <a href="<%= contextPath %>/admin/orders?id=<%= o.getOrderId() %>"
                           class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-eye"></i>
                        </a>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
        <div style="margin-top:10px;">
            <a href="<%= contextPath %>/admin/orders" style="font-size:0.85rem;">
                View all orders &rarr;
            </a>
        </div>
        <% } %>

    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
