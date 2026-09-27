package com.apollocare.service;

import com.apollocare.dao.MedicineDAO;
import com.apollocare.dao.OrderDAO;
import com.apollocare.model.CartItem;
import com.apollocare.model.Medicine;
import com.apollocare.model.Order;
import com.apollocare.model.User;
import com.apollocare.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Business logic for order placement and management.
 *
 * placeOrder() is the most important method in this class.
 * It demonstrates a complete JDBC transaction:
 *   1. setAutoCommit(false)   → begin transaction
 *   2. Insert order            → orderDAO.createOrder()
 *   3. Insert order items      → orderDAO.createOrderItems()
 *   4. Decrement stock         → orderDAO.updateStock()
 *   5. commit()                → all three succeed together
 *   6. rollback() on any error → nothing partial is saved
 *
 * The same Connection is passed to all three DAO methods so they
 * all participate in the same transaction.
 */
public class OrderService {

    private final OrderDAO    orderDAO    = new OrderDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();

    // Valid status transitions for admin order management
    private static final List<String> VALID_STATUSES =
        Arrays.asList("PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED");

    /**
     * Places a new order in a single JDBC transaction.
     *
     * @param user            the authenticated customer
     * @param cart            the current cart from HttpSession
     * @param deliveryAddress the address entered at checkout
     * @return the generated order_id on success
     * @throws Exception with a user-friendly message on failure
     */
    public int placeOrder(User user, List<CartItem> cart, String deliveryAddress)
            throws Exception {

        // --- Business validation (before touching the database) ---
        if (cart == null || cart.isEmpty()) {
            throw new Exception("Your cart is empty. Please add items before placing an order.");
        }
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            throw new Exception("Delivery address is required.");
        }

        // --- Validate stock availability for each cart item ---
        for (CartItem item : cart) {
            Medicine current = medicineDAO.findById(item.getMedicine().getMedicineId());
            if (current == null || "INACTIVE".equals(current.getStatus())) {
                throw new Exception("'" + item.getMedicine().getMedicineName() +
                                    "' is no longer available.");
            }
            if (current.getStock() < item.getQuantity()) {
                throw new Exception("Insufficient stock for '" + current.getMedicineName() +
                                    "'. Available: " + current.getStock());
            }
        }

        // --- Calculate order total ---
        double total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
        }

        // --- Build the order object ---
        Order order = new Order();
        order.setUserId(user.getUserId());
        order.setTotalAmount(total);
        order.setDeliveryAddress(deliveryAddress.trim());
        order.setStatus("PLACED");

        // --- JDBC Transaction ---
        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);  // Begin transaction

            try {
                // Step 1: Insert the order row → get back the generated orderId
                int orderId = orderDAO.createOrder(conn, order);

                // Step 2: Insert one order_items row per cart item
                orderDAO.createOrderItems(conn, orderId, cart);

                // Step 3: Decrement stock for each medicine
                orderDAO.updateStock(conn, cart);

                // All three steps succeeded — commit
                conn.commit();

                return orderId;

            } catch (Exception e) {
                // Any failure → roll back everything
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Returns all orders for a specific customer (order history).
     * Does NOT populate order items — lightweight list view.
     */
    public List<Order> getOrdersByUser(int userId) throws SQLException {
        return orderDAO.findOrdersByUser(userId);
    }

    /**
     * Returns a single order with its items populated.
     * Used for order detail views.
     */
    public Order getOrderById(int orderId) throws SQLException {
        return orderDAO.findOrderById(orderId);
    }

    /**
     * Returns all orders (admin view), newest first.
     */
    public List<Order> getAllOrders() throws SQLException {
        return orderDAO.findAllOrders();
    }

    /**
     * Updates the status of an order.
     * Validates that the new status is a recognised value.
     *
     * @throws Exception if the status value is invalid
     */
    public void updateOrderStatus(int orderId, String newStatus) throws Exception {
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new Exception("Invalid order status: " + newStatus);
        }
        orderDAO.updateOrderStatus(orderId, newStatus);
    }

    /**
     * Returns total order count for admin dashboard statistics.
     */
    public int getOrderCount() throws SQLException {
        return orderDAO.countAllOrders();
    }
}
