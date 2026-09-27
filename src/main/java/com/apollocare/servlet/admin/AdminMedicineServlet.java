package com.apollocare.servlet.admin;

import com.apollocare.model.Medicine;
import com.apollocare.service.MedicineService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Displays the admin medicine management page with search and pagination.
 *
 * GET /admin/medicines              → all medicines (page 1)
 * GET /admin/medicines?page=2       → page 2
 * GET /admin/medicines?keyword=pain → search
 */
public class AdminMedicineServlet extends HttpServlet {

    private final MedicineService medicineService = new MedicineService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        int page = 1;
        try {
            String p = request.getParameter("page");
            if (p != null && !p.isEmpty()) page = Integer.parseInt(p);
        } catch (NumberFormatException e) {
            page = 1;
        }
        if (page < 1) page = 1;

        try {
            List<Medicine> medicines = medicineService.getAdminMedicines(keyword, page);
            int totalPages           = medicineService.getAdminTotalPages(keyword);

            request.setAttribute("medicines",    medicines);
            request.setAttribute("keyword",      keyword != null ? keyword : "");
            request.setAttribute("currentPage",  page);
            request.setAttribute("totalPages",   totalPages);

            // Check for success/error flash messages from redirects
            request.setAttribute("successMsg", request.getParameter("success"));
            request.setAttribute("errorMsg",   request.getParameter("error"));

            request.getRequestDispatcher("/WEB-INF/views/admin/medicines.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load medicines.");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                   .forward(request, response);
        }
    }
}
