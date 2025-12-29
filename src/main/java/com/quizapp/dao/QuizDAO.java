package com.quizapp.dao;

import com.quizapp.model.Quiz;
import com.quizapp.model.User;
import com.quizapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO implements DAO<Quiz> {
    
    @Override
    public Quiz getById(int id) {
        Quiz quiz = null;
        String sql = "SELECT * FROM quizzes WHERE quiz_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                quiz = extractQuizFromResultSet(rs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting quiz by ID: " + e.getMessage());
        }
        return quiz;
    }
    
    @Override
    public List<Quiz> getAll() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes ORDER BY quiz_id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                quizzes.add(extractQuizFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all quizzes: " + e.getMessage());
        }
        return quizzes;
    }
    
    @Override
    public boolean insert(Quiz quiz) {
        String sql = "INSERT INTO quizzes (title, description, created_by, category, " +
                    "difficulty, duration_minutes, max_attempts, is_published) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, quiz.getTitle());
            pstmt.setString(2, quiz.getDescription());
            pstmt.setInt(3, quiz.getCreatedBy().getUserId());
            pstmt.setString(4, quiz.getCategory());
            pstmt.setString(5, quiz.getDifficulty().name());
            pstmt.setInt(6, quiz.getDurationMinutes());
            pstmt.setInt(7, quiz.getMaxAttempts());
            pstmt.setBoolean(8, quiz.isPublished());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    quiz.setQuizId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting quiz: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean update(Quiz quiz) {
        String sql = "UPDATE quizzes SET title=?, description=?, created_by=?, category=?, " +
                    "difficulty=?, duration_minutes=?, max_attempts=?, is_published=? " +
                    "WHERE quiz_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, quiz.getTitle());
            pstmt.setString(2, quiz.getDescription());
            pstmt.setInt(3, quiz.getCreatedBy().getUserId());
            pstmt.setString(4, quiz.getCategory());
            pstmt.setString(5, quiz.getDifficulty().name());
            pstmt.setInt(6, quiz.getDurationMinutes());
            pstmt.setInt(7, quiz.getMaxAttempts());
            pstmt.setBoolean(8, quiz.isPublished());
            pstmt.setInt(9, quiz.getQuizId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating quiz: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM quizzes WHERE quiz_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting quiz: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM quizzes";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting quizzes: " + e.getMessage());
        }
        return 0;
    }
    
    // Additional methods
    public List<Quiz> getPublishedQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE is_published = TRUE ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                quizzes.add(extractQuizFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting published quizzes: " + e.getMessage());
        }
        return quizzes;
    }
    
    public List<Quiz> getByCategory(String category) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE category = ? AND is_published = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                quizzes.add(extractQuizFromResultSet(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting quizzes by category: " + e.getMessage());
        }
        return quizzes;
    }
    
    public List<Quiz> getByCreator(int userId) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE created_by = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                quizzes.add(extractQuizFromResultSet(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting quizzes by creator: " + e.getMessage());
        }
        return quizzes;
    }
    
    private Quiz extractQuizFromResultSet(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setQuizId(rs.getInt("quiz_id"));
        quiz.setTitle(rs.getString("title"));
        quiz.setDescription(rs.getString("description"));
        
        // Create minimal user object for creator
        User creator = new User();
        creator.setUserId(rs.getInt("created_by"));
        quiz.setCreatedBy(creator);
        
        quiz.setCategory(rs.getString("category"));
        quiz.setDifficulty(Quiz.DifficultyLevel.valueOf(rs.getString("difficulty")));
        quiz.setDurationMinutes(rs.getInt("duration_minutes"));
        quiz.setMaxAttempts(rs.getInt("max_attempts"));
        quiz.setPublished(rs.getBoolean("is_published"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            quiz.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            quiz.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return quiz;
    }

    public List<Quiz> getQuizzesNotTakenByStudent(int userId) {
        List<Quiz> quizzes = new ArrayList<>();
        // SQL: Get published quizzes that DO NOT have an entry in quiz_attempts for this user
        String sql = "SELECT * FROM quizzes WHERE is_published = TRUE AND quiz_id NOT IN " +
                "(SELECT quiz_id FROM quiz_attempts WHERE user_id = ? AND status = 'COMPLETED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { quizzes.add(extractQuizFromResultSet(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return quizzes;
    }

    public List<Quiz> getQuizzesTakenByStudent(int userId) {
        List<Quiz> quizzes = new ArrayList<>();
        // SQL: Get published quizzes that DO have a completed entry in quiz_attempts
        String sql = "SELECT * FROM quizzes WHERE quiz_id IN " +
                "(SELECT quiz_id FROM quiz_attempts WHERE user_id = ? AND status = 'COMPLETED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { quizzes.add(extractQuizFromResultSet(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return quizzes;
    }
}