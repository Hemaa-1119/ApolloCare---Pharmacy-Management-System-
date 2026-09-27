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
 * Handles adding a new medicine (admin).
 *
 * GET  /admin/medicine/add → show the add medicine form
 * POST /admin/medicine/add → validate, save medicine, redirect to medicine list
 *
 * Image Upload:
 * Uses jakarta.servlet.http.Part (native Servlet 3.0+ API).
 * multipart-config is declared in web.xml (not via annotation here,
 * since we use pure web.xml mapping). The @MultipartConfig annotation
 * here is needed for Tomcat to process multipart requests on this servlet.
 *
 * The uploaded file is saved to: webapp/images/medicines/<timestamp>.<ext>
 * Only the filename is stored in the database.
 */
@MultipartConfig(
    maxFileSize    = 5 * 1024 * 1024,   // 5 MB
    maxRequestSize = 10 * 1024 * 1024   // 10 MB
)
public class AddMedicineServlet extends HttpServlet {

    private final MedicineService medicineService = new MedicineService();

    /** GET — show the blank add-medicine form */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Category> categories = medicineService.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/WEB-INF/views/admin/add-medicine.jsp")
                   .forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Unable to load categories.");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                   .forward(request, response);
        }
    }

    /** POST — validate, handle image upload, save medicine */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String medicineName  = request.getParameter("medicineName");
        String description   = request.getParameter("description");
        String priceStr      = request.getParameter("price");
        String stockStr      = request.getParameter("stock");
        String categoryIdStr = request.getParameter("categoryId");
        String prescription  = request.getParameter("prescriptionRequired");

        // --- Server-side validation ---
        String error = validateInput(medicineName, priceStr, stockStr, categoryIdStr);
        if (error != null) {
            try {
                request.setAttribute("error",       error);
                request.setAttribute("medicineName", medicineName);
                request.setAttribute("description",  description);
                request.setAttribute("price",        priceStr);
                request.setAttribute("stock",        stockStr);
                request.setAttribute("categories",   medicineService.getAllCategories());
                request.getRequestDispatcher("/WEB-INF/views/admin/add-medicine.jsp")
                       .forward(request, response);
            } catch (Exception ex) {
                response.sendRedirect(request.getContextPath() + "/admin/medicines?error=server");
            }
            return;
        }

        // --- Handle image upload ---
        String imageFileName = null;
        Part imagePart = request.getPart("image");
        if (imagePart != null && imagePart.getSize() > 0) {
            imageFileName = saveImage(imagePart, request);
        }

        // --- Build and save medicine ---
        try {
            Medicine medicine = new Medicine();
            medicine.setMedicineName(medicineName.trim());
            medicine.setDescription(description);
            medicine.setPrice(Double.parseDouble(priceStr));
            medicine.setStock(Integer.parseInt(stockStr));
            medicine.setCategoryId(Integer.parseInt(categoryIdStr));
            medicine.setPrescriptionRequired("on".equals(prescription) || "true".equals(prescription));
            medicine.setImage(imageFileName);
            medicine.setStatus("ACTIVE");

            medicineService.addMedicine(medicine);
            response.sendRedirect(request.getContextPath() +
                                  "/admin/medicines?success=Medicine+added+successfully");

        } catch (Exception e) {
            request.setAttribute("error", "Failed to save medicine: " + e.getMessage());
            try {
                request.setAttribute("categories", medicineService.getAllCategories());
            } catch (Exception ignored) {}
            request.getRequestDispatcher("/WEB-INF/views/admin/add-medicine.jsp")
                   .forward(request, response);
        }
    }

    private String validateInput(String name, String price, String stock, String categoryId) {
        if (ValidationUtil.isEmpty(name))            return "Medicine name is required.";
        if (!ValidationUtil.isPositiveDecimal(price)) return "Price must be a positive number.";
        if (!ValidationUtil.isNonNegativeInt(stock)) return "Stock must be a non-negative number.";
        if (ValidationUtil.isEmpty(categoryId))      return "Please select a category.";
        return null;
    }

    /**
     * Saves the uploaded image to webapp/images/medicines/ and returns the filename.
     * Filename = timestamp + original extension (avoids name collisions).
     */
    private String saveImage(Part imagePart, HttpServletRequest request) throws IOException {
        String originalName = imagePart.getSubmittedFileName();
        if (originalName == null || originalName.isEmpty()) return null;

        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) extension = originalName.substring(dotIndex);

        String fileName = System.currentTimeMillis() + extension;
        String uploadPath = request.getServletContext().getRealPath("/images/medicines");
        imagePart.write(uploadPath + File.separator + fileName);
        return fileName;
    }
}
