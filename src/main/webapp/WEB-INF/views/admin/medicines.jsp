<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.Medicine, java.util.List" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    List<Medicine> medicines  = (List<Medicine>) request.getAttribute("medicines");
    String         keyword    = (String) request.getAttribute("keyword");
    int            currentPage = (int) request.getAttribute("currentPage");
    int            totalPages  = (int) request.getAttribute("totalPages");
    String         successMsg  = (String) request.getAttribute("successMsg");
    String         errorMsg    = (String) request.getAttribute("errorMsg");
    if (keyword == null) keyword = "";
%>

<div class="admin-layout">

    <%-- Admin Sidebar --%>
    <nav class="admin-sidebar">
        <div class="admin-sidebar-title">Main Menu</div>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/dashboard">
            <i class="bi bi-speedometer2"></i>Dashboard
        </a>
        <a class="admin-nav-link active" href="<%= contextPath %>/admin/medicines">
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
        <a class="admin-nav-link" href="<%= contextPath %>/logout" style="color:var(--danger);">
            <i class="bi bi-box-arrow-right"></i>Logout
        </a>
    </nav>

    <div class="admin-content">

        <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:12px; margin-bottom:16px;">
            <h1 class="page-title mb-0">
                <i class="bi bi-capsule me-2 text-primary"></i>Medicines
            </h1>
            <a href="<%= contextPath %>/admin/medicine/add" class="btn btn-primary">
                <i class="bi bi-plus-circle me-1"></i>Add Medicine
            </a>
        </div>

        <%-- Flash messages from redirects --%>
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

        <%-- Search --%>
        <form class="search-bar" action="<%= contextPath %>/admin/medicines" method="get">
            <input type="text" name="keyword" class="form-control"
                   placeholder="Search medicines..." value="<%= keyword %>">
            <button type="submit" class="btn btn-outline-primary">
                <i class="bi bi-search"></i>
            </button>
            <% if (!keyword.isEmpty()) { %>
            <a href="<%= contextPath %>/admin/medicines" class="btn btn-outline-primary">
                <i class="bi bi-x"></i> Clear
            </a>
            <% } %>
        </form>

        <%-- Medicine Table --%>
        <% if (medicines == null || medicines.isEmpty()) { %>
        <p style="color:var(--text-muted); text-align:center; padding:40px 0;">
            No medicines found<%= !keyword.isEmpty() ? " for \"" + keyword + "\"" : "" %>.
        </p>
        <% } else { %>

        <div style="overflow-x:auto;">
        <table class="data-table">
            <thead>
                <tr>
                    <th>#ID</th>
                    <th>Name</th>
                    <th>Category</th>
                    <th style="text-align:right;">Price</th>
                    <th style="text-align:right;">Stock</th>
                    <th style="text-align:center;">Rx</th>
                    <th style="text-align:center;">Status</th>
                    <th style="text-align:center;">Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for (Medicine m : medicines) { %>
                <tr>
                    <td style="color:var(--text-muted);">#<%= m.getMedicineId() %></td>
                    <td>
                        <div style="font-weight:600; max-width:180px;"><%= m.getMedicineName() %></div>
                        <% if (m.getImage() != null && !m.getImage().isEmpty()) { %>
                        <small style="color:var(--text-muted);">
                            <i class="bi bi-image"></i>
                        </small>
                        <% } %>
                    </td>
                    <td style="color:var(--text-muted); font-size:0.85rem;"><%= m.getCategoryName() %></td>
                    <td style="text-align:right;">&#8377;<%= String.format("%.2f", m.getPrice()) %></td>
                    <td style="text-align:right;">
                        <% if (m.getStock() <= 0) { %>
                        <span class="stock-low"><strong>0</strong></span>
                        <% } else if (m.getStock() <= 10) { %>
                        <span class="stock-low"><%= m.getStock() %></span>
                        <% } else { %>
                        <%= m.getStock() %>
                        <% } %>
                    </td>
                    <td style="text-align:center;">
                        <% if (m.isPrescriptionRequired()) { %>
                        <span class="badge-rx">Yes</span>
                        <% } else { %>
                        <span style="color:var(--text-muted);">—</span>
                        <% } %>
                    </td>
                    <td style="text-align:center;">
                        <span class="status-badge status-<%= m.getStatus() %>"><%= m.getStatus() %></span>
                    </td>
                    <td style="text-align:center;">
                        <div style="display:flex; gap:6px; justify-content:center;">
                            <a href="<%= contextPath %>/admin/medicine/edit?id=<%= m.getMedicineId() %>"
                               class="btn btn-sm btn-outline-primary" title="Edit">
                                <i class="bi bi-pencil"></i>
                            </a>
                            <% if ("ACTIVE".equals(m.getStatus())) { %>
                            <form action="<%= contextPath %>/admin/medicine/delete" method="post"
                                  onsubmit="return confirm('Deactivate &quot;<%= m.getMedicineName() %>&quot;?')">
                                <input type="hidden" name="id" value="<%= m.getMedicineId() %>">
                                <button type="submit" class="btn btn-sm btn-danger" title="Deactivate">
                                    <i class="bi bi-slash-circle"></i>
                                </button>
                            </form>
                            <% } %>
                        </div>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
        </div>

        <%-- Pagination --%>
        <% if (totalPages > 1) { %>
        <div class="pagination-container">
            <a class="page-link <%= currentPage <= 1 ? "disabled" : "" %>"
               href="<%= contextPath %>/admin/medicines?page=<%= currentPage-1 %>&keyword=<%= keyword %>">
                <i class="bi bi-chevron-left"></i>
            </a>
            <% for (int p = 1; p <= totalPages; p++) { %>
            <a class="page-link <%= p == currentPage ? "active" : "" %>"
               href="<%= contextPath %>/admin/medicines?page=<%= p %>&keyword=<%= keyword %>">
                <%= p %>
            </a>
            <% } %>
            <a class="page-link <%= currentPage >= totalPages ? "disabled" : "" %>"
               href="<%= contextPath %>/admin/medicines?page=<%= currentPage+1 %>&keyword=<%= keyword %>">
                <i class="bi bi-chevron-right"></i>
            </a>
        </div>
        <% } %>

        <% } %>

    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
