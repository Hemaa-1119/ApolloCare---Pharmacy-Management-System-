package com.apollocare.dao;

import com.apollocare.model.CartItem;
import com.apollocare.model.Order;
import com.apollocare.model.OrderItem;
import com.apollocare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for orders and order_items tables.
 *
 * IMPORTANT — Transaction methods accept an existing Connection:
 * createOrder(), createOrderItems(), updateStock() all take a Connection
 * parameter so that OrderService can manage the JDBC transaction from one
 * central place. These methods do NOT open their own connection.
 *
 * Non-transactional read methods open their own connection as usual.
 */
public class OrderDAO {

    // ---------------------------------------------------------------
    // TRANSACTIONAL WRITE METHODS  (receive a Connection from OrderService)
    // ---------------------------------------------------------------

    /**
     * Inserts a new order record and returns the generated order_id.
     * Called inside a transaction managed by OrderService.
     */
    public int createOrder(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, total_amount, status, delivery_address) " +
                     "VALUES (?, ?, 'PLACED', ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,    order.getUserId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setString(3, order.getDeliveryAddress());

            ps.executeUpdate();

            // Retrieve the auto-generated order_id
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Creating order failed — no generated key returned.");
            }
        }
    }

    /**
     * Inserts one row into order_items for each item in the cart.
     * price is taken from the Medicine object — snapshot at order time.
     */
    public void createOrderItems(Connection conn, int orderId, List<CartItem> cart)
            throws SQLException {

        String sql = "INSERT INTO order_items (order_id, medicine_id, quantity, price) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CartItem item : cart) {
                ps.setInt(1,    orderId);
                ps.setInt(2,    item.getMedicine().getMedicineId());
                ps.setInt(3,    item.getQuantity());
                ps.setDouble(4, item.getMedicine().getPrice());
                ps.addBatch();
            }
            ps.executeBatch();  // Send all inserts in one network round-trip
        }
    }

    /**
     * Decrements stock for each medicine in the cart.
     * Throws SQLException if stock would go negative (safety check via SQL).
     */
    public void updateStock(Connection conn, List<CartItem> cart) throws SQLException {
        String sql = "UPDATE medicines SET stock = stock - ? WHERE medicine_id = ? AND stock >= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CartItem item : cart) {
                ps.setInt(1, item.getQuantity());
                ps.setInt(2, item.getMedicine().getMedicineId());
                ps.setInt(3, item.getQuantity());  // WHERE stock >= quantity
                ps.addBatch();
            }

            int[] results = ps.executeBatch();

            // If any row's affected count is 0, the WHERE stock >= qty condition failed
            for (int i = 0; i < results.length; i++) {
                if (results[i] == 0) {
                    CartItem item = cart.get(i);
                    throw new SQLException(
                        "Insufficient stock for: " + item.getMedicine().getMedicineName());
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // READ METHODS  (open their own connection)
    // ---------------------------------------------------------------

    /**
     * Returns all orders placed by a specific customer, newest first.
     * Does NOT populate order items — use findOrderById() for that.
     */
    public List<Order> findOrdersByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) orders.add(mapOrderRow(rs));
            }
        }
        return orders;
    }

    /**
     * Returns all orders (admin view), newest first.
     * Joins with users table to populate userName.
     */
    public List<Order> findAllOrders() throws SQLException {
        String sql = "SELECT o.*, u.name AS user_name " +
                     "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                     "ORDER BY o.order_date DESC";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = mapOrderRow(rs);
                order.setUserName(rs.getString("user_name"));
                orders.add(order);
            }
        }
        return orders;
    }

    /**
     * Finds a single order with its items.
     * Populates the items list by calling findOrderItemsByOrderId().
     * Returns null if not found.
     */
    public Order findOrderById(int orderId) throws SQLException {
        String sql = "SELECT o.*, u.name AS user_name " +
                     "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                     "WHERE o.order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setUserName(rs.getString("user_name"));
                    order.setItems(findOrderItemsByOrderId(orderId));  // populate items
                    return order;
                }
            }
        }
        return null;
    }

    /**
     * Returns all items for a given order.
     * Joins with medicines to populate medicineName.
     */
    public List<OrderItem> findOrderItemsByOrderId(int orderId) throws SQLException {
        String sql = "SELECT oi.*, m.medicine_name " +
                     "FROM order_items oi JOIN medicines m ON oi.medicine_id = m.medicine_id " +
                     "WHERE oi.order_id = ?";
        List<OrderItem> items = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setMedicineId(rs.getInt("medicine_id"));
                    item.setMedicineName(rs.getString("medicine_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("price"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * Updates the status of an order.
     * Called by admin via UpdateOrderStatusServlet.
     */
    public void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2,    orderId);
            ps.executeUpdate();
        }
    }

    /**
     * Returns the total order count. Used by admin dashboard statistics.
     */
    public int countAllOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ---------------------------------------------------------------
    // PRIVATE HELPERS
    // ---------------------------------------------------------------

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setDeliveryAddress(rs.getString("delivery_address"));

        Timestamp ts = rs.getTimestamp("order_date");
        if (ts != null) order.setOrderDate(ts.toLocalDateTime());

        return order;
    }
}
