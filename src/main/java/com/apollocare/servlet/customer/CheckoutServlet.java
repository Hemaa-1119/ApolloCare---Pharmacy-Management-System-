package com.apollocare.servlet.customer;

import com.apollocare.model.CartItem;
import com.apollocare.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Displays the checkout page (order review before placing).
 *
 * GET /checkout → show checkout form with cart items and delivery address
 *
 * This servlet is READ-ONLY (GET only).
 * The actual order placement is handled by PlaceOrderServlet (POST /order/place).
 *
 * If the cart is empty, redirect back to the cart page.
 */
public class CheckoutServlet extends HttpServlet {

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        List<CartItem> cart = (session != null)
            ? (List<CartItem>) session.getAttribute("cart")
            : null;

        // Cannot checkout with an empty cart
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?error=empty");
            return;
        }

        // Calculate total to display in checkout summary
        double total = 0;
        for (CartItem item : cart) total += item.getSubtotal();

        // Pre-fill delivery address from the logged-in user's profile
        User user = (User) session.getAttribute("user");

        request.setAttribute("cart",  cart);
        request.setAttribute("total", total);
        request.setAttribute("user",  user);

        request.getRequestDispatcher("/WEB-INF/views/customer/checkout.jsp")
               .forward(request, response);
    }
}
