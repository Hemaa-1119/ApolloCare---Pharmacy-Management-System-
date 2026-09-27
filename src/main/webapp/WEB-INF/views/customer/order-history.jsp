<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.Order, com.apollocare.model.OrderItem, java.util.List, java.time.format.DateTimeFormatter" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    List<Order> orders = (List<Order>) request.getAttribute("orders");
    Order       order  = (Order)       request.getAttribute("order");
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
%>

<div class="page-container">

    <h1 class="page-title">
        <i class="bi bi-clock-history me-2 text-primary"></i>My Orders
    </h1>

    <%-- Success flash after order placement --%>
    <% if ("true".equals(request.getParameter("success"))) { %>
    <div class="alert alert-success">
        <i class="bi bi-check-circle me-1"></i>
        <strong>Order placed successfully!</strong>
        Your order #<%= request.getParameter("orderId") %> has been received.
    </div>
    <% } %>

    <%-- Order Detail View (when orderId is in URL) --%>
    <% if (order != null) { %>
    <a href="<%= contextPath %>/order/history" class="btn btn-outline-primary btn-sm mb-3">
        <i class="bi bi-arrow-left me-1"></i>All Orders
    </a>

    <div style="background:#fff; border:1px solid var(--border); border-radius:10px;
                box-shadow:var(--card-shadow); padding:24px; margin-bottom:16px;">
        <div style="display:flex; justify-content:space-between; flex-wrap:wrap; gap:12px; margin-bottom:16px;">
            <div>
                <div style="font-size:0.78rem; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.5px;">Order ID</div>
                <div style="font-size:1.2rem; font-weight:700;">#<%= order.getOrderId() %></div>
            </div>
            <div>
                <div style="font-size:0.78rem; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.5px;">Date</div>
                <div><%= order.getOrderDate() != null ? order.getOrderDate().format(fmt) : "N/A" %></div>
            </div>
            <div>
                <div style="font-size:0.78rem; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.5px;">Status</div>
                <div><span class="status-badge status-<%= order.getStatus() %>"><%= order.getStatus() %></span></div>
            </div>
            <div>
                <div style="font-size:0.78rem; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.5px;">Total</div>
                <div style="font-size:1.1rem; font-weight:700; color:var(--primary);">
                    &#8377;<%= String.format("%.2f", order.getTotalAmount()) %>
                </div>
            </div>
        </div>

        <div style="margin-bottom:16px;">
            <div style="font-size:0.78rem; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.5px; margin-bottom:4px;">
                Delivery Address
            </div>
            <div style="font-size:0.9rem;"><%= order.getDeliveryAddress() %></div>
        </div>

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

    <% } else { %>
    <%-- Order List View --%>
    <% if (orders == null || orders.isEmpty()) { %>
    <div style="text-align:center; padding:60px 0; color:var(--text-muted);">
        <i class="bi bi-bag" style="font-size:3rem;"></i>
        <p style="margin-top:12px;">You haven't placed any orders yet.</p>
        <a href="<%= contextPath %>/medicines" class="btn btn-primary mt-3">
            <i class="bi bi-grid me-1"></i>Browse Medicines
        </a>
    </div>
    <% } else { %>
    <table class="data-table">
        <thead>
            <tr>
                <th>Order #</th>
                <th>Date</th>
                <th>Items</th>
                <th style="text-align:right;">Total</th>
                <th style="text-align:center;">Status</th>
                <th style="text-align:center;">Details</th>
            </tr>
        </thead>
        <tbody>
            <% for (Order o : orders) { %>
            <tr>
                <td><strong>#<%= o.getOrderId() %></strong></td>
                <td style="font-size:0.85rem;">
                    <%= o.getOrderDate() != null ? o.getOrderDate().format(fmt) : "N/A" %>
                </td>
                <td style="color:var(--text-muted); font-size:0.85rem;">
                    <%= o.getItems() != null ? o.getItems().size() + " item(s)" : "—" %>
                </td>
                <td style="text-align:right; font-weight:600; color:var(--primary);">
                    &#8377;<%= String.format("%.2f", o.getTotalAmount()) %>
                </td>
                <td style="text-align:center;">
                    <span class="status-badge status-<%= o.getStatus() %>"><%= o.getStatus() %></span>
                </td>
                <td style="text-align:center;">
                    <a href="<%= contextPath %>/order/history?orderId=<%= o.getOrderId() %>"
                       class="btn btn-sm btn-outline-primary">
                        <i class="bi bi-eye"></i> View
                    </a>
                </td>
            </tr>
            <% } %>
        </tbody>
    </table>
    <% } %>
    <% } %>

</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
