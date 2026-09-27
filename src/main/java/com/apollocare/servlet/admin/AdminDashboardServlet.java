package com.apollocare.servlet.admin;

import com.apollocare.service.MedicineService;
import com.apollocare.service.OrderService;
import com.apollocare.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Displays the admin dashboard with summary statistics.
 *
 * GET /admin/dashboard → show counts of medicines, customers, orders
 */
public class AdminDashboardServlet extends HttpServlet {

    private final MedicineService medicineService = new MedicineService();
    private final UserService     userService     = new UserService();
    private final OrderService    orderService    = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int medicineCount = medicineService.getMedicineCount();
            int customerCount = userService.getCustomerCount();
            int orderCount    = orderService.getOrderCount();

            request.setAttribute("medicineCount", medicineCount);
            request.setAttribute("customerCount", customerCount);
            request.setAttribute("orderCount",    orderCount);

            // Show recent orders on dashboard (latest 5)
            request.setAttribute("recentOrders",
                orderService.getAllOrders().stream().limit(5).toList());

            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load dashboard data.");
            request.getRequestDispatcher("/WEB-INF/views/common/error.jsp")
                   .forward(request, response);
        }
    }
}
