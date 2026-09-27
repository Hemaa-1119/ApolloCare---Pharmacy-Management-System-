package com.apollocare.servlet.customer;

import com.apollocare.model.Medicine;
import com.apollocare.service.MedicineService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Displays the detail page for a single medicine.
 *
 * GET /medicine/detail?id=5 → show medicine with ID 5
 *
 * Handles invalid or missing IDs gracefully — shows a
 * user-friendly error instead of exposing a stack trace.
 */
public class MedicineDetailServlet extends HttpServlet {

    private final MedicineService medicineService = new MedicineService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        // Validate that an ID was provided
        if (idParam == null || idParam.trim().isEmpty()) {
            request.setAttribute("errorMessage", "No medicine specified.");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                   .forward(request, response);
            return;
        }

        int medicineId;
        try {
            medicineId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid medicine ID.");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                   .forward(request, response);
            return;
        }

        try {
            Medicine medicine = medicineService.getMedicineById(medicineId);

            if (medicine == null) {
                // Medicine not found or is inactive
                request.setAttribute("errorMessage",
                    "The medicine you are looking for is not available.");
                request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                       .forward(request, response);
                return;
            }

            request.setAttribute("medicine", medicine);
            request.getRequestDispatcher("/WEB-INF/views/customer/medicine-detail.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Unable to load medicine details. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                   .forward(request, response);
        }
    }
}
