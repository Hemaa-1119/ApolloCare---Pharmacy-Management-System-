package com.apollocare.model;

import java.io.Serializable;

/**
 * Represents a single item in the shopping cart.
 *
 * The cart itself is a List<CartItem> stored in HttpSession.
 * Implements Serializable so it can be distributed across
 * cluster nodes if the session is replicated (future-proofing).
 *
 * getSubtotal() is a computed property used in JSP and checkout logic.
 */
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Medicine medicine;
    private int      quantity;

    public CartItem(Medicine medicine, int quantity) {
        this.medicine = medicine;
        this.quantity = quantity;
    }

    public Medicine getMedicine() { 
    	return medicine; 
    	
    }
    
    
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }

    public int getQuantity()                   { return quantity; }
    public void setQuantity(int quantity)      { this.quantity = quantity; }

    /**
     * Computed subtotal for this cart line: price × quantity.
     * Used in JSP to display per-item total and in OrderService to
     * compute the order total.
     */
    public double getSubtotal() {
        return medicine.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return "CartItem{medicine=" + medicine.getMedicineName() +
               ", quantity=" + quantity + ", subtotal=" + getSubtotal() + "}";
    }
}
