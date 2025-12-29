package com.quizapp.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * QuizAttempt entity representing a user's attempt at a quiz
 */
public class QuizAttempt extends BaseEntity {
    private int attemptId;
    private User user;
    private Quiz quiz;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double score;
    private int totalQuestions;
    private int correctAnswers;
    private AttemptStatus status;
    private String ipAddress;
    private String userAgent;
    
    // Relationships
    private List<UserAnswer> userAnswers = new ArrayList<>();
    private Result result;
    
    public enum AttemptStatus {
        IN_PROGRESS, COMPLETED, TIMEOUT
    }
    
    // Constructors
    public QuizAttempt() {
        super();
        this.startTime = LocalDateTime.now();
        this.status = AttemptStatus.IN_PROGRESS;
        this.score = 0.0;
        this.totalQuestions = 0;
        this.correctAnswers = 0;
    }
    
    public QuizAttempt(User user, Quiz quiz) {
        this();
        this.user = user;
        this.quiz = quiz;
        this.totalQuestions = quiz.getTotalQuestions();
    }
    
    // Getters and Setters
    public int getAttemptId() {
        return attemptId;
    }
    
    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Quiz getQuiz() {
        return quiz;
    }
    
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    
    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
    
    public AttemptStatus getStatus() {
        return status;
    }
    
    public void setStatus(AttemptStatus status) {
        this.status = status;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    // Relationship methods
    public List<UserAnswer> getUserAnswers() {
        return userAnswers;
    }
    
    public void setUserAnswers(List<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }
    
    public void addUserAnswer(UserAnswer userAnswer) {
        this.userAnswers.add(userAnswer);
        userAnswer.setAttempt(this);
    }
    
    public Result getResult() {
        return result;
    }
    
    public void setResult(Result result) {
        this.result = result;
    }
    
    // Business logic methods
    public double getPercentage() {
        if (totalQuestions == 0) return 0.0;
        return (score / (quiz.getTotalPoints())) * 100;
    }
    
    public long getTimeTakenSeconds() {
        if (endTime == null) return 0;
        return Duration.between(startTime, endTime).getSeconds();
    }
    
    public boolean isCompleted() {
        return status == AttemptStatus.COMPLETED;
    }
    
    public boolean isInProgress() {
        return status == AttemptStatus.IN_PROGRESS;
    }
    
    public boolean isTimedOut() {
        return status == AttemptStatus.TIMEOUT;
    }
    
    public boolean hasTimeRemaining() {
        if (endTime != null) return false;
        
        Duration elapsed = Duration.between(startTime, LocalDateTime.now());
        Duration totalTime = Duration.ofMinutes(quiz.getDurationMinutes());
        
        return elapsed.compareTo(totalTime) < 0;
    }
    
    public long getTimeRemainingSeconds() {
        if (!hasTimeRemaining()) return 0;
        
        Duration elapsed = Duration.between(startTime, LocalDateTime.now());
        Duration totalTime = Duration.ofMinutes(quiz.getDurationMinutes());
        
        return totalTime.minus(elapsed).getSeconds();
    }
    
    public void complete() {
        if (this.endTime == null) {
            this.endTime = LocalDateTime.now();
        }
        this.status = AttemptStatus.COMPLETED;
        calculateScore();
    }
    
    public void timeout() {
        this.endTime = LocalDateTime.now();
        this.status = AttemptStatus.TIMEOUT;
        calculateScore();
    }
    
    private void calculateScore() {
        double calculatedScore = 0.0;
        int correctCount = 0;
        
        for (UserAnswer userAnswer : userAnswers) {
            if (userAnswer.isCorrect()) {
                calculatedScore += userAnswer.getPointsEarned();
                correctCount++;
            }
        }
        
        this.score = calculatedScore;
        this.correctAnswers = correctCount;
    }
    
    public UserAnswer getUserAnswerForQuestion(Question question) {
        return userAnswers.stream()
            .filter(answer -> answer.getQuestion().getQuestionId() == question.getQuestionId())
            .findFirst()
            .orElse(null);
    }
    
    public boolean hasAnsweredAllQuestions() {
        return userAnswers.size() >= totalQuestions;
    }
    
    public int getQuestionsAnswered() {
        return userAnswers.size();
    }
    
    public int getQuestionsRemaining() {
        return totalQuestions - getQuestionsAnswered();
    }
    
    public String getGrade() {
        double percentage = getPercentage();
        
        if (percentage >= 90) return "A";
        else if (percentage >= 80) return "B";
        else if (percentage >= 70) return "C";
        else if (percentage >= 60) return "D";
        else return "F";
    }
    
    // Validation methods
    public boolean isValid() {
        return user != null && quiz != null && startTime != null;
    }
    
    @Override
    public String toString() {
        return "QuizAttempt{" +
                "attemptId=" + attemptId +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", quiz=" + (quiz != null ? quiz.getTitle() : "null") +
                ", score=" + score +
                ", percentage=" + getPercentage() +
                ", status=" + status +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuizAttempt that = (QuizAttempt) o;
        return attemptId == that.attemptId;
    }
    
    @Override
    public int hashCode() {
        return attemptId;
    }
}