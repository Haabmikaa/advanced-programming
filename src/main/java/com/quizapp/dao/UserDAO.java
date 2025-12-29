package com.quizapp.dao;

import com.quizapp.model.User;
import com.quizapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements DAO<User> {
    
    @Override
    public User getById(int id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = extractUserFromResultSet(rs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        return user;
    }
    
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }
    
    @Override
    public boolean insert(User user) {
        String sql = "INSERT INTO users (username, password, email, full_name, role, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getRole().name());
            pstmt.setBoolean(6, user.isActive());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET username=?, password=?, email=?, full_name=?, " +
                    "role=?, is_active=?, last_login=? WHERE user_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getRole().name());
            pstmt.setBoolean(6, user.isActive());
            
            if (user.getLastLogin() != null) {
                pstmt.setTimestamp(7, Timestamp.valueOf(user.getLastLogin()));
            } else {
                pstmt.setNull(7, Types.TIMESTAMP);
            }
            
            pstmt.setInt(8, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE user_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            // Re-throw or handle specifically if needed, but for now we return false
            // which the servlet handles.
        }
        return false;
    }
    
    @Override
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting users: " + e.getMessage());
        }
        return 0;
    }
    
    // Additional methods specific to User
    public User getByUsername(String username) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = extractUserFromResultSet(rs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
        }
        return user;
    }
    
    public User authenticate(String username, String password) {
        User user = getByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.isActive()) {
            // Update last login
            user.setLastLogin(java.time.LocalDateTime.now());
            update(user);
            return user;
        }
        return null;
    }
    
    public List<User> getByRole(User.UserRole role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY username";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role.name());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting users by role: " + e.getMessage());
        }
        return users;
    }
    
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));
        Timestamp regDate = rs.getTimestamp("registration_date");
        if (regDate != null) {
            user.setRegistrationDate(regDate.toLocalDateTime());
        }
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}