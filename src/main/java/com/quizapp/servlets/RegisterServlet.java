package com.quizapp.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.model.User.UserRole;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // --- Get form inputs ---
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String email = request.getParameter("email");
            String fullName = request.getParameter("fullName");
            String roleParam = request.getParameter("role");

            // --- Server-side validation (extra safety) ---
            if (username == null || password == null || confirmPassword == null ||
                email == null || fullName == null || roleParam == null ||
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                email.isEmpty() || fullName.isEmpty() || roleParam.isEmpty()) {

                request.setAttribute("error", "All fields are required!");
                request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
                return;
            }

            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Passwords do not match!");
                request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
                return;
            }

            UserDAO userDAO = new UserDAO();

            // --- Check if username already exists ---
            if (userDAO.getByUsername(username) != null) {
                request.setAttribute("error", "Username already exists! Please choose another one.");
                request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
                return;
            }

            // --- Build User object ---
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password); // You can hash this later
            newUser.setEmail(email);
            newUser.setFullName(fullName);

            // Assign role based on selected option
            try {
                newUser.setRole(UserRole.valueOf(roleParam));
            } catch (IllegalArgumentException e) {
                newUser.setRole(UserRole.STUDENT); // fallback
            }

            newUser.setActive(true);

            // --- Insert into database using your DAO ---
            boolean success = userDAO.insert(newUser);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/pages/login.jsp?success=registered");
            } else {
                request.setAttribute("error", "Registration failed: Could not save user to database. Please check server logs.");
                request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("❌ CRITICAL ERROR IN REGISTERSERVLET:");
            e.printStackTrace();
            request.setAttribute("error", "A critical system error occurred: " + e.getMessage());
            request.getRequestDispatcher("/pages/register.jsp").forward(request, response);
        }
    }
}
