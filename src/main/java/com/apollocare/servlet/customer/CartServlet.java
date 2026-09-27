package com.apollocare.servlet.customer;

import com.apollocare.model.CartItem;
import com.apollocare.model.Medicine;
import com.apollocare.service.MedicineService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the shopping cart stored in HttpSession.
 *
 * GET  /cart         → show the cart
 * POST /cart?action=add    → add a medicine to the cart
 * POST /cart?action=update → update quantity of an item
 * POST /cart?action=remove → remove an item from the cart
 * POST /cart?action=clear  → clear the entire cart
 *
 * The cart is stored in session as: List<CartItem>
 * Session key: "cart"
 *
 * Demonstrates: HttpSession, session-based state management,
 *               single-servlet multi-action pattern.
 */
public class CartServlet extends HttpServlet {

    private static final String SESSION_CART = "cart";
    private final MedicineService medicineService = new MedicineService();

    /** GET — show the current cart */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // getSession(false) — do NOT create a session just to show an empty cart
        List<CartItem> cart = getCart(request);
        request.setAttribute("cart", cart);
        request.setAttribute("total", calculateTotal(cart));

        request.getRequestDispatcher("/WEB-INF/views/customer/cart.jsp")
               .forward(request, response);
    }

    /** POST — handle cart actions */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action != null ? action : "") {
            case "add"    -> handleAdd(request, response);
            case "update" -> handleUpdate(request, response);
            case "remove" -> handleRemove(request, response);
            case "clear"  -> handleClear(request, response);
            default       -> response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    // ---------------------------------------------------------------

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String medicineIdParam = request.getParameter("medicineId");
        String quantityParam   = request.getParameter("quantity");

        int medicineId, quantity;
        try {
            medicineId = Integer.parseInt(medicineIdParam);
            quantity   = Integer.parseInt(quantityParam);
            if (quantity < 1) quantity = 1;
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/medicines?error=invalid");
            return;
        }

        try {
            Medicine medicine = medicineService.getMedicineById(medicineId);
            if (medicine == null) {
                response.sendRedirect(request.getContextPath() + "/medicines?error=notfound");
                return;
            }
            if (medicine.getStock() < quantity) {
                response.sendRedirect(request.getContextPath() +
                        "/medicine/detail?id=" + medicineId + "&error=stock");
                return;
            }

            // Add to cart in session
            HttpSession session = request.getSession(true);  // create session if needed
            List<CartItem> cart = getCartFromSession(session);

            // Check if this medicine is already in the cart → increase quantity
            boolean found = false;
            for (CartItem item : cart) {
                if (item.getMedicine().getMedicineId() == medicineId) {
                    int newQty = item.getQuantity() + quantity;
                    if (newQty > medicine.getStock()) newQty = medicine.getStock();
                    item.setQuantity(newQty);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cart.add(new CartItem(medicine, quantity));
            }

            session.setAttribute(SESSION_CART, cart);

            // PRG: redirect to cart (prevents duplicate POST on refresh)
            response.sendRedirect(request.getContextPath() + "/cart?added=true");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/medicines?error=server");
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int medicineId, quantity;
        try {
            medicineId = Integer.parseInt(request.getParameter("medicineId"));
            quantity   = Integer.parseInt(request.getParameter("quantity"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            List<CartItem> cart = getCartFromSession(session);
            for (CartItem item : cart) {
                if (item.getMedicine().getMedicineId() == medicineId) {
                    if (quantity <= 0) {
                        cart.remove(item);  // Remove if quantity set to 0 or less
                    } else {
                        item.setQuantity(Math.min(quantity, item.getMedicine().getStock()));
                    }
                    break;
                }
            }
            session.setAttribute(SESSION_CART, cart);
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleRemove(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int medicineId;
        try {
            medicineId = Integer.parseInt(request.getParameter("medicineId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            List<CartItem> cart = getCartFromSession(session);
            cart.removeIf(item -> item.getMedicine().getMedicineId() == medicineId);
            session.setAttribute(SESSION_CART, cart);
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleClear(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SESSION_CART);
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------

    /** Retrieves the cart from the session, or an empty list if none exists. */
    private List<CartItem> getCart(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return new ArrayList<>();
        return getCartFromSession(session);
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCartFromSession(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(SESSION_CART);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(SESSION_CART, cart);
        }
        return cart;
    }

    /** Calculates the total price of all items in the cart. */
    private double calculateTotal(List<CartItem> cart) {
        double total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
        }
        return total;
    }
}
