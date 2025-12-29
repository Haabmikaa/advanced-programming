package com.quizapp.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/quizws")
public class QuizWebSocketServlet {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Map<Integer, Set<String>> quizSessions = new ConcurrentHashMap<>();
    
    @OnOpen
    public void onOpen(Session session) {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        System.out.println("🔗 WebSocket connected: " + sessionId);
        
        try {
            session.getBasicRemote().sendText("{\"type\":\"connect\",\"message\":\"Connected to quiz server\"}");
        } catch (IOException e) {
            System.err.println("Error sending welcome message: " + e.getMessage());
        }
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("📨 Received from " + session.getId() + ": " + message);
        
        try {
            // Parse JSON message
            // Simple JSON parsing (in real app, use Gson/Jackson)
            if (message.contains("\"type\":\"force_stop\"")) {
    int quizId = extractQuizId(message);
    // Tell everyone in this quiz room to stop!
    broadcastToQuiz(quizId, "{\"type\":\"terminate_quiz\",\"message\":\"Time is up! Quiz ended by Teacher.\"}", null);
} else if (message.contains("\"type\":\"answer\"")) {
                int quizId = extractQuizId(message);
                broadcastToQuiz(quizId, message, session.getId());
                
            } else if (message.contains("\"type\":\"chat\"")) {
                // Broadcast chat message to all in quiz
                int quizId = extractQuizId(message);
                broadcastToQuiz(quizId, message, session.getId());
            }
            
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            try {
                session.getBasicRemote().sendText("{\"type\":\"error\",\"message\":\"Invalid message format\"}");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @OnClose
    public void onClose(Session session) {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        
        // Remove from all quiz sessions
        for (Set<String> quizSessionSet : quizSessions.values()) {
            quizSessionSet.remove(sessionId);
        }
        
        System.out.println("👋 WebSocket disconnected: " + sessionId);
    }
    
    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("❌ WebSocket error for " + session.getId() + ": " + error.getMessage());
    }
    
    private void joinQuiz(int quizId, String sessionId) {
        quizSessions.computeIfAbsent(quizId, k -> ConcurrentHashMap.newKeySet())
                   .add(sessionId);
        System.out.println("📚 Session " + sessionId + " joined quiz " + quizId);
    }
    
    private void broadcastToQuiz(int quizId, String message, String excludeSessionId) {
        Set<String> quizSessionSet = quizSessions.get(quizId);
        if (quizSessionSet != null) {
            for (String sessionId : quizSessionSet) {
                if (!sessionId.equals(excludeSessionId)) {
                    Session session = sessions.get(sessionId);
                    if (session != null && session.isOpen()) {
                        try {
                            session.getBasicRemote().sendText(message);
                        } catch (IOException e) {
                            System.err.println("Error sending to " + sessionId + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
    
    private int extractQuizId(String message) {
        try {
            // Simple extraction - in real app, use proper JSON parser
            int start = message.indexOf("\"quizId\":") + 9;
            int end = message.indexOf(",", start);
            if (end == -1) end = message.indexOf("}", start);
            String quizIdStr = message.substring(start, end).trim();
            return Integer.parseInt(quizIdStr.replace("\"", ""));
        } catch (Exception e) {
            return -1;
        }
    }
    
    // Static method to send notifications from servlets
    public static void sendNotification(int quizId, String message) {
        Set<String> quizSessionSet = quizSessions.get(quizId);
        if (quizSessionSet != null) {
            String jsonMessage = "{\"type\":\"notification\",\"message\":\"" + 
                               message.replace("\"", "\\\"") + "\"}";
            
            for (String sessionId : quizSessionSet) {
                Session session = sessions.get(sessionId);
                if (session != null && session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(jsonMessage);
                    } catch (IOException e) {
                        System.err.println("Error sending notification: " + e.getMessage());
                    }
                }
            }
        }
    }
    
    public static int getConnectedUsers() {
        return sessions.size();
    }
    
    public static Map<Integer, Integer> getQuizStats() {
        Map<Integer, Integer> stats = new HashMap<>();
        for (Map.Entry<Integer, Set<String>> entry : quizSessions.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        return stats;
    }
}