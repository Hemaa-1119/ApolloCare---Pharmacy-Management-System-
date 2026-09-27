<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" %>
<%
    // Determine the error message to display
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage == null) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        if (statusCode != null) {
            if (statusCode == 404)      errorMessage = "The page you are looking for does not exist.";
            else if (statusCode == 403) errorMessage = "You are not authorized to access this page.";
            else if (statusCode == 500) errorMessage = "An internal server error occurred. Please try again later.";
            else                        errorMessage = "Something went wrong. Please try again.";
        } else {
            errorMessage = "An unexpected error occurred.";
        }
    }
%>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="page-container" style="text-align:center; padding-top:80px;">
    <div style="font-size:4rem; margin-bottom:16px;">
        <i class="bi bi-exclamation-triangle-fill text-warning"></i>
    </div>
    <h1 style="font-size:1.8rem; font-weight:700; margin-bottom:12px; color:var(--text-main);">
        Oops! Something went wrong
    </h1>
    <p style="color:var(--text-muted); max-width:400px; margin:0 auto 24px;">
        <%= errorMessage %>
    </p>
    <div style="display:flex; gap:12px; justify-content:center;">
        <a href="<%= request.getContextPath() %>/medicines" class="btn btn-primary">
            <i class="bi bi-house me-1"></i>Back to Medicines
        </a>
        <a href="javascript:history.back()" class="btn btn-outline-primary">
            <i class="bi bi-arrow-left me-1"></i>Go Back
        </a>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
