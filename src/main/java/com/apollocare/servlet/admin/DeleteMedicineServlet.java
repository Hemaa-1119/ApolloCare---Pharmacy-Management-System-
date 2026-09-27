package com.apollocare.servlet.admin;

import com.apollocare.service.MedicineService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Soft-deletes (deactivates) a medicine.
 *
 * POST /admin/medicine/delete?id=5 → set status = INACTIVE, redirect
 *
 * Soft-delete (status = INACTIVE) is used instead of hard DELETE so that:
 * - Existing order history still references this medicine
 * - The medicine can be re-activated if needed
 */
public class DeleteMedicineServlet extends HttpServlet {

    private final MedicineService medicineService = new MedicineService();

    /** POST — deactivate the medicine */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/medicines?error=invalid");
            return;
        }

        try {
            int medicineId = Integer.parseInt(idParam);
            medicineService.deactivateMedicine(medicineId);
            response.sendRedirect(request.getContextPath() +
                                  "/admin/medicines?success=Medicine+deactivated+successfully");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/medicines?error=invalid");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() +
                                  "/admin/medicines?error=Failed+to+deactivate+medicine");
        }
    }

    /** Reject GET requests for this action — only POST is allowed */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/medicines");
    }
}
