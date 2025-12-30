package com.quizapp.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/pages/login.jsp?error=missing");
                return;
            }

            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticate(username, password);

            if (user != null) {
                // Create session and store user info
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole().name());
                session.setAttribute("userId", user.getUserId());

                // Build safe base path (handles /QuizWebApp context)
                String ctx = request.getContextPath();

                // Redirect according to role
                switch (user.getRole()) {
                    case STUDENT:
                        response.sendRedirect(ctx + "/pages/student/dashboard.jsp");
                        break;
                    case TEACHER:
                        response.sendRedirect(ctx + "/pages/teacher/dashboard.jsp");
                        break;
                    case ADMIN:
                        response.sendRedirect(ctx + "/pages/admin/dashboard.jsp");
                        break;
                    default:
                        // Unknown role: back to login with message
                        request.setAttribute("error", "Unrecognized role");
                        request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
                }

            } else {
                response.sendRedirect(request.getContextPath() + "/pages/login.jsp?error=invalid");
            }
        } catch (Exception e) {
            System.err.println("❌ CRITICAL ERROR IN LOGINSERVLET:");
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/pages/login.jsp?error=system");
        }
    }
}
