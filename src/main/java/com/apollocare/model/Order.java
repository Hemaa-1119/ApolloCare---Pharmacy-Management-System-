package com.apollocare.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a placed order.
 *
 * - items is populated only when fetching order details (not in the list view).
 * - userName is populated via JOIN in OrderDAO for the admin orders list.
 * - deliveryAddress is a snapshot of the address at time of order placement.
 */
public class Order {

    private int              orderId;
    private int              userId;
    private String           userName;        // populated via JOIN for admin view
    private double           totalAmount;
    private LocalDateTime    orderDate;
    private String           status;          // PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    private String           deliveryAddress;
    private List<OrderItem>  items;           // populated when viewing order details

    public Order() {}

    public int getOrderId()                           { return orderId; }
    public void setOrderId(int orderId)               { this.orderId = orderId; }

    public int getUserId()                            { return userId; }
    public void setUserId(int userId)                 { this.userId = userId; }

    public String getUserName()                       { return userName; }
    public void setUserName(String userName)          { this.userName = userName; }

    public double getTotalAmount()                    { return totalAmount; }
    public void setTotalAmount(double totalAmount)    { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate()               { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getStatus()                         { return status; }
    public void setStatus(String status)              { this.status = status; }

    public String getDeliveryAddress()                       { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress)   { this.deliveryAddress = deliveryAddress; }

    public List<OrderItem> getItems()                 { return items; }
    public void setItems(List<OrderItem> items)       { this.items = items; }

    @Override
    public String toString() {
        return "Order{orderId=" + orderId + ", userId=" + userId +
               ", totalAmount=" + totalAmount + ", status='" + status + "'}";
    }
}
