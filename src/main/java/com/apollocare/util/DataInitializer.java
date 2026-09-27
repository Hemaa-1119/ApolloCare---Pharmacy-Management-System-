package com.apollocare.util;

import com.apollocare.dao.UserDAO;
import com.apollocare.model.User;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.File;
import java.sql.SQLException;

/**
 * Application startup listener — runs once when Tomcat deploys the WAR.
 *
 * Responsibilities:
 * 1. Create the medicine image upload directory if it does not exist.
 * 2. Seed the default ADMIN user if no admin account exists in the database.
 *
 * Demonstrates: ServletContextListener, application lifecycle management.
 *
 * In web.xml this is registered as:
 *   <listener>
 *       <listener-class>com.apollocare.util.DataInitializer</listener-class>
 *   </listener>
 */
public class DataInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[ApolloCare] Application starting...");

        // ----------------------------------------------------------
        // Step 1: Ensure the medicine image upload directory exists.
        // getRealPath resolves to the deployed WAR directory on disk.
        // ----------------------------------------------------------
        String uploadDir = sce.getServletContext().getRealPath("/images/medicines");
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("[ApolloCare] Created image upload directory: " + uploadDir);
            } else {
                System.err.println("[ApolloCare] WARNING: Could not create image directory: " + uploadDir);
            }
        }

        // ----------------------------------------------------------
        // Step 2: Seed the default admin user if none exists.
        // This avoids hardcoding the BCrypt hash in schema.sql
        // and demonstrates application-layer initialisation.
        // ----------------------------------------------------------
        UserDAO userDAO = new UserDAO();
        try {
            if (userDAO.findByEmail("admin@apollocare.com") == null) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@apollocare.com");
                admin.setPassword(PasswordUtil.hashPassword("Admin@123"));
                admin.setPhone("9999999999");
                admin.setAddress("Apollo Care HQ, Mumbai");
                admin.setRole("ADMIN");
                userDAO.saveUser(admin);
                System.out.println("[ApolloCare] Default admin created. Login: admin@apollocare.com / Admin@123");
            }
        } catch (SQLException e) {
            System.err.println("[ApolloCare] ERROR seeding admin user: " + e.getMessage());
            // Non-fatal — the application still starts; admin can be created manually.
        }

        System.out.println("[ApolloCare] Application started successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[ApolloCare] Application stopped.");
    }
}
