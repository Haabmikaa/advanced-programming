package com.quizapp.model;

import java.time.LocalDateTime;

/**
 * Log entity for system logging and monitoring
 */
public class Log extends BaseEntity {
    private int logId;
    private LogLevel level;
    private String component;
    private String message;
    private User user;
    private String ipAddress;
    private LocalDateTime timestamp;
    
    public enum LogLevel {
        INFO, WARN, ERROR, DEBUG
    }
    
    // Constructors
    public Log() {
        super();
        this.level = LogLevel.INFO;
        this.timestamp = LocalDateTime.now();
    }
    
    public Log(LogLevel level, String component, String message) {
        this();
        this.level = level;
        this.component = component;
        this.message = message;
    }
    
    public Log(LogLevel level, String component, String message, User user) {
        this(level, component, message);
        this.user = user;
    }
    
    // Getters and Setters
    public int getLogId() {
        return logId;
    }
    
    public void setLogId(int logId) {
        this.logId = logId;
    }
    
    public LogLevel getLevel() {
        return level;
    }
    
    public void setLevel(LogLevel level) {
        this.level = level;
    }
    
    public String getComponent() {
        return component;
    }
    
    public void setComponent(String component) {
        this.component = component;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    // Business logic methods
    public String getFormattedTimestamp() {
        return timestamp.toString().replace("T", " ");
    }
    
    public String getColor() {
        switch (level) {
            case INFO: return "blue";
            case WARN: return "orange";
            case ERROR: return "red";
            case DEBUG: return "gray";
            default: return "black";
        }
    }
    
    public String getIcon() {
        switch (level) {
            case INFO: return "ℹ️";
            case WARN: return "⚠️";
            case ERROR: return "❌";
            case DEBUG: return "🐛";
            default: return "📝";
        }
    }
    
    public boolean isError() {
        return level == LogLevel.ERROR;
    }
    
    public boolean isWarning() {
        return level == LogLevel.WARN;
    }
    
    public boolean isInfo() {
        return level == LogLevel.INFO;
    }
    
    public boolean isDebug() {
        return level == LogLevel.DEBUG;
    }
    
    public String getFormattedMessage() {
        String userInfo = user != null ? 
            "User: " + user.getUsername() + " (" + user.getUserId() + ")" : 
            "User: System";
        
        return String.format("[%s] [%s] %s - %s", 
            getFormattedTimestamp(), 
            component, 
            userInfo, 
            message);
    }
    
    public String toCSV() {
        return String.join(",", 
            getFormattedTimestamp(),
            level.name(),
            component,
            user != null ? user.getUsername() : "SYSTEM",
            ipAddress != null ? ipAddress : "",
            "\"" + message.replace("\"", "\"\"") + "\""
        );
    }
    
    // Factory methods for common log types
    public static Log createUserLoginLog(User user, String ipAddress) {
        Log log = new Log(LogLevel.INFO, "AUTH", "User logged in", user);
        log.setIpAddress(ipAddress);
        return log;
    }
    
    public static Log createQuizAttemptLog(User user, Quiz quiz) {
        return new Log(LogLevel.INFO, "QUIZ", 
            "Started quiz: " + quiz.getTitle(), user);
    }
    
    public static Log createErrorLog(String component, String message, User user) {
        return new Log(LogLevel.ERROR, component, message, user);
    }
    
    public static Log createSystemStartupLog() {
        return new Log(LogLevel.INFO, "SYSTEM", 
            "Distributed Quiz System started successfully");
    }
    
    public static Log createRMIServerLog(RMIServer server, String message) {
        return new Log(LogLevel.INFO, "RMI", 
            "Server " + server.getServerName() + ": " + message);
    }
    
    @Override
    public String toString() {
        return "Log{" +
                "logId=" + logId +
                ", level=" + level +
                ", component='" + component + '\'' +
                ", message='" + message.substring(0, Math.min(50, message.length())) + "..." + '\'' +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", timestamp=" + getFormattedTimestamp() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return logId == log.logId;
    }
    
    @Override
    public int hashCode() {
        return logId;
    }
}