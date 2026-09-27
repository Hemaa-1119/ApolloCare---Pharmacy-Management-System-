<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.CartItem, com.apollocare.model.User, java.util.List" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    List<CartItem> cart  = (List<CartItem>) request.getAttribute("cart");
    User           user  = (User) request.getAttribute("user");
    double         total = request.getAttribute("total") != null ? (double) request.getAttribute("total") : 0;
    String         error = (String) request.getAttribute("error");
%>

<div class="page-container" style="max-width:800px;">
    <h1 class="page-title">
        <i class="bi bi-credit-card me-2 text-primary"></i>Checkout
    </h1>

    <%-- Error message from PlaceOrderServlet --%>
    <% if (error != null) { %>
    <div class="alert alert-danger">
        <i class="bi bi-exclamation-circle me-1"></i><%= error %>
    </div>
    <% } %>

    <div style="display:flex; gap:20px; flex-wrap:wrap;">

        <%-- LEFT: Order Summary --%>
        <div style="flex:1; min-width:280px;">
            <div style="background:#fff; border:1px solid var(--border); border-radius:10px;
                        box-shadow:var(--card-shadow); overflow:hidden;">
                <div style="padding:16px; border-bottom:1px solid var(--border);">
                    <strong>Order Summary</strong>
                    <span style="float:right; color:var(--text-muted); font-size:0.85rem;">
                        <%= cart != null ? cart.size() : 0 %> item(s)
                    </span>
                </div>
                <div style="padding:0;">
                    <% if (cart != null) { for (CartItem item : cart) { %>
                    <div style="display:flex; justify-content:space-between; padding:12px 16px;
                                border-bottom:1px solid var(--border); font-size:0.88rem;">
                        <div>
                            <div style="font-weight:600;"><%= item.getMedicine().getMedicineName() %></div>
                            <div style="color:var(--text-muted);">Qty: <%= item.getQuantity() %> &times; &#8377;<%= String.format("%.2f", item.getMedicine().getPrice()) %></div>
                        </div>
                        <div style="font-weight:600;">&#8377;<%= String.format("%.2f", item.getSubtotal()) %></div>
                    </div>
                    <% } } %>
                    <div style="display:flex; justify-content:space-between; padding:14px 16px;
                                background:#f8fafc;">
                        <strong>Total</strong>
                        <strong style="color:var(--primary); font-size:1.1rem;">
                            &#8377;<%= String.format("%.2f", total) %>
                        </strong>
                    </div>
                </div>
            </div>
            <a href="<%= contextPath %>/cart" class="btn btn-outline-primary btn-sm mt-3 w-100">
                <i class="bi bi-arrow-left me-1"></i>Back to Cart
            </a>
        </div>

        <%-- RIGHT: Delivery Details & Place Order --%>
        <div style="flex:1; min-width:280px;">
            <div style="background:#fff; border:1px solid var(--border); border-radius:10px;
                        box-shadow:var(--card-shadow); padding:20px;">
                <h2 style="font-size:1rem; font-weight:700; margin-bottom:16px;">
                    <i class="bi bi-geo-alt me-1 text-primary"></i>Delivery Details
                </h2>

                <form action="<%= contextPath %>/order/place" method="post">

                    <div class="form-group">
                        <label class="form-label">Delivering to:</label>
                        <div style="font-weight:600; font-size:0.9rem;">
                            <%= user != null ? user.getName() : "" %>
                        </div>
                        <div style="color:var(--text-muted); font-size:0.85rem;">
                            <%= user != null ? user.getPhone() : "" %>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="deliveryAddress">Delivery Address</label>
                        <textarea id="deliveryAddress" name="deliveryAddress"
                                  class="form-control" rows="3"
                                  placeholder="Enter your full delivery address" required><%= user != null ? user.getAddress() : "" %></textarea>
                        <small style="color:var(--text-muted); font-size:0.78rem;">
                            You can edit the address before placing the order.
                        </small>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 mt-2"
                            onclick="return confirm('Place order for ₹<%= String.format("%.2f", total) %>?')">
                        <i class="bi bi-bag-check me-1"></i>
                        Place Order &mdash; &#8377;<%= String.format("%.2f", total) %>
                    </button>

                </form>
            </div>
        </div>

    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
