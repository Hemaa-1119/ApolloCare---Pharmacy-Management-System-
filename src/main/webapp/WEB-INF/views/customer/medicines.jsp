<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.Medicine, java.util.List" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    List<Medicine> medicines  = (List<Medicine>) request.getAttribute("medicines");
    String         keyword    = (String) request.getAttribute("keyword");
    int            currentPage = (int) request.getAttribute("currentPage");
    int            totalPages  = (int) request.getAttribute("totalPages");
    if (keyword == null) keyword = "";
%>

<div class="page-container">

    <!-- Page Header + Search -->
    <div style="display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:12px; margin-bottom:20px;">
        <h1 class="page-title mb-0">
            <i class="bi bi-capsule me-2 text-primary"></i>
            <%= keyword.isEmpty() ? "All Medicines" : "Results for \"" + keyword + "\"" %>
        </h1>

        <form class="search-bar" action="<%= contextPath %>/medicines" method="get"
              style="margin-bottom:0;">
            <input type="text" name="keyword" class="form-control"
                   placeholder="Search medicines..." value="<%= keyword %>">
            <button type="submit" class="btn btn-primary">
                <i class="bi bi-search"></i>
            </button>
            <% if (!keyword.isEmpty()) { %>
            <a href="<%= contextPath %>/medicines" class="btn btn-outline-primary">
                <i class="bi bi-x"></i>
            </a>
            <% } %>
        </form>
    </div>

    <%-- Flash messages --%>
    <% if ("unauthorized".equals(request.getParameter("error"))) { %>
    <div class="alert alert-danger">
        <i class="bi bi-shield-exclamation me-1"></i>
        You are not authorized to access the admin area.
    </div>
    <% } %>

    <% if ("true".equals(request.getParameter("added"))) { %>
    <div class="alert alert-success">
        <i class="bi bi-cart-check me-1"></i>
        Item added to cart!
    </div>
    <% } %>

    <%-- Medicine Grid --%>
    <% if (medicines == null || medicines.isEmpty()) { %>
    <div style="text-align:center; padding:60px 0; color:var(--text-muted);">
        <i class="bi bi-search" style="font-size:3rem;"></i>
        <p style="margin-top:12px;">
            <%= keyword.isEmpty() ? "No medicines available." : "No medicines found for \"" + keyword + "\"." %>
        </p>
        <% if (!keyword.isEmpty()) { %>
        <a href="<%= contextPath %>/medicines" class="btn btn-outline-primary btn-sm mt-2">
            View All Medicines
        </a>
        <% } %>
    </div>
    <% } else { %>

    <div class="row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-lg-5 g-3">
        <% for (Medicine m : medicines) { %>
        <div class="col">
            <div class="medicine-card">

                <%-- Medicine Image --%>
                <% if (m.getImage() != null && !m.getImage().isEmpty()) { %>
                <img src="<%= contextPath %>/images/medicines/<%= m.getImage() %>"
                     alt="<%= m.getMedicineName() %>"
                     class="medicine-card-img">
                <% } else { %>
                <div class="medicine-card-img-placeholder">
                    <i class="bi bi-capsule"></i>
                </div>
                <% } %>

                <div class="medicine-card-body">
                    <div class="medicine-card-category">
                        <i class="bi bi-tag me-1"></i><%= m.getCategoryName() %>
                    </div>
                    <div class="medicine-card-title"><%= m.getMedicineName() %></div>
                    <div class="medicine-card-price">&#8377;<%= String.format("%.2f", m.getPrice()) %></div>

                    <div style="display:flex; gap:6px; align-items:center; margin-bottom:8px; flex-wrap:wrap;">
                        <% if (m.isPrescriptionRequired()) { %>
                        <span class="badge-rx"><i class="bi bi-prescription2 me-1"></i>Rx</span>
                        <% } else { %>
                        <span class="badge-otc">OTC</span>
                        <% } %>
                        <% if (m.getStock() <= 0) { %>
                        <span class="stock-low">Out of stock</span>
                        <% } else if (m.getStock() <= 10) { %>
                        <span class="stock-low">Only <%= m.getStock() %> left</span>
                        <% } else { %>
                        <span class="stock-ok"><i class="bi bi-check-circle me-1"></i>In stock</span>
                        <% } %>
                    </div>

                    <a href="<%= contextPath %>/medicine/detail?id=<%= m.getMedicineId() %>"
                       class="btn btn-outline-primary btn-sm w-100">
                        View Details
                    </a>
                </div>

                <div class="medicine-card-footer">
                    <% if (m.getStock() > 0) { %>
                    <form action="<%= contextPath %>/cart" method="post" class="d-flex gap-1 w-100">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="medicineId" value="<%= m.getMedicineId() %>">
                        <input type="number" name="quantity" value="1" min="1"
                               max="<%= m.getStock() %>" class="cart-qty-input"
                               style="width:55px;">
                        <button type="submit" class="btn btn-primary btn-sm flex-grow-1">
                            <i class="bi bi-cart-plus"></i> Add
                        </button>
                    </form>
                    <% } else { %>
                    <button class="btn btn-sm w-100" style="background:#f1f5f9;color:var(--text-muted);" disabled>
                        Out of Stock
                    </button>
                    <% } %>
                </div>

            </div>
        </div>
        <% } %>
    </div>

    <%-- Pagination --%>
    <% if (totalPages > 1) { %>
    <div class="pagination-container">
        <a class="page-link <%= currentPage <= 1 ? "disabled" : "" %>"
           href="<%= contextPath %>/medicines?page=<%= currentPage-1 %>&keyword=<%= keyword %>">
            <i class="bi bi-chevron-left"></i> Prev
        </a>

        <% for (int p = 1; p <= totalPages; p++) { %>
        <a class="page-link <%= p == currentPage ? "active" : "" %>"
           href="<%= contextPath %>/medicines?page=<%= p %>&keyword=<%= keyword %>">
            <%= p %>
        </a>
        <% } %>

        <a class="page-link <%= currentPage >= totalPages ? "disabled" : "" %>"
           href="<%= contextPath %>/medicines?page=<%= currentPage+1 %>&keyword=<%= keyword %>">
            Next <i class="bi bi-chevron-right"></i>
        </a>
    </div>
    <p style="text-align:center; color:var(--text-muted); font-size:0.82rem; margin-top:8px;">
        Page <%= currentPage %> of <%= totalPages %>
    </p>
    <% } %>

    <% } %>

</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
