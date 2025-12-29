package com.quizapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity representing users of the system
 * Roles: STUDENT, TEACHER, ADMIN
 */
public class User extends BaseEntity {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private UserRole role;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLogin;
    private boolean isActive;
    
    // Relationships
    private List<Quiz> createdQuizzes = new ArrayList<>();
    private List<QuizAttempt> quizAttempts = new ArrayList<>();
    private List<Result> results = new ArrayList<>();
    private List<SocketSession> sessions = new ArrayList<>();
    
    public enum UserRole {
        STUDENT, TEACHER, ADMIN
    }
    
    // Constructors
    public User() {
        super();
        this.registrationDate = LocalDateTime.now();
        this.isActive = true;
        this.role = UserRole.STUDENT;
    }
    
    public User(String username, String password, String email, String fullName, UserRole role) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    // Relationship methods
    public List<Quiz> getCreatedQuizzes() {
        return createdQuizzes;
    }
    
    public void setCreatedQuizzes(List<Quiz> createdQuizzes) {
        this.createdQuizzes = createdQuizzes;
    }
    
    public void addCreatedQuiz(Quiz quiz) {
        this.createdQuizzes.add(quiz);
        quiz.setCreatedBy(this);
    }
    
    public List<QuizAttempt> getQuizAttempts() {
        return quizAttempts;
    }
    
    public void setQuizAttempts(List<QuizAttempt> quizAttempts) {
        this.quizAttempts = quizAttempts;
    }
    
    public List<Result> getResults() {
        return results;
    }
    
    public void setResults(List<Result> results) {
        this.results = results;
    }
    
    public List<SocketSession> getSessions() {
        return sessions;
    }
    
    public void setSessions(List<SocketSession> sessions) {
        this.sessions = sessions;
    }
    
    // Business logic methods
    public boolean isStudent() {
        return this.role == UserRole.STUDENT;
    }
    
    public boolean isTeacher() {
        return this.role == UserRole.TEACHER;
    }
    
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
    
    public double getAverageScore() {
        if (results.isEmpty()) return 0.0;
        return results.stream()
            .mapToDouble(Result::getPercentage)
            .average()
            .orElse(0.0);
    }
    
    public int getQuizzesTaken() {
        return (int) quizAttempts.stream()
            .filter(attempt -> attempt.getStatus() == QuizAttempt.AttemptStatus.COMPLETED)
            .count();
    }
    
    // Validation methods
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               email != null && email.contains("@") &&
               fullName != null && !fullName.trim().isEmpty();
    }
    
    // Security methods
    public void hashPassword() {
        // In a real application, use BCrypt or similar
        this.password = "hashed_" + this.password;
    }
    
    public boolean verifyPassword(String inputPassword) {
        // In a real application, use BCrypt password verification
        return this.password.equals("hashed_" + inputPassword);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", registrationDate=" + registrationDate +
                ", isActive=" + isActive +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && username.equals(user.username);
    }
    
    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + username.hashCode();
        return result;
    }
}