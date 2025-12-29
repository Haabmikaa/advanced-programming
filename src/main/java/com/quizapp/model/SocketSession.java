package com.quizapp.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * SocketSession entity representing a real-time socket connection
 */
public class SocketSession extends BaseEntity {
    private String sessionId;
    private User user;
    private Quiz quiz;
    private QuizAttempt attempt;
    private String socketAddress;
    private LocalDateTime connectedAt;
    private LocalDateTime disconnectedAt;
    private SessionStatus status;
    
    public enum SessionStatus {
        CONNECTED, DISCONNECTED, TIMEOUT
    }
    
    // Constructors
    public SocketSession() {
        super();
        this.sessionId = generateSessionId();
        this.connectedAt = LocalDateTime.now();
        this.status = SessionStatus.CONNECTED;
    }
    
    public SocketSession(User user) {
        this();
        this.user = user;
    }
    
    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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
    
    public String getSocketAddress() {
        return socketAddress;
    }
    
    public void setSocketAddress(String socketAddress) {
        this.socketAddress = socketAddress;
    }
    
    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }
    
    public void setConnectedAt(LocalDateTime connectedAt) {
        this.connectedAt = connectedAt;
    }
    
    public LocalDateTime getDisconnectedAt() {
        return disconnectedAt;
    }
    
    public void setDisconnectedAt(LocalDateTime disconnectedAt) {
        this.disconnectedAt = disconnectedAt;
    }
    
    public SessionStatus getStatus() {
        return status;
    }
    
    public void setStatus(SessionStatus status) {
        this.status = status;
    }
    
    // Business logic methods
    private String generateSessionId() {
        return "SESSION_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 1000);
    }
    
    public boolean isConnected() {
        return status == SessionStatus.CONNECTED;
    }
    
    public void disconnect() {
        this.disconnectedAt = LocalDateTime.now();
        this.status = SessionStatus.DISCONNECTED;
    }
    
    public void timeout() {
        this.disconnectedAt = LocalDateTime.now();
        this.status = SessionStatus.TIMEOUT;
    }
    
    public long getDurationSeconds() {
        LocalDateTime endTime = isConnected() ? LocalDateTime.now() : disconnectedAt;
        if (connectedAt == null || endTime == null) return 0;
        
        return Duration.between(connectedAt, endTime).getSeconds();
    }
    
    public String getFormattedDuration() {
        long seconds = getDurationSeconds();
        
        if (seconds < 60) {
            return seconds + " seconds";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return minutes + " min " + remainingSeconds + " sec";
        } else {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + " hr " + minutes + " min";
        }
    }
    
    public boolean isActive() {
        return isConnected() && 
               (connectedAt.isAfter(LocalDateTime.now().minusMinutes(5)) || 
                (disconnectedAt != null && disconnectedAt.isAfter(LocalDateTime.now().minusMinutes(5))));
    }
    
    public boolean isExpired(int timeoutMinutes) {
        if (isConnected()) {
            LocalDateTime timeoutTime = connectedAt.plusMinutes(timeoutMinutes);
            return LocalDateTime.now().isAfter(timeoutTime);
        }
        return false;
    }
    
    public void updateActivity() {
        if (isConnected()) {
            // Update last activity timestamp
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    // Validation methods
    public boolean isValid() {
        return sessionId != null && !sessionId.trim().isEmpty() &&
               user != null && connectedAt != null;
    }
    
    @Override
    public String toString() {
        return "SocketSession{" +
                "sessionId='" + sessionId + '\'' +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", status=" + status +
                ", duration=" + getFormattedDuration() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocketSession that = (SocketSession) o;
        return sessionId.equals(that.sessionId);
    }
    
    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }
}