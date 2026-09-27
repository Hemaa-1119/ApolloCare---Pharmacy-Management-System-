<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.apollocare.model.Medicine, com.apollocare.model.Category, java.util.List" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<%
    Medicine       medicine   = (Medicine)       request.getAttribute("medicine");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    String         error      = (String)         request.getAttribute("error");
%>

<div class="admin-layout">

    <nav class="admin-sidebar">
        <div class="admin-sidebar-title">Main Menu</div>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/dashboard"><i class="bi bi-speedometer2"></i>Dashboard</a>
        <a class="admin-nav-link active" href="<%= contextPath %>/admin/medicines"><i class="bi bi-capsule"></i>Medicines</a>
        <a class="admin-nav-link" href="<%= contextPath %>/admin/medicine/add"><i class="bi bi-plus-circle"></i>Add Medicine</a>
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
                <i class="bi bi-pencil me-2 text-primary"></i>Edit Medicine
            </h1>
        </div>

        <% if (error != null) { %>
        <div class="alert alert-danger">
            <i class="bi bi-exclamation-circle me-1"></i><%= error %>
        </div>
        <% } %>

        <div class="form-card form-card-wide" style="max-width:680px; margin:0;">

            <form action="<%= contextPath %>/admin/medicine/edit" method="post"
                  enctype="multipart/form-data"
                  data-validate-form="editMedicineForm" novalidate>

                <%-- Hidden ID --%>
                <input type="hidden" name="medicineId" value="<%= medicine.getMedicineId() %>">

                <div class="row g-3">

                    <div class="col-md-8">
                        <div class="form-group">
                            <label class="form-label" for="medicineName">Medicine Name *</label>
                            <input type="text" id="medicineName" name="medicineName"
                                   class="form-control" required
                                   value="<%= medicine.getMedicineName() %>">
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="form-label" for="categoryId">Category *</label>
                            <select id="categoryId" name="categoryId" class="form-select" required>
                                <option value="">-- Select --</option>
                                <% if (categories != null) { for (Category cat : categories) { %>
                                <option value="<%= cat.getCategoryId() %>"
                                    <%= cat.getCategoryId() == medicine.getCategoryId() ? "selected" : "" %>>
                                    <%= cat.getCategoryName() %>
                                </option>
                                <% } } %>
                            </select>
                        </div>
                    </div>

                    <div class="col-12">
                        <div class="form-group">
                            <label class="form-label" for="description">Description</label>
                            <textarea id="description" name="description" class="form-control" rows="3"><%= medicine.getDescription() != null ? medicine.getDescription() : "" %></textarea>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="form-label" for="price">Price (&#8377;) *</label>
                            <input type="number" id="price" name="price" class="form-control"
                                   step="0.01" min="0.01" required
                                   value="<%= medicine.getPrice() %>">
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="form-label" for="stock">Stock Quantity *</label>
                            <input type="number" id="stock" name="stock" class="form-control"
                                   min="0" required
                                   value="<%= medicine.getStock() %>">
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="form-group">
                            <label class="form-label" for="status">Status</label>
                            <select id="status" name="status" class="form-select">
                                <option value="ACTIVE"   <%= "ACTIVE".equals(medicine.getStatus())   ? "selected" : "" %>>Active</option>
                                <option value="INACTIVE" <%= "INACTIVE".equals(medicine.getStatus()) ? "selected" : "" %>>Inactive</option>
                            </select>
                        </div>
                    </div>

                    <div class="col-12">
                        <div class="form-group">
                            <label class="form-label" for="image">
                                Replace Image
                                <small style="color:var(--text-muted); font-weight:400;">
                                    (leave empty to keep existing)
                                </small>
                            </label>
                            <% if (medicine.getImage() != null && !medicine.getImage().isEmpty()) { %>
                            <div style="margin-bottom:8px;">
                                <img src="<%= contextPath %>/images/medicines/<%= medicine.getImage() %>"
                                     alt="Current image"
                                     style="height:80px; border-radius:6px; border:1px solid var(--border);">
                                <small style="color:var(--text-muted); display:block; margin-top:4px;">
                                    Current: <%= medicine.getImage() %>
                                </small>
                            </div>
                            <% } %>
                            <input type="file" id="image" name="image" class="form-control"
                                   accept="image/*">
                        </div>
                    </div>

                    <div class="col-12">
                        <div class="form-check">
                            <input type="checkbox" id="prescriptionRequired"
                                   name="prescriptionRequired" class="form-check-input"
                                   <%= medicine.isPrescriptionRequired() ? "checked" : "" %>>
                            <label class="form-check-label" for="prescriptionRequired">
                                Prescription Required
                            </label>
                        </div>
                    </div>

                    <div class="col-12 d-flex gap-2 mt-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-check-circle me-1"></i>Update Medicine
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
