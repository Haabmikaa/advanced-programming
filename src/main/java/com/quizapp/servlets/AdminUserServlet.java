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


public class AdminUserServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = GsonUtil.getGson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\":false, \"error\":\"GET method not supported. Use POST.\"}");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println("AdminUserServlet: Received POST request. Action: " + action);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            System.out.println("AdminUserServlet: Unauthorized access attempt.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"success\":false, \"error\":\"Unauthorized access\"}");
            return;
        }

        try {
            if ("save".equals(action)) {
                handleSave(request, response);
            } else if ("delete".equals(action)) {
                handleDelete(request, response);
            } else {
                System.out.println("AdminUserServlet: Invalid action '" + action + "'");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\":false, \"error\":\"Invalid action\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorResult = new JsonObject();
            errorResult.addProperty("success", false);
            // Include stack trace and cause for debugging
            StringBuilder fullError = new StringBuilder(e.getMessage() != null ? e.getMessage() : e.toString());
            if (e.getCause() != null) {
                fullError.append(" | Cause: ").append(e.getCause().getMessage());
            }
            errorResult.addProperty("error", "Server Error: " + fullError.toString());
            response.getWriter().write(gson.toJson(errorResult));
        }
    }

    private void handleSave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            BufferedReader reader = request.getReader();
            JsonObject data = gson.fromJson(reader, JsonObject.class);

            if (data == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\":false, \"error\":\"Invalid request data\"}");
                return;
            }

            String userIdStr = data.has("userId") && !data.get("userId").isJsonNull() ? data.get("userId").getAsString() : "";
            String username = data.has("username") && !data.get("username").isJsonNull() ? data.get("username").getAsString().trim() : "";
            String fullName = data.has("fullName") && !data.get("fullName").isJsonNull() ? data.get("fullName").getAsString().trim() : "";
            String email = data.has("email") && !data.get("email").isJsonNull() ? data.get("email").getAsString().trim() : "";
            String password = data.has("password") && !data.get("password").isJsonNull() ? data.get("password").getAsString() : "";
            String roleStr = data.has("role") && !data.get("role").isJsonNull() ? data.get("role").getAsString() : "STUDENT";

            User user;
            boolean success;
            String errorMsg = null;

            if (userIdStr == null || userIdStr.isEmpty()) {
                // Create new user
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"success\":false, \"error\":\"Username, full name, email and password are required for new users\"}");
                    return;
                }

                // Check for duplicates
                if (userDAO.getByUsername(username) != null) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write("{\"success\":false, \"error\":\"Username '" + username + "' is already taken.\"}");
                    return;
                }
                if (userDAO.getByEmail(email) != null) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write("{\"success\":false, \"error\":\"Email '" + email + "' is already registered.\"}");
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
                // Update existing user
                int userId = Integer.parseInt(userIdStr);
                user = userDAO.getById(userId);
                if (user == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"success\":false, \"error\":\"User not found\"}");
                    return;
                }

                // Check if username/email changed and if they are taken by others
                if (!user.getUsername().equalsIgnoreCase(username)) {
                    if (userDAO.getByUsername(username) != null) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        response.getWriter().write("{\"success\":false, \"error\":\"Username '" + username + "' is already taken by another user.\"}");
                        return;
                    }
                }
                if (!user.getEmail().equalsIgnoreCase(email)) {
                    if (userDAO.getByEmail(email) != null) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        response.getWriter().write("{\"success\":false, \"error\":\"Email '" + email + "' is already taken by another user.\"}");
                        return;
                    }
                }

                user.setUsername(username);
                user.setFullName(fullName);
                user.setEmail(email);
                user.setRole(User.UserRole.valueOf(roleStr));
                if (password != null && !password.trim().isEmpty()) {
                    user.setPassword(password);
                }
                success = userDAO.update(user);
            }

            JsonObject result = new JsonObject();
            result.addProperty("success", success);
            if (!success) {
                result.addProperty("error", "Database operation failed. The user might have linked records (quizzes/attempts) or there's a connection issue.");
            }
            response.getWriter().write(gson.toJson(result));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false, \"error\":\"Invalid User ID format: " + e.getMessage() + "\"}");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false, \"error\":\"Invalid role or data value: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false, \"error\":\"Unexpected save error: " + e.getMessage() + "\"}");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\":false, \"error\":\"User ID is required\"}");
                return;
            }

            int userId = Integer.parseInt(idStr);
            
            // Check if user exists
            User userToDelete = userDAO.getById(userId);
            if (userToDelete == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"success\":false, \"error\":\"User not found with ID: " + userId + "\"}");
                return;
            }

            // Prevent deleting self (current admin)
            HttpSession session = request.getSession(false);
            Integer currentUserId = (Integer) session.getAttribute("userId");
            if (currentUserId != null && currentUserId == userId) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\":false, \"error\":\"Security block: You cannot delete your own admin account while logged in.\"}");
                return;
            }

            boolean success = userDAO.delete(userId);
            JsonObject result = new JsonObject();
            if (success) {
                result.addProperty("success", true);
                result.addProperty("message", "User deleted successfully");
            } else {
                result.addProperty("success", false);
                result.addProperty("error", "Database refused deletion. This user likely has related data (like quiz attempts) that must be deleted first.");
            }
            response.getWriter().write(gson.toJson(result));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false, \"error\":\"Invalid User ID format: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false, \"error\":\"Unexpected delete error: " + e.getMessage() + "\"}");
        }
    }
}
