package com.apollocare.service;

import com.apollocare.dao.CategoryDAO;
import com.apollocare.dao.MedicineDAO;
import com.apollocare.model.Category;
import com.apollocare.model.Medicine;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for medicine catalogue management.
 *
 * Pagination logic lives here (calculating offsets and total pages)
 * so that neither Servlets nor DAOs need to know about it.
 */
public class MedicineService {

    private static final int PAGE_SIZE = 10;   // medicines per page

    private final MedicineDAO  medicineDAO  = new MedicineDAO();
    private final CategoryDAO  categoryDAO  = new CategoryDAO();

    // ---------------------------------------------------------------
    // CUSTOMER-FACING
    // ---------------------------------------------------------------

    /**
     * Returns one page of ACTIVE medicines for the customer catalogue.
     *
     * @param keyword  search term (null = no filter)
     * @param page     1-based page number
     */
    public List<Medicine> getMedicines(String keyword, int page) throws SQLException {
        int offset = (page - 1) * PAGE_SIZE;
        return medicineDAO.findActive(keyword, offset, PAGE_SIZE);
    }

    /**
     * Returns the total number of pages for the customer catalogue.
     */
    public int getTotalPages(String keyword) throws SQLException {
        int total = medicineDAO.countActive(keyword);
        return (int) Math.ceil((double) total / PAGE_SIZE);
    }

    /**
     * Returns a single ACTIVE medicine by ID.
     * Returns null if not found or if the medicine is inactive.
     */
    public Medicine getMedicineById(int medicineId) throws SQLException {
        Medicine medicine = medicineDAO.findById(medicineId);
        if (medicine == null || "INACTIVE".equals(medicine.getStatus())) {
            return null;
        }
        return medicine;
    }

    // ---------------------------------------------------------------
    // ADMIN-FACING
    // ---------------------------------------------------------------

    /**
     * Returns one page of ALL medicines for admin management.
     */
    public List<Medicine> getAdminMedicines(String keyword, int page) throws SQLException {
        int offset = (page - 1) * PAGE_SIZE;
        return medicineDAO.findAll(keyword, offset, PAGE_SIZE);
    }

    /**
     * Returns total pages for admin medicine listing.
     */
    public int getAdminTotalPages(String keyword) throws SQLException {
        int total = medicineDAO.countAll(keyword);
        return (int) Math.ceil((double) total / PAGE_SIZE);
    }

    /**
     * Returns a medicine by ID regardless of status (for edit form).
     */
    public Medicine getMedicineByIdForAdmin(int medicineId) throws SQLException {
        return medicineDAO.findById(medicineId);
    }

    /**
     * Adds a new medicine to the catalogue.
     */
    public void addMedicine(Medicine medicine) throws SQLException {
        medicineDAO.save(medicine);
    }

    /**
     * Updates an existing medicine.
     */
    public void updateMedicine(Medicine medicine) throws SQLException {
        medicineDAO.update(medicine);
    }

    /**
     * Soft-deletes a medicine (sets status = INACTIVE).
     */
    public void deactivateMedicine(int medicineId) throws SQLException {
        medicineDAO.deactivate(medicineId);
    }

    /**
     * Returns all categories for populating dropdown menus.
     */
    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    /**
     * Returns total active medicine count for admin dashboard.
     */
    public int getMedicineCount() throws SQLException {
        return medicineDAO.countAll(null);
    }
}
