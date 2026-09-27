<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Welcome page — redirect based on login state
    jakarta.servlet.http.HttpSession s = request.getSession(false);
    if (s != null && s.getAttribute("user") != null) {
        com.apollocare.model.User u = (com.apollocare.model.User) s.getAttribute("user");
        if (u.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/medicines");
        }
    } else {
        response.sendRedirect(request.getContextPath() + "/medicines");
    }
%>
