<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.Order, java.util.List, java.time.format.DateTimeFormatter" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    List<Order> orders     = (List<Order>) request.getAttribute("orders");
    String      successMsg = (String) request.getAttribute("successMsg");
    String      errorMsg   = (String) request.getAttribute("errorMsg");
    DateTimeFormatter fmt  = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
%>

<div class="admin-layout">

    <nav class="admin-sidebar">
        <div class="admin-sidebar-title">Main Menu</div>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/dashboard"><i class="bi bi-speedometer2"></i>Dashboard</a>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/medicines"><i class="bi bi-capsule"></i>Medicines</a>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/medicine/add"><i class="bi bi-plus-circle"></i>Add Medicine</a>
        <div class="admin-sidebar-title">Orders</div>
        <a class="admin-nav-link active" href="<%= contextPath %>/admin/orders"><i class="bi bi-bag"></i>All Orders</a>
        <div class="admin-sidebar-title">Account</div>
        <a class="admin-nav-link" href="<%= contextPath %>/medicines"><i class="bi bi-globe"></i>View Store</a>
        <a class="admin-nav-link" href="<%= contextPath %>/logout" style="color:var(--danger);"><i class="bi bi-box-arrow-right"></i>Logout</a>
    </nav>

    <div class="admin-content">

        <h1 class="page-title">
            <i class="bi bi-bag me-2 text-primary"></i>All Orders
        </h1>

        <% if (successMsg != null && !successMsg.isEmpty()) { %>
        <div class="alert alert-success">
            <i class="bi bi-check-circle me-1"></i><%= successMsg %>
        </div>
        <% } %>
        <% if (errorMsg != null && !errorMsg.isEmpty()) { %>
        <div class="alert alert-danger">
            <i class="bi bi-exclamation-circle me-1"></i><%= errorMsg %>
        </div>
        <% } %>

        <% if (orders == null || orders.isEmpty()) { %>
        <p style="color:var(--text-muted); text-align:center; padding:40px 0;">No orders found.</p>
        <% } else { %>

        <div style="overflow-x:auto;">
        <table class="data-table">
            <thead>
                <tr>
                    <th>Order #</th>
                    <th>Customer</th>
                    <th>Date</th>
                    <th style="text-align:right;">Amount</th>
                    <th style="text-align:center;">Status</th>
                    <th style="text-align:center;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for (Order o : orders) { %>
                <tr>
                    <td><strong>#<%= o.getOrderId() %></strong></td>
                    <td><%= o.getUserName() != null ? o.getUserName() : "—" %></td>
                    <td style="font-size:0.85rem; white-space:nowrap;">
                        <%= o.getOrderDate() != null ? o.getOrderDate().format(fmt) : "N/A" %>
                    </td>
                    <td style="text-align:right; font-weight:600;">
                        &#8377;<%= String.format("%.2f", o.getTotalAmount()) %>
                    </td>
                    <td style="text-align:center;">
                        <span class="status-badge status-<%= o.getStatus() %>">
                            <%= o.getStatus() %>
                        </span>
                    </td>
                    <td style="text-align:center;">
                        <a href="<%= contextPath %>/admin/orders?id=<%= o.getOrderId() %>"
                           class="btn btn-sm btn-outline-primary">
                            <i class="bi bi-eye"></i> View
                        </a>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
        </div>
        <p style="color:var(--text-muted); font-size:0.82rem; margin-top:8px;">
            Total: <%= orders.size() %> order(s)
        </p>

        <% } %>

    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
