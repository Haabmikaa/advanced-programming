package com.quizapp.dao;

import com.quizapp.model.Result;
import com.quizapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO implements DAO<Result> {
    
    @Override
    public Result getById(int id) {
        Result result = null;
        String sql = "SELECT r.*, q.title as quiz_title FROM results r " +
                    "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                    "WHERE r.result_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                result = extractResultFromResultSet(rs);
                
                // Populate Quiz
                com.quizapp.model.Quiz quiz = new com.quizapp.model.Quiz();
                quiz.setQuizId(rs.getInt("quiz_id"));
                quiz.setTitle(rs.getString("quiz_title"));
                result.setQuiz(quiz);
                
                // Populate Attempt
                com.quizapp.model.QuizAttempt attempt = new com.quizapp.model.QuizAttempt();
                attempt.setAttemptId(rs.getInt("attempt_id"));
                result.setAttempt(attempt);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting result by ID: " + e.getMessage());
        }
        return result;
    }
    
    @Override
    public List<Result> getAll() {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT r.*, q.title as quiz_title, u.full_name as student_name " +
                    "FROM results r " +
                    "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "ORDER BY r.submitted_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Result result = extractResultFromResultSet(rs);
                
                // Populate Quiz
                com.quizapp.model.Quiz quiz = new com.quizapp.model.Quiz();
                quiz.setQuizId(rs.getInt("quiz_id"));
                quiz.setTitle(rs.getString("quiz_title"));
                result.setQuiz(quiz);
                
                // Populate User
                com.quizapp.model.User student = new com.quizapp.model.User();
                student.setUserId(rs.getInt("user_id"));
                student.setFullName(rs.getString("student_name"));
                result.setUser(student);
                
                results.add(result);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all results: " + e.getMessage());
        }
        return results;
    }
    
    @Override
    public boolean insert(Result result) {
        String sql = "INSERT INTO results (user_id, quiz_id, attempt_id, total_score, " +
                    "percentage, time_taken_seconds, submitted_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, result.getUser().getUserId());
            pstmt.setInt(2, result.getQuiz().getQuizId());
            pstmt.setInt(3, result.getAttempt().getAttemptId());
            pstmt.setDouble(4, result.getTotalScore());
            pstmt.setDouble(5, result.getPercentage());
            pstmt.setInt(6, result.getTimeTakenSeconds());
            pstmt.setTimestamp(7, Timestamp.valueOf(result.getSubmittedAt()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    result.setResultId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting result: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean update(Result result) {
        String sql = "UPDATE results SET user_id=?, quiz_id=?, attempt_id=?, total_score=?, " +
                    "percentage=?, time_taken_seconds=?, submitted_at=? WHERE result_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, result.getUser().getUserId());
            pstmt.setInt(2, result.getQuiz().getQuizId());
            pstmt.setInt(3, result.getAttempt().getAttemptId());
            pstmt.setDouble(4, result.getTotalScore());
            pstmt.setDouble(5, result.getPercentage());
            pstmt.setInt(6, result.getTimeTakenSeconds());
            pstmt.setTimestamp(7, Timestamp.valueOf(result.getSubmittedAt()));
            pstmt.setInt(8, result.getResultId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating result: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM results WHERE result_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting result: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM results";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting results: " + e.getMessage());
        }
        return 0;
    }
    
    // Additional methods
    public List<Result> getByUserId(int userId) {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT r.*, q.title as quiz_title FROM results r " +
                    "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                    "WHERE r.user_id = ? ORDER BY r.submitted_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Result result = extractResultFromResultSet(rs);
                
                // Manually set quiz title since extractResultFromResultSet doesn't do it
                com.quizapp.model.Quiz quiz = new com.quizapp.model.Quiz();
                quiz.setQuizId(rs.getInt("quiz_id"));
                quiz.setTitle(rs.getString("quiz_title"));
                result.setQuiz(quiz);
                
                results.add(result);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting results by user: " + e.getMessage());
        }
        return results;
    }
    
    public List<Result> getByQuizId(int quizId) {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT * FROM results WHERE quiz_id = ? ORDER BY percentage DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                results.add(extractResultFromResultSet(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting results by quiz: " + e.getMessage());
        }
        return results;
    }
    
    public List<Result> getByTeacherId(int teacherId) {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT r.*, q.title as quiz_title, u.full_name as student_name " +
                    "FROM results r " +
                    "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                    "JOIN users u ON r.user_id = u.user_id " +
                    "WHERE q.created_by = ? " +
                    "ORDER BY r.submitted_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Result result = extractResultFromResultSet(rs);
                
                // Populate Quiz
                com.quizapp.model.Quiz quiz = new com.quizapp.model.Quiz();
                quiz.setQuizId(rs.getInt("quiz_id"));
                quiz.setTitle(rs.getString("quiz_title"));
                result.setQuiz(quiz);
                
                // Populate User
                com.quizapp.model.User student = new com.quizapp.model.User();
                student.setUserId(rs.getInt("user_id"));
                student.setFullName(rs.getString("student_name"));
                result.setUser(student);
                
                results.add(result);
            }
        } catch (SQLException e) {
            System.err.println("Error getting results by teacher: " + e.getMessage());
        }
        return results;
    }

    public double getAverageScoreByQuizId(int quizId) {
        String sql = "SELECT AVG(percentage) FROM results WHERE quiz_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting average score: " + e.getMessage());
        }
        return 0.0;
    }
    
    private Result extractResultFromResultSet(ResultSet rs) throws SQLException {
        Result result = new Result();
        result.setResultId(rs.getInt("result_id"));
        result.setTotalScore(rs.getDouble("total_score"));
        result.setPercentage(rs.getDouble("percentage"));
        result.setTimeTakenSeconds(rs.getInt("time_taken_seconds"));
        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        if (submittedAt != null) {
            result.setSubmittedAt(submittedAt.toLocalDateTime());
        }
        return result;
    }
}