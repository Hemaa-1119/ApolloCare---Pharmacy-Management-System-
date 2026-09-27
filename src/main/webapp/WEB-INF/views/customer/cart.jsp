<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.CartItem, java.util.List" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    List<CartItem> cart  = (List<CartItem>) request.getAttribute("cart");
    double         total = 0;
    if (request.getAttribute("total") != null) {
        total = (double) request.getAttribute("total");
    }
    boolean isEmpty = (cart == null || cart.isEmpty());
%>

<div class="page-container" style="max-width:860px;">
    <h1 class="page-title">
        <i class="bi bi-cart3 me-2 text-primary"></i>Shopping Cart
    </h1>

    <%-- Flash messages --%>
    <% if ("true".equals(request.getParameter("added"))) { %>
    <div class="alert alert-success">
        <i class="bi bi-cart-check me-1"></i>Item added to cart successfully!
    </div>
    <% } %>
    <% if ("empty".equals(request.getParameter("error"))) { %>
    <div class="alert alert-warning">
        <i class="bi bi-exclamation-triangle me-1"></i>Your cart is empty. Please add items first.
    </div>
    <% } %>

    <% if (isEmpty) { %>
    <%-- Empty Cart State --%>
    <div style="text-align:center; padding:60px 0; color:var(--text-muted);">
        <i class="bi bi-cart-x" style="font-size:3.5rem;"></i>
        <p style="margin-top:12px; font-size:1rem;">Your cart is empty</p>
        <a href="<%= contextPath %>/medicines" class="btn btn-primary mt-3">
            <i class="bi bi-grid me-1"></i>Browse Medicines
        </a>
    </div>
    <% } else { %>

    <%-- Cart Table --%>
    <div style="background:#fff; border:1px solid var(--border); border-radius:10px;
                box-shadow:var(--card-shadow); overflow:hidden; margin-bottom:16px;">
        <table class="data-table cart-table">
            <thead>
                <tr>
                    <th>Medicine</th>
                    <th>Category</th>
                    <th style="text-align:right;">Unit Price</th>
                    <th style="text-align:center;">Quantity</th>
                    <th style="text-align:right;">Subtotal</th>
                    <th style="text-align:center;">Remove</th>
                </tr>
            </thead>
            <tbody>
                <% for (CartItem item : cart) { %>
                <tr>
                    <td>
                        <a href="<%= contextPath %>/medicine/detail?id=<%= item.getMedicine().getMedicineId() %>"
                           style="font-weight:600;">
                            <%= item.getMedicine().getMedicineName() %>
                        </a>
                        <% if (item.getMedicine().isPrescriptionRequired()) { %>
                        <span class="badge-rx ms-1">Rx</span>
                        <% } %>
                    </td>
                    <td style="color:var(--text-muted); font-size:0.85rem;">
                        <%= item.getMedicine().getCategoryName() %>
                    </td>
                    <td style="text-align:right;">
                        &#8377;<%= String.format("%.2f", item.getMedicine().getPrice()) %>
                    </td>
                    <td style="text-align:center;">
                        <form action="<%= contextPath %>/cart" method="post"
                              class="d-flex align-items-center justify-content-center gap-1">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="medicineId" value="<%= item.getMedicine().getMedicineId() %>">
                            <input type="number" name="quantity" value="<%= item.getQuantity() %>"
                                   min="1" max="<%= item.getMedicine().getStock() %>"
                                   class="cart-qty-input">
                            <button type="submit" class="btn btn-sm btn-outline-primary"
                                    title="Update quantity">
                                <i class="bi bi-arrow-clockwise"></i>
                            </button>
                        </form>
                    </td>
                    <td style="text-align:right; font-weight:600;">
                        &#8377;<%= String.format("%.2f", item.getSubtotal()) %>
                    </td>
                    <td style="text-align:center;">
                        <form action="<%= contextPath %>/cart" method="post">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="medicineId" value="<%= item.getMedicine().getMedicineId() %>">
                            <button type="submit" class="btn btn-sm btn-danger" title="Remove item"
                                    onclick="return confirm('Remove this item from cart?')">
                                <i class="bi bi-trash"></i>
                            </button>
                        </form>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>

    <%-- Order Total + Actions --%>
    <div style="display:flex; justify-content:space-between; align-items:center;
                flex-wrap:wrap; gap:16px; background:#fff; border:1px solid var(--border);
                border-radius:10px; padding:20px; box-shadow:var(--card-shadow);">

        <div>
            <a href="<%= contextPath %>/medicines" class="btn btn-outline-primary me-2">
                <i class="bi bi-arrow-left me-1"></i>Continue Shopping
            </a>
            <form action="<%= contextPath %>/cart" method="post" class="d-inline"
                  onsubmit="return confirm('Clear your entire cart?')">
                <input type="hidden" name="action" value="clear">
                <button type="submit" class="btn btn-outline-primary"
                        style="border-color:var(--danger); color:var(--danger);">
                    <i class="bi bi-trash me-1"></i>Clear Cart
                </button>
            </form>
        </div>

        <div style="text-align:right;">
            <div style="color:var(--text-muted); font-size:0.85rem; margin-bottom:4px;">
                Total (<%= cart.size() %> item<%= cart.size() != 1 ? "s" : "" %>)
            </div>
            <div style="font-size:1.6rem; font-weight:700; color:var(--primary);">
                &#8377;<%= String.format("%.2f", total) %>
            </div>
            <a href="<%= contextPath %>/checkout" class="btn btn-primary mt-2">
                Proceed to Checkout <i class="bi bi-arrow-right ms-1"></i>
            </a>
        </div>

    </div>
    <% } %>

</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
