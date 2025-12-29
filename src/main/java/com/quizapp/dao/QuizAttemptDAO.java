package com.quizapp.dao;

import com.quizapp.model.QuizAttempt;
import com.quizapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizAttemptDAO implements DAO<QuizAttempt> {
    
    @Override
    public QuizAttempt getById(int id) {
        QuizAttempt attempt = null;
        String sql = "SELECT * FROM quiz_attempts WHERE attempt_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                attempt = extractAttemptFromResultSet(rs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting attempt by ID: " + e.getMessage());
        }
        return attempt;
    }
    
    @Override
    public List<QuizAttempt> getAll() {
        List<QuizAttempt> attempts = new ArrayList<>();
        String sql = "SELECT * FROM quiz_attempts ORDER BY attempt_id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                attempts.add(extractAttemptFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all attempts: " + e.getMessage());
        }
        return attempts;
    }
    
    @Override
    public boolean insert(QuizAttempt attempt) {
        String sql = "INSERT INTO quiz_attempts (user_id, quiz_id, start_time, end_time, " +
                    "score, total_questions, correct_answers, status, ip_address, user_agent) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, attempt.getUser().getUserId());
            pstmt.setInt(2, attempt.getQuiz().getQuizId());
            pstmt.setTimestamp(3, Timestamp.valueOf(attempt.getStartTime()));
            
            if (attempt.getEndTime() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(attempt.getEndTime()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }
            
            pstmt.setDouble(5, attempt.getScore());
            pstmt.setInt(6, attempt.getTotalQuestions());
            pstmt.setInt(7, attempt.getCorrectAnswers());
            pstmt.setString(8, attempt.getStatus().name());
            pstmt.setString(9, attempt.getIpAddress());
            pstmt.setString(10, attempt.getUserAgent());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    attempt.setAttemptId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting attempt: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean update(QuizAttempt attempt) {
        String sql = "UPDATE quiz_attempts SET user_id=?, quiz_id=?, start_time=?, end_time=?, " +
                    "score=?, total_questions=?, correct_answers=?, status=?, ip_address=?, user_agent=? " +
                    "WHERE attempt_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attempt.getUser().getUserId());
            pstmt.setInt(2, attempt.getQuiz().getQuizId());
            pstmt.setTimestamp(3, Timestamp.valueOf(attempt.getStartTime()));
            
            if (attempt.getEndTime() != null) {
                pstmt.setTimestamp(4, Timestamp.valueOf(attempt.getEndTime()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }
            
            pstmt.setDouble(5, attempt.getScore());
            pstmt.setInt(6, attempt.getTotalQuestions());
            pstmt.setInt(7, attempt.getCorrectAnswers());
            pstmt.setString(8, attempt.getStatus().name());
            pstmt.setString(9, attempt.getIpAddress());
            pstmt.setString(10, attempt.getUserAgent());
            pstmt.setInt(11, attempt.getAttemptId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating attempt: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM quiz_attempts WHERE attempt_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting attempt: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM quiz_attempts";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting attempts: " + e.getMessage());
        }
        return 0;
    }
    
    // Additional methods
    public List<QuizAttempt> getByUserId(int userId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        String sql = "SELECT * FROM quiz_attempts WHERE user_id = ? ORDER BY start_time DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                attempts.add(extractAttemptFromResultSet(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting attempts by user: " + e.getMessage());
        }
        return attempts;
    }
    
    public List<QuizAttempt> getByQuizId(int quizId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        String sql = "SELECT * FROM quiz_attempts WHERE quiz_id = ? ORDER BY start_time DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                attempts.add(extractAttemptFromResultSet(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting attempts by quiz: " + e.getMessage());
        }
        return attempts;
    }
    
    public QuizAttempt getActiveAttempt(int userId, int quizId) {
        QuizAttempt attempt = null;
        String sql = "SELECT * FROM quiz_attempts WHERE user_id = ? AND quiz_id = ? AND status = 'IN_PROGRESS'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, quizId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                attempt = extractAttemptFromResultSet(rs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting active attempt: " + e.getMessage());
        }
        return attempt;
    }
    
    public boolean completeAttempt(int attemptId, double score, int correctAnswers) {
        String sql = "UPDATE quiz_attempts SET end_time = CURRENT_TIMESTAMP, score = ?, " +
                    "correct_answers = ?, status = 'COMPLETED' WHERE attempt_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, score);
            pstmt.setInt(2, correctAnswers);
            pstmt.setInt(3, attemptId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error completing attempt: " + e.getMessage());
        }
        return false;
    }
    
    private QuizAttempt extractAttemptFromResultSet(ResultSet rs) throws SQLException {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setAttemptId(rs.getInt("attempt_id"));
        attempt.setScore(rs.getDouble("score"));
        attempt.setTotalQuestions(rs.getInt("total_questions"));
        attempt.setCorrectAnswers(rs.getInt("correct_answers"));
        attempt.setStatus(QuizAttempt.AttemptStatus.valueOf(rs.getString("status")));
        Timestamp startTime = rs.getTimestamp("start_time");
        if (startTime != null) {
            attempt.setStartTime(startTime.toLocalDateTime());
        }
        
        Timestamp endTime = rs.getTimestamp("end_time");
        if (endTime != null) {
            attempt.setEndTime(endTime.toLocalDateTime());
        }
        
        attempt.setIpAddress(rs.getString("ip_address"));
        attempt.setUserAgent(rs.getString("user_agent"));
        return attempt;
    }
}