package com.apollocare.dao;

import com.apollocare.model.Medicine;
import com.apollocare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the medicines table.
 *
 * Key design decisions:
 * - Customer-facing methods only return ACTIVE medicines.
 * - Admin-facing methods return ALL medicines (including INACTIVE).
 * - Search uses LIKE with PreparedStatement — never string concatenation.
 * - Pagination uses LIMIT / OFFSET.
 * - categoryName is populated via a JOIN, not a second query.
 */
public class MedicineDAO {

    // ---------------------------------------------------------------
    // CUSTOMER-FACING (ACTIVE medicines only)
    // ---------------------------------------------------------------

    /**
     * Returns a page of ACTIVE medicines matching the optional keyword.
     * If keyword is null or empty, returns all active medicines.
     *
     * @param keyword  search term (can be null or blank)
     * @param offset   number of rows to skip  (= (page-1) * pageSize)
     * @param limit    number of rows to fetch (= pageSize)
     */
    public List<Medicine> findActive(String keyword, int offset, int limit) throws SQLException {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        String sql = "SELECT m.*, c.category_name " +
                     "FROM medicines m JOIN categories c ON m.category_id = c.category_id " +
                     "WHERE m.status = 'ACTIVE' " +
                     (hasKeyword ? "AND m.medicine_name LIKE ? " : "") +
                     "ORDER BY m.medicine_name LIMIT ? OFFSET ?";

        List<Medicine> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            if (hasKeyword) ps.setString(idx++, "%" + keyword.trim() + "%");
            ps.setInt(idx++, limit);
            ps.setInt(idx,   offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Returns the total count of ACTIVE medicines matching the optional keyword.
     * Used to calculate the total number of pages for pagination.
     */
    public int countActive(String keyword) throws SQLException {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        String sql = "SELECT COUNT(*) FROM medicines " +
                     "WHERE status = 'ACTIVE' " +
                     (hasKeyword ? "AND medicine_name LIKE ?" : "");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (hasKeyword) ps.setString(1, "%" + keyword.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Finds a single medicine by ID regardless of status.
     * Returns null if not found.
     */
    public Medicine findById(int medicineId) throws SQLException {
        String sql = "SELECT m.*, c.category_name " +
                     "FROM medicines m JOIN categories c ON m.category_id = c.category_id " +
                     "WHERE m.medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, medicineId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ---------------------------------------------------------------
    // ADMIN-FACING (ALL medicines, including INACTIVE)
    // ---------------------------------------------------------------

    /**
     * Returns a page of ALL medicines (for admin medicine management).
     */
    public List<Medicine> findAll(String keyword, int offset, int limit) throws SQLException {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        String sql = "SELECT m.*, c.category_name " +
                     "FROM medicines m JOIN categories c ON m.category_id = c.category_id " +
                     (hasKeyword ? "WHERE m.medicine_name LIKE ? " : "") +
                     "ORDER BY m.medicine_id DESC LIMIT ? OFFSET ?";

        List<Medicine> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            if (hasKeyword) ps.setString(idx++, "%" + keyword.trim() + "%");
            ps.setInt(idx++, limit);
            ps.setInt(idx,   offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Returns the total count of ALL medicines (for admin pagination).
     */
    public int countAll(String keyword) throws SQLException {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        String sql = "SELECT COUNT(*) FROM medicines " +
                     (hasKeyword ? "WHERE medicine_name LIKE ?" : "");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (hasKeyword) ps.setString(1, "%" + keyword.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // ---------------------------------------------------------------
    // CRUD
    // ---------------------------------------------------------------

    /**
     * Inserts a new medicine. Uses the auto-generated primary key.
     */
    public void save(Medicine medicine) throws SQLException {
        String sql = "INSERT INTO medicines " +
                     "(medicine_name, description, price, stock, category_id, prescription_required, image, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, medicine.getMedicineName());
            ps.setString(2, medicine.getDescription());
            ps.setDouble(3, medicine.getPrice());
            ps.setInt(4,    medicine.getStock());
            ps.setInt(5,    medicine.getCategoryId());
            ps.setBoolean(6, medicine.isPrescriptionRequired());
            ps.setString(7, medicine.getImage());

            ps.executeUpdate();
        }
    }

    /**
     * Updates an existing medicine's details.
     * image is only updated if a new filename is provided (non-null).
     */
    public void update(Medicine medicine) throws SQLException {
        // If a new image was uploaded, update it; otherwise keep the existing one
        boolean updateImage = medicine.getImage() != null && !medicine.getImage().isEmpty();

        String sql = "UPDATE medicines SET " +
                     "medicine_name = ?, description = ?, price = ?, stock = ?, " +
                     "category_id = ?, prescription_required = ?, status = ? " +
                     (updateImage ? ", image = ? " : "") +
                     "WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            ps.setString(idx++, medicine.getMedicineName());
            ps.setString(idx++, medicine.getDescription());
            ps.setDouble(idx++, medicine.getPrice());
            ps.setInt(idx++,    medicine.getStock());
            ps.setInt(idx++,    medicine.getCategoryId());
            ps.setBoolean(idx++, medicine.isPrescriptionRequired());
            ps.setString(idx++, medicine.getStatus());
            if (updateImage) ps.setString(idx++, medicine.getImage());
            ps.setInt(idx, medicine.getMedicineId());

            ps.executeUpdate();
        }
    }

    /**
     * Soft-deletes a medicine by setting its status to INACTIVE.
     * The medicine still appears in existing order history but not in the catalogue.
     */
    public void deactivate(int medicineId) throws SQLException {
        String sql = "UPDATE medicines SET status = 'INACTIVE' WHERE medicine_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, medicineId);
            ps.executeUpdate();
        }
    }

    // ---------------------------------------------------------------
    // PRIVATE HELPERS
    // ---------------------------------------------------------------

    /** Maps a ResultSet row to a Medicine object. */
    private Medicine mapRow(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setMedicineId(rs.getInt("medicine_id"));
        m.setMedicineName(rs.getString("medicine_name"));
        m.setDescription(rs.getString("description"));
        m.setPrice(rs.getDouble("price"));
        m.setStock(rs.getInt("stock"));
        m.setCategoryId(rs.getInt("category_id"));
        m.setCategoryName(rs.getString("category_name"));
        m.setPrescriptionRequired(rs.getBoolean("prescription_required"));
        m.setImage(rs.getString("image"));
        m.setStatus(rs.getString("status"));
        return m;
    }
}
