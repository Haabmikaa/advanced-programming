package com.quizapp.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quizapp.dao.UserDAO;
import com.quizapp.model.User;
import com.quizapp.util.GsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/admin/user")
public class AdminUserServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = GsonUtil.getGson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User admin = (User) (session != null ? session.getAttribute("user") : null);
        
        if (admin == null || !admin.isAdmin()) {
            response.setStatus(403);
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }

        String action = request.getParameter("action");
        response.setContentType("application/json");

        try {
            if ("save".equals(action)) {
                handleSave(request, response);
            } else if ("delete".equals(action)) {
                handleDelete(request, response);
            } else {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Invalid action\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleSave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            BufferedReader reader = request.getReader();
            JsonObject data = gson.fromJson(reader, JsonObject.class);

            if (data == null) {
                response.getWriter().write("{\"success\":false, \"error\":\"Invalid request data\"}");
                return;
            }

            String userIdStr = data.has("userId") ? data.get("userId").getAsString() : "";
            String username = data.has("username") ? data.get("username").getAsString() : "";
            String fullName = data.has("fullName") ? data.get("fullName").getAsString() : "";
            String email = data.has("email") ? data.get("email").getAsString() : "";
            String password = data.has("password") ? data.get("password").getAsString() : "";
            String roleStr = data.has("role") ? data.get("role").getAsString() : "STUDENT";

            User user;
            boolean success;

            if (userIdStr == null || userIdStr.isEmpty()) {
                // Create new
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    response.getWriter().write("{\"success\":false, \"error\":\"Username, email and password are required for new users\"}");
                    return;
                }
                user = new User();
                user.setUsername(username);
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPassword(password);
                user.setRole(User.UserRole.valueOf(roleStr));
                user.setActive(true);
                success = userDAO.insert(user);
            } else {
                // Update existing
                int userId = Integer.parseInt(userIdStr);
                user = userDAO.getById(userId);
                if (user == null) {
                    response.getWriter().write("{\"success\":false, \"error\":\"User not found\"}");
                    return;
                }
                user.setUsername(username);
                user.setFullName(fullName);
                user.setEmail(email);
                user.setRole(User.UserRole.valueOf(roleStr));
                if (password != null && !password.isEmpty()) {
                    user.setPassword(password);
                }
                success = userDAO.update(user);
            }

            JsonObject result = new JsonObject();
            result.addProperty("success", success);
            if (!success) {
                result.addProperty("error", "Database operation failed. Please check if the username or email is already taken.");
            }
            response.getWriter().write(gson.toJson(result));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false, \"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.getWriter().write("{\"success\":false, \"error\":\"User ID is required\"}");
                return;
            }
            
            int userId = Integer.parseInt(idStr);
            boolean success = userDAO.delete(userId);
            
            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
            } else {
                result.addProperty("success", false);
                result.addProperty("error", "Could not delete user. This usually happens if they have related data like quizzes or results that cannot be removed.");
            }
            response.getWriter().write(gson.toJson(result));
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"success\":false, \"error\":\"Invalid User ID format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"success\":false, \"error\":\"Server error: " + e.getMessage() + "\"}");
        }
    }
}
