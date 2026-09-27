package com.apollocare.model;

/**
 * Represents one medicine line within a placed order.
 *
 * price = the medicine's price AT THE TIME THE ORDER WAS PLACED.
 * This is captured as a snapshot so that future price changes
 * do not alter historical order data.
 *
 * medicineName is populated via JOIN in OrderDAO.
 */
public class OrderItem {

    private int    orderItemId;
    private int    orderId;
    private int    medicineId;
    private String medicineName;   // populated via JOIN
    private int    quantity;
    private double price;          // price at time of order

    public OrderItem() {}

    public int getOrderItemId()                      { return orderItemId; }
    public void setOrderItemId(int orderItemId)      { this.orderItemId = orderItemId; }

    public int getOrderId()                          { return orderId; }
    public void setOrderId(int orderId)              { this.orderId = orderId; }

    public int getMedicineId()                       { return medicineId; }
    public void setMedicineId(int medicineId)        { this.medicineId = medicineId; }

    public String getMedicineName()                  { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public int getQuantity()                         { return quantity; }
    public void setQuantity(int quantity)            { this.quantity = quantity; }

    public double getPrice()                         { return price; }
    public void setPrice(double price)               { this.price = price; }

    /** Subtotal for this line item. */
    public double getLineTotal() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{orderItemId=" + orderItemId + ", medicineName='" + medicineName +
               "', quantity=" + quantity + ", price=" + price + "}";
    }
}
