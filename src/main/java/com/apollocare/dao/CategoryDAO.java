package com.apollocare.dao;

import com.apollocare.model.Category;
import com.apollocare.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the categories table.
 * Simple read-only DAO — categories are seeded via schema.sql.
 */
public class CategoryDAO {

    /**
     * Returns all categories.
     * Used to populate the category dropdown on the Add/Edit Medicine form.
     */
    public List<Category> findAll() throws SQLException {
        String sql = "SELECT category_id, category_name FROM categories ORDER BY category_name";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryId(rs.getInt("category_id"));
                cat.setCategoryName(rs.getString("category_name"));
                categories.add(cat);
            }
        }
        return categories;
    }
}
