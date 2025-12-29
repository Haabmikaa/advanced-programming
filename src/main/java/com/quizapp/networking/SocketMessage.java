package com.quizapp.networking;

import java.io.Serializable;

public class SocketMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private MessageType type;
    private String sender;
    private String content;
    private String timestamp;
    private Object data;
    
    public enum MessageType {
        CONNECT, DISCONNECT, QUIZ_START, QUIZ_END, ANSWER_SUBMIT, 
        TIME_UPDATE, SCORE_UPDATE, NOTIFICATION, ERROR
    }
    
    public SocketMessage() {}
    
    public SocketMessage(MessageType type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }
    
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp, sender, content);
    }
}