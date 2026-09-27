<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.Order, com.apollocare.model.OrderItem, java.util.List, java.time.format.DateTimeFormatter" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    Order  order = (Order) request.getAttribute("order");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    String[] statuses = {"PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"};

    // Check for flash messages from UpdateOrderStatusServlet redirect
    String successMsg = request.getParameter("success");
    String errorMsg   = request.getParameter("error");
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

        <div style="display:flex; align-items:center; gap:12px; margin-bottom:20px;">
            <a href="<%= contextPath %>/admin/orders" class="btn btn-outline-primary btn-sm">
                <i class="bi bi-arrow-left"></i>
            </a>
            <h1 class="page-title mb-0">
                <i class="bi bi-bag me-2 text-primary"></i>Order #<%= order.getOrderId() %>
            </h1>
        </div>

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

        <div style="display:flex; gap:16px; flex-wrap:wrap; margin-bottom:20px;">

            <%-- Order Summary Card --%>
            <div style="flex:1; min-width:240px; background:#fff; border:1px solid var(--border);
                        border-radius:10px; padding:20px; box-shadow:var(--card-shadow);">
                <h2 style="font-size:0.9rem; font-weight:700; color:var(--text-muted);
                            text-transform:uppercase; letter-spacing:0.5px; margin-bottom:12px;">
                    Order Info
                </h2>
                <div style="display:flex; flex-direction:column; gap:8px; font-size:0.88rem;">
                    <div><strong>Customer:</strong> <%= order.getUserName() %></div>
                    <div><strong>Date:</strong> <%= order.getOrderDate() != null ? order.getOrderDate().format(fmt) : "N/A" %></div>
                    <div>
                        <strong>Status:</strong>
                        <span class="status-badge status-<%= order.getStatus() %> ms-1">
                            <%= order.getStatus() %>
                        </span>
                    </div>
                    <div><strong>Total:</strong>
                        <span style="color:var(--primary); font-weight:700;">
                            &#8377;<%= String.format("%.2f", order.getTotalAmount()) %>
                        </span>
                    </div>
                    <div><strong>Address:</strong><br>
                        <span style="color:var(--text-muted);"><%= order.getDeliveryAddress() %></span>
                    </div>
                </div>
            </div>

            <%-- Update Status Card --%>
            <div style="flex:1; min-width:240px; background:#fff; border:1px solid var(--border);
                        border-radius:10px; padding:20px; box-shadow:var(--card-shadow);">
                <h2 style="font-size:0.9rem; font-weight:700; color:var(--text-muted);
                            text-transform:uppercase; letter-spacing:0.5px; margin-bottom:12px;">
                    Update Status
                </h2>
                <form action="<%= contextPath %>/admin/order/status" method="post">
                    <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                    <div class="form-group">
                        <label class="form-label" for="newStatus">New Status</label>
                        <select id="newStatus" name="newStatus" class="form-select">
                            <% for (String s : statuses) { %>
                            <option value="<%= s %>" <%= s.equals(order.getStatus()) ? "selected" : "" %>>
                                <%= s %>
                            </option>
                            <% } %>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm"
                            onclick="return confirm('Update order status?')">
                        <i class="bi bi-check-circle me-1"></i>Update Status
                    </button>
                </form>
            </div>

        </div>

        <%-- Order Items Table --%>
        <h2 style="font-size:1rem; font-weight:700; margin-bottom:10px;">Order Items</h2>
        <table class="data-table">
            <thead>
                <tr>
                    <th>Medicine</th>
                    <th style="text-align:center;">Quantity</th>
                    <th style="text-align:right;">Unit Price</th>
                    <th style="text-align:right;">Line Total</th>
                </tr>
            </thead>
            <tbody>
                <% if (order.getItems() != null) { for (OrderItem item : order.getItems()) { %>
                <tr>
                    <td><%= item.getMedicineName() %></td>
                    <td style="text-align:center;"><%= item.getQuantity() %></td>
                    <td style="text-align:right;">&#8377;<%= String.format("%.2f", item.getPrice()) %></td>
                    <td style="text-align:right; font-weight:600;">&#8377;<%= String.format("%.2f", item.getLineTotal()) %></td>
                </tr>
                <% } } %>
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="3" style="text-align:right; font-weight:600;">Grand Total</td>
                    <td style="text-align:right; font-weight:700; color:var(--primary);">
                        &#8377;<%= String.format("%.2f", order.getTotalAmount()) %>
                    </td>
                </tr>
            </tfoot>
        </table>

    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
