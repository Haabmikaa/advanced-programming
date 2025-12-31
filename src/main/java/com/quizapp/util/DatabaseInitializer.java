package com.quizapp.util;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.model.User.UserRole;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.List;

/**
 * Listener that runs when the web application starts.
 * It ensures that at least one ADMIN user exists in the database.
 * If no admin exists, it creates one using environment variables for security.
 */
@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🚀 QuizWebApp starting up. Checking for admin user...");
        try {
            initializeAdminUser();
        } catch (Exception e) {
            System.err.println("❌ CRITICAL: Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeAdminUser() {
        UserDAO userDAO = new UserDAO();
        List<User> admins = userDAO.getByRole(UserRole.ADMIN);

        if (admins == null || admins.isEmpty()) {
            System.out.println("⚠️ No admin user found in database. Creating initial secure admin...");

            // Get credentials from environment variables (set these in Render)
            String adminUser = System.getenv("ADMIN_USERNAME");
            String adminPass = System.getenv("ADMIN_PASSWORD");
            String adminEmail = System.getenv("ADMIN_EMAIL");

            // Default fallbacks if env vars are missing
            if (adminUser == null || adminUser.trim().isEmpty()) {
                adminUser = "admin";
            }
            if (adminPass == null || adminPass.trim().isEmpty()) {
                adminPass = "admin123"; // Note: User should change this after first login
                System.out.println("ℹ️ ADMIN_PASSWORD not set. Using default: " + adminPass);
            }
            if (adminEmail == null || adminEmail.trim().isEmpty()) {
                adminEmail = "admin@quizapp.com";
            }

            User admin = new User();
            admin.setUsername(adminUser);
            admin.setPassword(adminPass);
            admin.setEmail(adminEmail);
            admin.setFullName("System Administrator");
            admin.setRole(UserRole.ADMIN);
            admin.setActive(true);

            boolean success = userDAO.insert(admin);
            if (success) {
                System.out.println("✅ Initial admin user '" + adminUser + "' created successfully.");
                System.out.println("🔒 Credentials are now managed via environment variables and the database.");
            } else {
                System.err.println("❌ Failed to insert initial admin user. Check database connection.");
            }
        } else {
            System.out.println("✅ Admin user(s) already exist. No action needed.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("👋 QuizWebApp shutting down.");
    }
}
