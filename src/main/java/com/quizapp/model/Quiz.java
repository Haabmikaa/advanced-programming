package com.quizapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz entity representing a quiz/test
 */
public class Quiz extends BaseEntity {
    private int quizId;
    private String title;
    private String description;
    private User createdBy;
    private String category;
    private DifficultyLevel difficulty;
    private int durationMinutes;
    private int maxAttempts;
    private boolean isPublished;
    
    // Relationships
    private List<Question> questions = new ArrayList<>();
    private List<QuizAttempt> attempts = new ArrayList<>();
    private List<Result> results = new ArrayList<>();
    
    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
    
    // Constructors
    public Quiz() {
        super();
        this.durationMinutes = 30;
        this.maxAttempts = 1;
        this.isPublished = false;
        this.difficulty = DifficultyLevel.MEDIUM;
    }
    
    public Quiz(String title, String description, User createdBy, String category, 
                DifficultyLevel difficulty, int durationMinutes) {
        this();
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.category = category;
        this.difficulty = difficulty;
        this.durationMinutes = durationMinutes;
    }
    
    // Getters and Setters
    public int getQuizId() {
        return quizId;
    }
    
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public DifficultyLevel getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public int getMaxAttempts() {
        return maxAttempts;
    }
    
    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
    
    public boolean isPublished() {
        return isPublished;
    }
    
    public void setPublished(boolean published) {
        isPublished = published;
    }
    
    // Relationship methods
    public List<Question> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
    
    public void addQuestion(Question question) {
        this.questions.add(question);
        question.setQuiz(this);
    }
    
    public List<QuizAttempt> getAttempts() {
        return attempts;
    }
    
    public void setAttempts(List<QuizAttempt> attempts) {
        this.attempts = attempts;
    }
    
    public List<Result> getResults() {
        return results;
    }
    
    public void setResults(List<Result> results) {
        this.results = results;
    }
    
    // Business logic methods
    public int getTotalQuestions() {
        return questions.size();
    }
    
    public int getTotalPoints() {
        return questions.stream()
            .mapToInt(Question::getPoints)
            .sum();
    }
    
    public double getAverageScore() {
        if (results.isEmpty()) return 0.0;
        return results.stream()
            .mapToDouble(Result::getPercentage)
            .average()
            .orElse(0.0);
    }
    
    public int getTotalAttempts() {
        return (int) attempts.stream()
            .filter(attempt -> attempt.getStatus() == QuizAttempt.AttemptStatus.COMPLETED)
            .count();
    }
    
    public int getUniqueUsersAttempted() {
        return (int) attempts.stream()
            .filter(attempt -> attempt.getStatus() == QuizAttempt.AttemptStatus.COMPLETED)
            .map(QuizAttempt::getUser)
            .distinct()
            .count();
    }
    
    public boolean canUserAttempt(User user) {
        if (!isPublished) return false;
        
        long userAttempts = attempts.stream()
            .filter(attempt -> attempt.getUser().equals(user) && 
                    attempt.getStatus() == QuizAttempt.AttemptStatus.COMPLETED)
            .count();
        
        return userAttempts < maxAttempts;
    }
    
    public List<Question> getShuffledQuestions() {
        List<Question> shuffled = new ArrayList<>(questions);
        java.util.Collections.shuffle(shuffled);
        return shuffled;
    }
    
    public Question getQuestionById(int questionId) {
        return questions.stream()
            .filter(q -> q.getQuestionId() == questionId)
            .findFirst()
            .orElse(null);
    }
    
    // Validation methods
    public boolean isValid() {
        return title != null && !title.trim().isEmpty() &&
               description != null && !description.trim().isEmpty() &&
               createdBy != null && createdBy.isTeacher() &&
               category != null && !category.trim().isEmpty() &&
               durationMinutes > 0 &&
               maxAttempts > 0;
    }
    
    @Override
    public String toString() {
        return "Quiz{" +
                "quizId=" + quizId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", difficulty=" + difficulty +
                ", durationMinutes=" + durationMinutes +
                ", isPublished=" + isPublished +
                ", totalQuestions=" + getTotalQuestions() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return quizId == quiz.quizId && title.equals(quiz.title);
    }
    
    @Override
    public int hashCode() {
        int result = quizId;
        result = 31 * result + title.hashCode();
        return result;
    }
}