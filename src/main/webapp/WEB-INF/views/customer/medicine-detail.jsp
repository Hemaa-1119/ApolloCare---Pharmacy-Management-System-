<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.Medicine" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    Medicine m = (Medicine) request.getAttribute("medicine");
%>

<div class="page-container" style="max-width:900px;">

    <a href="<%= contextPath %>/medicines" class="btn btn-outline-primary btn-sm mb-3">
        <i class="bi bi-arrow-left me-1"></i>Back to Medicines
    </a>

    <%-- Flash messages --%>
    <% if ("stock".equals(request.getParameter("error"))) { %>
    <div class="alert alert-danger">
        <i class="bi bi-exclamation-circle me-1"></i>
        Insufficient stock for the requested quantity.
    </div>
    <% } %>

    <div style="background:#fff; border:1px solid var(--border); border-radius:12px;
                box-shadow:var(--card-shadow); overflow:hidden;">
        <div style="display:flex; flex-wrap:wrap;">

            <%-- Image Panel --%>
            <div style="width:300px; flex-shrink:0; background:#f8fafc;
                        display:flex; align-items:center; justify-content:center; min-height:280px;">
                <% if (m.getImage() != null && !m.getImage().isEmpty()) { %>
                <img src="<%= contextPath %>/images/medicines/<%= m.getImage() %>"
                     alt="<%= m.getMedicineName() %>"
                     style="width:100%; height:280px; object-fit:cover;">
                <% } else { %>
                <div style="font-size:5rem; color:#cbd5e1;">
                    <i class="bi bi-capsule"></i>
                </div>
                <% } %>
            </div>

            <%-- Details Panel --%>
            <div style="flex:1; padding:28px; min-width:260px;">

                <div style="font-size:0.82rem; color:var(--text-muted); margin-bottom:6px;">
                    <i class="bi bi-tag me-1"></i><%= m.getCategoryName() %>
                </div>

                <h1 style="font-size:1.5rem; font-weight:700; color:var(--text-main); margin-bottom:8px;">
                    <%= m.getMedicineName() %>
                </h1>

                <div style="font-size:1.6rem; font-weight:700; color:var(--primary); margin-bottom:16px;">
                    &#8377;<%= String.format("%.2f", m.getPrice()) %>
                </div>

                <p style="color:var(--text-muted); font-size:0.9rem; line-height:1.7; margin-bottom:16px;">
                    <%= m.getDescription() != null ? m.getDescription() : "No description available." %>
                </p>

                <div style="display:flex; gap:16px; flex-wrap:wrap; margin-bottom:20px;">
                    <div>
                        <div style="font-size:0.75rem; font-weight:600; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.5px;">
                            Prescription
                        </div>
                        <div style="margin-top:4px;">
                            <% if (m.isPrescriptionRequired()) { %>
                            <span class="badge-rx"><i class="bi bi-prescription2 me-1"></i>Required</span>
                            <% } else { %>
                            <span class="badge-otc">Not Required</span>
                            <% } %>
                        </div>
                    </div>
                    <div>
                        <div style="font-size:0.75rem; font-weight:600; color:var(--text-muted); text-transform:uppercase; letter-spacing:0.5px;">
                            Availability
                        </div>
                        <div style="margin-top:4px;">
                            <% if (m.getStock() <= 0) { %>
                            <span class="stock-low"><i class="bi bi-x-circle me-1"></i>Out of stock</span>
                            <% } else if (m.getStock() <= 10) { %>
                            <span class="stock-low"><i class="bi bi-exclamation-circle me-1"></i>Only <%= m.getStock() %> left</span>
                            <% } else { %>
                            <span class="stock-ok"><i class="bi bi-check-circle me-1"></i>In stock (<%= m.getStock() %> units)</span>
                            <% } %>
                        </div>
                    </div>
                </div>

                <%-- Add to Cart Form --%>
                <% if (m.getStock() > 0) { %>
                <form action="<%= contextPath %>/cart" method="post" class="d-flex gap-2 align-items-center">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="medicineId" value="<%= m.getMedicineId() %>">
                    <div style="display:flex; align-items:center; gap:8px;">
                        <label style="font-size:0.88rem; font-weight:500;">Quantity:</label>
                        <input type="number" name="quantity" value="1" min="1"
                               max="<%= m.getStock() %>" class="cart-qty-input">
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-cart-plus me-1"></i>Add to Cart
                    </button>
                </form>
                <% } else { %>
                <button class="btn" style="background:#f1f5f9;color:var(--text-muted);" disabled>
                    <i class="bi bi-x-circle me-1"></i>Out of Stock
                </button>
                <% } %>

            </div>
        </div>
    </div>

</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
