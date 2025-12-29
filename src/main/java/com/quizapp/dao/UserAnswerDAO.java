package com.quizapp.dao;

import com.quizapp.model.UserAnswer;
import com.quizapp.model.Question;
import com.quizapp.model.QuizAttempt;
import com.quizapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAnswerDAO implements DAO<UserAnswer> {
    
    @Override
    public UserAnswer getById(int id) {
        UserAnswer answer = null;
        String sql = "SELECT * FROM user_answers WHERE answer_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                answer = extractUserAnswerFromResultSet(rs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting user answer by ID: " + e.getMessage());
        }
        return answer;
    }
    
    @Override
    public List<UserAnswer> getAll() {
        List<UserAnswer> answers = new ArrayList<>();
        String sql = "SELECT * FROM user_answers";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                answers.add(extractUserAnswerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all user answers: " + e.getMessage());
        }
        return answers;
    }
    
    @Override
    public boolean insert(UserAnswer answer) {
        String sql = "INSERT INTO user_answers (attempt_id, question_id, user_answer, is_correct, points_earned, answered_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, answer.getAttempt().getAttemptId());
            pstmt.setInt(2, answer.getQuestion().getQuestionId());
            pstmt.setString(3, answer.getUserAnswer());
            pstmt.setBoolean(4, answer.isCorrect());
            pstmt.setDouble(5, answer.getPointsEarned());
            pstmt.setTimestamp(6, Timestamp.valueOf(answer.getAnsweredAt()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    answer.setAnswerId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user answer: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean update(UserAnswer answer) {
        String sql = "UPDATE user_answers SET attempt_id=?, question_id=?, user_answer=?, is_correct=?, points_earned=?, answered_at=? " +
                    "WHERE answer_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, answer.getAttempt().getAttemptId());
            pstmt.setInt(2, answer.getQuestion().getQuestionId());
            pstmt.setString(3, answer.getUserAnswer());
            pstmt.setBoolean(4, answer.isCorrect());
            pstmt.setDouble(5, answer.getPointsEarned());
            pstmt.setTimestamp(6, Timestamp.valueOf(answer.getAnsweredAt()));
            pstmt.setInt(7, answer.getAnswerId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user answer: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM user_answers WHERE answer_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user answer: " + e.getMessage());
        }
        return false;
    }
    
    @Override
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM user_answers";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting user answers: " + e.getMessage());
        }
        return 0;
    }
    
    public List<UserAnswer> getByAttemptId(int attemptId) {
        List<UserAnswer> answers = new ArrayList<>();
        String sql = "SELECT ua.*, q.question_text, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_answer, q.question_type " +
                    "FROM user_answers ua " +
                    "JOIN questions q ON ua.question_id = q.question_id " +
                    "WHERE ua.attempt_id = ? ORDER BY ua.question_id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, attemptId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                UserAnswer ua = extractUserAnswerFromResultSet(rs);
                
                // Populate Question details for review
                Question q = new Question();
                q.setQuestionId(rs.getInt("question_id"));
                q.setQuestionText(rs.getString("question_text"));
                q.setOptionA(rs.getString("option_a"));
                q.setOptionB(rs.getString("option_b"));
                q.setOptionC(rs.getString("option_c"));
                q.setOptionD(rs.getString("option_d"));
                q.setCorrectAnswer(rs.getString("correct_answer"));
                q.setType(Question.QuestionType.valueOf(rs.getString("question_type")));
                ua.setQuestion(q);
                
                answers.add(ua);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting user answers by attempt: " + e.getMessage());
        }
        return answers;
    }
    
    private UserAnswer extractUserAnswerFromResultSet(ResultSet rs) throws SQLException {
        UserAnswer answer = new UserAnswer();
        answer.setAnswerId(rs.getInt("answer_id"));
        answer.setUserAnswer(rs.getString("user_answer"));
        answer.setCorrect(rs.getBoolean("is_correct"));
        answer.setPointsEarned(rs.getDouble("points_earned"));
        Timestamp answeredAt = rs.getTimestamp("answered_at");
        if (answeredAt != null) {
            answer.setAnsweredAt(answeredAt.toLocalDateTime());
        }
        return answer;
    }
}
