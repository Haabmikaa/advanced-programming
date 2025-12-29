package com.quizapp.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.quizapp.dao.QuizDAO;
import com.quizapp.dao.UserDAO;
import com.quizapp.model.Quiz;
import com.quizapp.model.User;

public class AdminServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("pages/login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("users".equals(action)) {
            listUsers(request, response);
        } else if ("quizzes".equals(action)) {
            listAllQuizzes(request, response);
        } else if ("dashboard".equals(action)) {
            showDashboard(request, response);
        } else {
            showDashboard(request, response);
        }
    }
    
    private void listUsers(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAll();
        
        request.setAttribute("users", users);
        request.getRequestDispatcher("pages/admin/users.jsp").forward(request, response);
    }
    
    private void listAllQuizzes(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        QuizDAO quizDAO = new QuizDAO();
        List<Quiz> quizzes = quizDAO.getAll();
        
        request.setAttribute("quizzes", quizzes);
        request.getRequestDispatcher("pages/admin/quizzes.jsp").forward(request, response);
    }
    
    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        UserDAO userDAO = new UserDAO();
        QuizDAO quizDAO = new QuizDAO();
        
        int totalUsers = userDAO.getCount();
        int totalQuizzes = quizDAO.getCount();
        
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalQuizzes", totalQuizzes);
        request.getRequestDispatcher("pages/admin/dashboard.jsp").forward(request, response);
    }
}