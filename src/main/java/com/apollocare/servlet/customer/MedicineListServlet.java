package com.apollocare.servlet.customer;

import com.apollocare.model.Medicine;
import com.apollocare.service.MedicineService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Displays the medicine catalogue with search and pagination.
 *
 * GET /medicines              → page 1, all active medicines
 * GET /medicines?page=2       → page 2
 * GET /medicines?keyword=pain → search results
 * GET /medicines?keyword=pain&page=2 → search page 2
 *
 * Flow:
 * medicines.jsp → GET → MedicineListServlet → MedicineService → MedicineDAO → MySQL
 *               ← forward ← request.setAttribute("medicines", ...)
 *
 * Demonstrates: request parameters, pagination, search with PreparedStatement,
 *               request.setAttribute(), forward to JSP.
 */
public class MedicineListServlet extends HttpServlet {

    private final MedicineService medicineService = new MedicineService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Read query parameters (with safe defaults)
        String keyword = request.getParameter("keyword");
        int page = 1;

        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;  // Invalid page param → default to 1
        }

        if (page < 1) page = 1;

        try {
            // Fetch paginated medicines from service
            List<Medicine> medicines = medicineService.getMedicines(keyword, page);
            int totalPages           = medicineService.getTotalPages(keyword);

            // Clamp page to valid range
            if (page > totalPages && totalPages > 0) {
                page = totalPages;
                medicines = medicineService.getMedicines(keyword, page);
            }

            // Set attributes for JSP to render
            request.setAttribute("medicines",  medicines);
            request.setAttribute("keyword",    keyword != null ? keyword : "");
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages",  totalPages);

            request.getRequestDispatcher("/WEB-INF/views/customer/medicines.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Unable to load medicines. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                   .forward(request, response);
        }
    }
}
