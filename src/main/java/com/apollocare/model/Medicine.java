package com.apollocare.model;

/**
 * Represents a medicine in the catalogue.
 *
 * categoryName is NOT a database column — it is populated by a JOIN
 * in MedicineDAO when fetching medicines with their category names.
 *
 * status: "ACTIVE" = available for purchase; "INACTIVE" = soft-deleted.
 */
public class Medicine {

    private int     medicineId;
    private String  medicineName;
    private String  description;
    private double  price;
    private int     stock;
    private int     categoryId;
    private String  categoryName;          // populated via JOIN in DAO
    private boolean prescriptionRequired;
    private String  image;                 // filename stored in /images/medicines/
    private String  status;               // "ACTIVE" or "INACTIVE"

    public Medicine() {}

    // Getters and Setters

    public int getMedicineId()                       { return medicineId; }
    public void setMedicineId(int medicineId)        { this.medicineId = medicineId; }

    public String getMedicineName()                  { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDescription()                   { return description; }
    public void setDescription(String description)   { this.description = description; }

    public double getPrice()                         { return price; }
    public void setPrice(double price)               { this.price = price; }

    public int getStock()                            { return stock; }
    public void setStock(int stock)                  { this.stock = stock; }

    public int getCategoryId()                       { return categoryId; }
    public void setCategoryId(int categoryId)        { this.categoryId = categoryId; }

    public String getCategoryName()                  { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public boolean isPrescriptionRequired()                          { return prescriptionRequired; }
    public void setPrescriptionRequired(boolean prescriptionRequired){ this.prescriptionRequired = prescriptionRequired; }

    public String getImage()                         { return image; }
    public void setImage(String image)               { this.image = image; }

    public String getStatus()                        { return status; }
    public void setStatus(String status)             { this.status = status; }

    /** Convenience method used in JSP to check availability. */
    public boolean isAvailable() {
        return "ACTIVE".equals(status) && stock > 0;
    }

    @Override
    public String toString() {
        return "Medicine{medicineId=" + medicineId + ", medicineName='" + medicineName +
               "', price=" + price + ", stock=" + stock + ", status='" + status + "'}";
    }
}
