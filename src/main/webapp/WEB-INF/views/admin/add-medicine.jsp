<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.Category, java.util.List" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    String error = (String) request.getAttribute("error");
%>

<div class="admin-layout">

    <nav class="admin-sidebar">
        <div class="admin-sidebar-title">Main Menu</div>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/dashboard"><i class="bi bi-speedometer2"></i>Dashboard</a>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/medicines"><i class="bi bi-capsule"></i>Medicines</a>
        <a class="admin-nav-link active" href="<%= contextPath %>/admin/medicine/add"><i class="bi bi-plus-circle"></i>Add Medicine</a>
        <div class="admin-sidebar-title">Orders</div>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/orders"><i class="bi bi-bag"></i>All Orders</a>
        <div class="admin-sidebar-title">Account</div>
        <a class="admin-nav-link" href="<%= contextPath %>/medicines"><i class="bi bi-globe"></i>View Store</a>
        <a class="admin-nav-link" href="<%= contextPath %>/logout" style="color:var(--danger);"><i class="bi bi-box-arrow-right"></i>Logout</a>
    </nav>

    <div class="admin-content">

        <div style="display:flex; align-items:center; gap:12px; margin-bottom:20px;">
            <a href="<%= contextPath %>/admin/medicines" class="btn btn-outline-primary btn-sm">
                <i class="bi bi-arrow-left"></i>
            </a>
            <h1 class="page-title mb-0">
                <i class="bi bi-plus-circle me-2 text-primary"></i>Add Medicine
            </h1>
        </div>

        <% if (error != null) { %>
        <div class="alert alert-danger">
            <i class="bi bi-exclamation-circle me-1"></i><%= error %>
        </div>
        <% } %>

        <div class="form-card form-card-wide" style="max-width:680px; margin:0;">

            <form action="<%= contextPath %>/admin/medicine/add" method="post"
                  enctype="multipart/form-data"
                  data-validate-form="addMedicineForm" novalidate>

                <div class="row g-3">

                    <div class="col-md-8">
                        <div class="form-group">
                            <label class="form-label" for="medicineName">Medicine Name *</label>
                            <input type="text" id="medicineName" name="medicineName"
                                   class="form-control" required
                                   value="<%= request.getAttribute("medicineName") != null ? request.getAttribute("medicineName") : "" %>">
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="form-label" for="categoryId">Category *</label>
                            <select id="categoryId" name="categoryId" class="form-select" required>
                                <option value="">-- Select --</option>
                                <% if (categories != null) { for (Category cat : categories) { %>
                                <option value="<%= cat.getCategoryId() %>">
                                    <%= cat.getCategoryName() %>
                                </option>
                                <% } } %>
                            </select>
                        </div>
                    </div>

                    <div class="col-12">
                        <div class="form-group">
                            <label class="form-label" for="description">Description</label>
                            <textarea id="description" name="description" class="form-control" rows="3"
                                      placeholder="Brief description of the medicine..."><%= request.getAttribute("description") != null ? request.getAttribute("description") : "" %></textarea>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="form-label" for="price">Price (&#8377;) *</label>
                            <input type="number" id="price" name="price" class="form-control"
                                   step="0.01" min="0.01" required
                                   value="<%= request.getAttribute("price") != null ? request.getAttribute("price") : "" %>">
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="form-label" for="stock">Stock Quantity *</label>
                            <input type="number" id="stock" name="stock" class="form-control"
                                   min="0" required
                                   value="<%= request.getAttribute("stock") != null ? request.getAttribute("stock") : "" %>">
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="form-label" for="image">Medicine Image</label>
                            <input type="file" id="image" name="image" class="form-control"
                                   accept="image/*">
                            <small style="color:var(--text-muted); font-size:0.78rem;">
                                Max 5 MB. JPG, PNG, GIF.
                            </small>
                        </div>
                    </div>

                    <div class="col-12">
                        <div class="form-check">
                            <input type="checkbox" id="prescriptionRequired"
                                   name="prescriptionRequired" class="form-check-input">
                            <label class="form-check-label" for="prescriptionRequired">
                                Prescription Required
                            </label>
                        </div>
                    </div>

                    <div class="col-12 d-flex gap-2 mt-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-plus-circle me-1"></i>Add Medicine
                        </button>
                        <a href="<%= contextPath %>/admin/medicines" class="btn btn-outline-primary">
                            Cancel
                        </a>
                    </div>

                </div>
            </form>

        </div>

    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
