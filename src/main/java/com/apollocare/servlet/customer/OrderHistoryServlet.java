package com.apollocare.servlet.customer;

import com.apollocare.model.Order;
import com.apollocare.model.User;
import com.apollocare.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Displays the logged-in customer's order history.
 *
 * GET /order/history → list all orders for this customer
 *
 * Also handles order detail view:
 * GET /order/history?orderId=5 → show details of order 5
 */
public class OrderHistoryServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String orderIdParam = request.getParameter("orderId");

        try {
            if (orderIdParam != null && !orderIdParam.isEmpty()) {
                // Show detail for a specific order
                int orderId = Integer.parseInt(orderIdParam);
                Order order = orderService.getOrderById(orderId);

                // Security: ensure the order belongs to this customer
                if (order == null || order.getUserId() != user.getUserId()) {
                    request.setAttribute("errorMessage", "Order not found.");
                    request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                           .forward(request, response);
                    return;
                }

                request.setAttribute("order", order);
                request.getRequestDispatcher("/WEB-INF/views/customer/order-history.jsp")
                       .forward(request, response);

            } else {
                // Show the full order list
                List<Order> orders = orderService.getOrdersByUser(user.getUserId());
                request.setAttribute("orders", orders);
                request.getRequestDispatcher("/WEB-INF/views/customer/order-history.jsp")
                       .forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Unable to load your orders. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                   .forward(request, response);
        }
    }
}
