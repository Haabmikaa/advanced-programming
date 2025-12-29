package com.quizapp.dao;

import com.quizapp.model.Question;
import com.quizapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO implements DAO<Question> {
    
    @Override
    public Question getById(int id) {
        Question question = null;
        String sql = "SELECT * FROM questions WHERE question_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                question = extractQuestionFromResultSet(rs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting question by ID: " + e.getMessage());
        }
        return question;
    }
    
    @Override
    public List<Question> getAll() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY question_id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                questions.add(extractQuestionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all questions: " + e.getMessage());
        }
        return questions;
    }
    
    @Override
    public boolean insert(Question question) {
        String sql = "INSERT INTO questions (quiz_id, question_text, question_type, points, " +
                    "option_a, option_b, option_c, option_d, correct_answer, explanation) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, question.getQuiz().getQuizId());
            pstmt.setString(2, question.getQuestionText());
            pstmt.setString(3, question.getType().name());
            pstmt.setInt(4, question.getPoints());
            pstmt.setString(5, question.getOptionA());
            pstmt.setString(6, question.getOptionB());
            pstmt.setString(7, question.getOptionC());
            pstmt.setString(8, question.getOptionD());
            pstmt.setString(9, question.getCorrectAnswer());
            pstmt.setString(10, question.getExplanation());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    question.setQuestionId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting question: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean update(Question question) {
        String sql = "UPDATE questions SET quiz_id=?, question_text=?, question_type=?, points=?, " +
                    "option_a=?, option_b=?, option_c=?, option_d=?, correct_answer=?, explanation=? " +
                    "WHERE question_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, question.getQuiz().getQuizId());
            pstmt.setString(2, question.getQuestionText());
            pstmt.setString(3, question.getType().name());
            pstmt.setInt(4, question.getPoints());
            pstmt.setString(5, question.getOptionA());
            pstmt.setString(6, question.getOptionB());
            pstmt.setString(7, question.getOptionC());
            pstmt.setString(8, question.getOptionD());
            pstmt.setString(9, question.getCorrectAnswer());
            pstmt.setString(10, question.getExplanation());
            pstmt.setInt(11, question.getQuestionId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating question: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM questions WHERE question_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting question: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM questions";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting questions: " + e.getMessage());
        }
        return 0;
    }
    
    // Additional methods
    public List<Question> getByQuizId(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                questions.add(extractQuestionFromResultSet(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting questions by quiz ID: " + e.getMessage());
        }
        return questions;
    }
    
    public int getQuestionCountByQuiz(int quizId) {
        String sql = "SELECT COUNT(*) FROM questions WHERE quiz_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error counting questions by quiz: " + e.getMessage());
        }
        return 0;
    }
    
    public void deleteByQuizId(int quizId) {
        String sql = "DELETE FROM questions WHERE quiz_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting questions by quiz: " + e.getMessage());
        }
    }

    private Question extractQuestionFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setQuestionId(rs.getInt("question_id"));
        question.setQuestionText(rs.getString("question_text"));
        question.setType(Question.QuestionType.valueOf(rs.getString("question_type")));
        question.setPoints(rs.getInt("points"));
        question.setOptionA(rs.getString("option_a"));
        question.setOptionB(rs.getString("option_b"));
        question.setOptionC(rs.getString("option_c"));
        question.setOptionD(rs.getString("option_d"));
        question.setCorrectAnswer(rs.getString("correct_answer"));
        question.setExplanation(rs.getString("explanation"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            question.setCreatedAt(createdAt.toLocalDateTime());
        }
        return question;
    }
}