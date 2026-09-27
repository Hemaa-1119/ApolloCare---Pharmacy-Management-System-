package com.apollocare.servlet.admin;

import com.apollocare.model.Category;
import com.apollocare.model.Medicine;
import com.apollocare.service.MedicineService;
import com.apollocare.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Handles editing an existing medicine (admin).
 *
 * GET  /admin/medicine/edit?id=5 → show edit form pre-populated with current data
 * POST /admin/medicine/edit      → validate, update medicine, redirect to medicine list
 */
@MultipartConfig(
    maxFileSize    = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
public class EditMedicineServlet extends HttpServlet {

    private final MedicineService medicineService = new MedicineService();

    /** GET — show the edit form pre-filled with existing medicine data */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/medicines");
            return;
        }

        try {
            int medicineId        = Integer.parseInt(idParam);
            Medicine medicine     = medicineService.getMedicineByIdForAdmin(medicineId);
            List<Category> cats   = medicineService.getAllCategories();

            if (medicine == null) {
                request.setAttribute("errorMessage", "Medicine not found.");
                request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                       .forward(request, response);
                return;
            }

            request.setAttribute("medicine",   medicine);
            request.setAttribute("categories", cats);
            request.getRequestDispatcher("/WEB-INF/views/admin/edit-medicine.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load medicine for editing.");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                   .forward(request, response);
        }
    }

    /** POST — validate and update the medicine */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr         = request.getParameter("medicineId");
        String medicineName  = request.getParameter("medicineName");
        String description   = request.getParameter("description");
        String priceStr      = request.getParameter("price");
        String stockStr      = request.getParameter("stock");
        String categoryIdStr = request.getParameter("categoryId");
        String prescription  = request.getParameter("prescriptionRequired");
        String status        = request.getParameter("status");

        int medicineId;
        try {
            medicineId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/medicines");
            return;
        }

        // --- Validation ---
        String error = validateInput(medicineName, priceStr, stockStr, categoryIdStr, status);
        if (error != null) {
            try {
                Medicine existing = medicineService.getMedicineByIdForAdmin(medicineId);
                request.setAttribute("medicine",   existing);
                request.setAttribute("categories", medicineService.getAllCategories());
                request.setAttribute("error",      error);
                request.getRequestDispatcher("/WEB-INF/views/admin/edit-medicine.jsp")
                       .forward(request, response);
            } catch (Exception ex) {
                response.sendRedirect(request.getContextPath() + "/admin/medicines?error=server");
            }
            return;
        }

        // --- Handle optional new image upload ---
        String newImageFileName = null;
        Part imagePart = request.getPart("image");
        if (imagePart != null && imagePart.getSize() > 0) {
            newImageFileName = saveImage(imagePart, request);
        }

        try {
            Medicine medicine = new Medicine();
            medicine.setMedicineId(medicineId);
            medicine.setMedicineName(medicineName.trim());
            medicine.setDescription(description);
            medicine.setPrice(Double.parseDouble(priceStr));
            medicine.setStock(Integer.parseInt(stockStr));
            medicine.setCategoryId(Integer.parseInt(categoryIdStr));
            medicine.setPrescriptionRequired("on".equals(prescription) || "true".equals(prescription));
            medicine.setStatus(status);
            medicine.setImage(newImageFileName);  // null if no new image uploaded

            medicineService.updateMedicine(medicine);
            response.sendRedirect(request.getContextPath() +
                                  "/admin/medicines?success=Medicine+updated+successfully");

        } catch (Exception e) {
            request.setAttribute("error", "Failed to update medicine: " + e.getMessage());
            try {
                request.setAttribute("categories", medicineService.getAllCategories());
            } catch (Exception ignored) {}
            request.getRequestDispatcher("/WEB-INF/views/admin/edit-medicine.jsp")
                   .forward(request, response);
        }
    }

    private String validateInput(String name, String price, String stock,
                                 String categoryId, String status) {
        if (ValidationUtil.isEmpty(name))             return "Medicine name is required.";
        if (!ValidationUtil.isPositiveDecimal(price)) return "Price must be a positive number.";
        if (!ValidationUtil.isNonNegativeInt(stock))  return "Stock must be a non-negative number.";
        if (ValidationUtil.isEmpty(categoryId))       return "Please select a category.";
        if (!"ACTIVE".equals(status) && !"INACTIVE".equals(status))
                                                      return "Invalid status value.";
        return null;
    }

    private String saveImage(Part imagePart, HttpServletRequest request) throws IOException {
        String originalName = imagePart.getSubmittedFileName();
        if (originalName == null || originalName.isEmpty()) return null;

        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) extension = originalName.substring(dotIndex);

        String fileName   = System.currentTimeMillis() + extension;
        String uploadPath = request.getServletContext().getRealPath("/images/medicines");
        imagePart.write(uploadPath + File.separator + fileName);
        return fileName;
    }
}
