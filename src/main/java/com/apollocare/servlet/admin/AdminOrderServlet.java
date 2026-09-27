package com.apollocare.servlet.admin;

import com.apollocare.model.Order;
import com.apollocare.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Displays all orders for admin management.
 *
 * GET /admin/orders          → list all orders
 * GET /admin/orders?id=5     → show detail for order 5
 */
public class AdminOrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String orderIdParam = request.getParameter("id");

        try {
            if (orderIdParam != null && !orderIdParam.isEmpty()) {
                // Show order detail
                int orderId  = Integer.parseInt(orderIdParam);
                Order order  = orderService.getOrderById(orderId);

                if (order == null) {
                    request.setAttribute("errorMessage", "Order #" + orderId + " not found.");
                    request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                           .forward(request, response);
                    return;
                }

                request.setAttribute("order", order);
                request.getRequestDispatcher("/WEB-INF/views/admin/order-detail.jsp")
                       .forward(request, response);

            } else {
                // Show full orders list
                List<Order> orders = orderService.getAllOrders();
                request.setAttribute("orders", orders);

                // Flash messages from redirects
                request.setAttribute("successMsg", request.getParameter("success"));
                request.setAttribute("errorMsg",   request.getParameter("error"));

                request.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp")
                       .forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load orders.");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                   .forward(request, response);
        }
    }
}
