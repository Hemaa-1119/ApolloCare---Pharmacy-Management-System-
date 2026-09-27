package com.apollocare.servlet.customer;

import com.apollocare.model.CartItem;
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
 * Places a new order.
 *
 * POST /order/place → validate → run JDBC transaction → clear cart → redirect to confirmation
 *
 * This is the most important servlet to trace for the JDBC transaction demonstration.
 *
 * Flow:
 * checkout.jsp → POST /order/place → PlaceOrderServlet
 *   → OrderService.placeOrder()
 *     → conn.setAutoCommit(false)
 *     → OrderDAO.createOrder()
 *     → OrderDAO.createOrderItems()
 *     → OrderDAO.updateStock()
 *     → conn.commit()
 *   → session cart cleared
 *   → redirect to /order/history?success=orderId
 *
 * If any step fails: conn.rollback() → no partial data saved.
 */
public class PlaceOrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Retrieve user and cart from session
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        List<CartItem> cart = (session != null)
            ? (List<CartItem>) session.getAttribute("cart")
            : null;

        // Guard: must be logged in (filter should have caught this already)
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Guard: cart must not be empty
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?error=empty");
            return;
        }

        // Read the delivery address from the checkout form
        String deliveryAddress = request.getParameter("deliveryAddress");

        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            // Forward back to checkout with error
            request.setAttribute("cart",  cart);
            request.setAttribute("error", "Delivery address is required.");
            request.setAttribute("user",  user);
            double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
            request.setAttribute("total", total);
            request.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp")
                   .forward(request, response);
            return;
        }

        try {
            // Delegate to OrderService — JDBC transaction happens here
            int orderId = orderService.placeOrder(user, cart, deliveryAddress);

            // Order placed successfully — clear the session cart
            session.removeAttribute("cart");

            // PRG: redirect to order history with success indicator
            response.sendRedirect(request.getContextPath() +
                                  "/order/history?success=true&orderId=" + orderId);

        } catch (Exception e) {
            // Transaction was rolled back — show error on checkout page
            request.setAttribute("cart",  cart);
            request.setAttribute("error", e.getMessage());
            request.setAttribute("user",  user);
            double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
            request.setAttribute("total", total);
            request.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp")
                   .forward(request, response);
        }
    }
}
