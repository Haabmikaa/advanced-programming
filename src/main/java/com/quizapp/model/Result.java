package com.quizapp.model;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;



import java.time.LocalDateTime;

/**
 * Result entity representing the final result of a quiz attempt
 */
public class Result extends BaseEntity {
    private int resultId;
    private User user;
    private Quiz quiz;
    private QuizAttempt attempt;
    private double totalScore;
    private double percentage;
    private int timeTakenSeconds;
    private LocalDateTime submittedAt;
    
    // Constructors
    public Result() {
        super();
        this.submittedAt = LocalDateTime.now();
    }
    
    public Result(QuizAttempt attempt) {
        this();
        this.attempt = attempt;
        this.user = attempt.getUser();
        this.quiz = attempt.getQuiz();
        this.totalScore = attempt.getScore();
        this.percentage = attempt.getPercentage();
        this.timeTakenSeconds = (int) attempt.getTimeTakenSeconds();
    }
    
    // Getters and Setters
    public int getResultId() {
        return resultId;
    }
    
    public void setResultId(int resultId) {
        this.resultId = resultId;
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
    
    public QuizAttempt getAttempt() {
        return attempt;
    }
    
    public void setAttempt(QuizAttempt attempt) {
        this.attempt = attempt;
    }
    
    public double getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }
    
    public double getPercentage() {
        return percentage;
    }
    
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    
    public int getTimeTakenSeconds() {
        return timeTakenSeconds;
    }
    
    public void setTimeTakenSeconds(int timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }
    
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
    
    // Business logic methods
    public String getFormattedTimeTaken() {
        int hours = timeTakenSeconds / 3600;
        int minutes = (timeTakenSeconds % 3600) / 60;
        int seconds = timeTakenSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    public String getGrade() {
        if (percentage >= 90) return "A (Excellent)";
        else if (percentage >= 80) return "B (Very Good)";
        else if (percentage >= 70) return "C (Good)";
        else if (percentage >= 60) return "D (Satisfactory)";
        else return "F (Needs Improvement)";
    }
    
    public String getPerformanceLevel() {
        if (percentage >= 90) return "Outstanding";
        else if (percentage >= 80) return "Excellent";
        else if (percentage >= 70) return "Good";
        else if (percentage >= 60) return "Average";
        else if (percentage >= 50) return "Below Average";
        else return "Poor";
    }
    
    public boolean isPassing() {
        return percentage >= 60; // 60% is passing
    }
    
    public String getScoreOutOfTotal() {
    if (quiz != null) {
        return totalScore + " / " + quiz.getTotalPoints();
    }
    return String.valueOf(totalScore);
}

    
    public String getFormattedPercentage() {
        return String.format("%.2f%%", percentage);
    }
    
    // Analytics methods
    public double getTimePerQuestion() {
        if (quiz != null && quiz.getTotalQuestions() > 0) {
            return (double) timeTakenSeconds / quiz.getTotalQuestions();
        }
        return 0.0;
    }
    
    public double getAccuracy() {
        if (attempt != null && attempt.getTotalQuestions() > 0) {
            return (double) attempt.getCorrectAnswers() / attempt.getTotalQuestions() * 100;
        }
        return 0.0;
    }
    
    // Comparison methods
    public boolean isBetterThan(Result other) {
        return this.percentage > other.percentage;
    }
    
    public boolean isWorseThan(Result other) {
        return this.percentage < other.percentage;
    }
    
    public int getRankAmong(List<Result> allResults) {
        List<Result> sorted = allResults.stream()
            .sorted((r1, r2) -> Double.compare(r2.percentage, r1.percentage))
            .collect(Collectors.toList());

        
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).equals(this)) {
                return i + 1; // Rank is 1-based
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        return "Result{" +
                "resultId=" + resultId +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", quiz=" + (quiz != null ? quiz.getTitle() : "null") +
                ", totalScore=" + totalScore +
                ", percentage=" + percentage +
                ", grade=" + getGrade() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return resultId == result.resultId;
    }
    
    @Override
    public int hashCode() {
        return resultId;
    }
}