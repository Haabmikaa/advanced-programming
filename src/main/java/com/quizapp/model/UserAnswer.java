package com.quizapp.model;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;



import java.time.LocalDateTime;

/**
 * UserAnswer entity representing a user's answer to a specific question
 */
public class UserAnswer extends BaseEntity {
    private int answerId;
    private QuizAttempt attempt;
    private Question question;
    private String userAnswer;
    private boolean isCorrect;
    private double pointsEarned;
    private LocalDateTime answeredAt;
    
    // Constructors
    public UserAnswer() {
        super();
        this.answeredAt = LocalDateTime.now();
        this.isCorrect = false;
        this.pointsEarned = 0.0;
    }
    
    public UserAnswer(QuizAttempt attempt, Question question, String userAnswer) {
        this();
        this.attempt = attempt;
        this.question = question;
        this.userAnswer = userAnswer;
        evaluateAnswer();
    }
    
    // Getters and Setters
    public int getAnswerId() {
        return answerId;
    }
    
    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }
    
    public QuizAttempt getAttempt() {
        return attempt;
    }
    
    public void setAttempt(QuizAttempt attempt) {
        this.attempt = attempt;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public String getUserAnswer() {
        return userAnswer;
    }
    
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
        evaluateAnswer();
    }
    
    public boolean isCorrect() {
        return isCorrect;
    }
    
    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
    
    public double getPointsEarned() {
        return pointsEarned;
    }
    
    public void setPointsEarned(double pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
    
    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }
    
    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
    }
    
    // Business logic methods
    private void evaluateAnswer() {
        if (question != null && userAnswer != null) {
            this.isCorrect = question.isCorrect(userAnswer);
            this.pointsEarned = this.isCorrect ? question.getPoints() : 0.0;
        }
    }
    
    public String getCorrectAnswer() {
        return question != null ? question.getCorrectAnswer() : "";
    }
    
    public boolean isAnswered() {
        return userAnswer != null && !userAnswer.trim().isEmpty();
    }
    
    public String getFormattedUserAnswer() {
        if (userAnswer == null) return "Not answered";
        
        if (question.isMultipleChoice() || question.isTrueFalse()) {
            // For MC/TF, show the selected option
            return userAnswer;
        } else {
            // For short answer, show first 50 chars
            return userAnswer.length() > 50 ? 
                   userAnswer.substring(0, 50) + "..." : userAnswer;
        }
    }
    
    public String getFormattedCorrectAnswer() {
        if (question == null) return "";
        
        if (question.isMultipleChoice()) {
            // Map answer to option letter
            String answer = question.getCorrectAnswer();
            List<String> options = question.getOptions();
            
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).equals(answer)) {
                    return String.valueOf((char) ('A' + i)) + ") " + answer;
                }
            }
            return answer;
        } else if (question.isTrueFalse()) {
            return question.getCorrectAnswer();
        } else {
            return question.getCorrectAnswer();
        }
    }
    
    @Override
    public String toString() {
        return "UserAnswer{" +
                "answerId=" + answerId +
                ", questionId=" + (question != null ? question.getQuestionId() : "null") +
                ", userAnswer='" + (userAnswer != null ? userAnswer.substring(0, Math.min(20, userAnswer.length())) : "null") + "..." + '\'' +
                ", isCorrect=" + isCorrect +
                ", pointsEarned=" + pointsEarned +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAnswer that = (UserAnswer) o;
        return answerId == that.answerId;
    }
    
    @Override
    public int hashCode() {
        return answerId;
    }
}