package com.apollocare.servlet.admin;

import com.apollocare.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Updates the status of a specific order (admin only).
 *
 * POST /admin/order/status
 *   params: orderId, newStatus
 *
 * Valid status values: PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
 * (Validation is performed in OrderService)
 */
public class UpdateOrderStatusServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String orderIdStr = request.getParameter("orderId");
        String newStatus  = request.getParameter("newStatus");

        if (orderIdStr == null || newStatus == null) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=invalid+request");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            orderService.updateOrderStatus(orderId, newStatus);
            response.sendRedirect(request.getContextPath() +
                                  "/admin/orders?id=" + orderId + "&success=Status+updated");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/orders?error=invalid+order+id");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() +
                                  "/admin/orders?error=" + e.getMessage().replace(" ", "+"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/orders");
    }
}
