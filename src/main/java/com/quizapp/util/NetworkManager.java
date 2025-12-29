package com.quizapp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.quizapp.networking.QuizSocketClient;
import com.quizapp.networking.SocketMessage;

public class NetworkManager {
    private static NetworkManager instance;
    private Map<String, QuizSocketClient> clients;
    private boolean socketServerRunning;
    
    private NetworkManager() {
        this.clients = new ConcurrentHashMap<>();
        this.socketServerRunning = false;
    }
    
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }
    
    public QuizSocketClient connectSocketClient(String username) {
        if (clients.containsKey(username)) {
            return clients.get(username);
        }
        
        QuizSocketClient client = new QuizSocketClient("localhost", 5555, username);
        if (client.isConnected()) {
            clients.put(username, client);
            System.out.println("✅ Socket client created for: " + username);
            return client;
        }
        return null;
    }
    
    public void disconnectSocketClient(String username) {
        QuizSocketClient client = clients.remove(username);
        if (client != null) {
            client.disconnect();
            System.out.println("👋 Socket client disconnected: " + username);
        }
    }
    
    public void sendQuizNotification(int quizId, String message) {
        // Send notification to all connected clients
        for (QuizSocketClient client : clients.values()) {
            if (client.isConnected()) {
                SocketMessage msg = new SocketMessage(
                    SocketMessage.MessageType.NOTIFICATION,
                    "System",
                    message
                );
                msg.setData(quizId);
                client.sendMessage(msg);
            }
        }
        
        // Also send via WebSocket
        com.quizapp.servlets.QuizWebSocketServlet.sendNotification(quizId, message);
    }
    
    public void broadcastQuizStart(int quizId, String quizTitle, String startedBy) {
        String message = String.format("Quiz '%s' has started by %s", quizTitle, startedBy);
        sendQuizNotification(quizId, message);
    }
    
    public void broadcastQuizCompletion(int quizId, String username, int score) {
        String message = String.format("%s completed the quiz with score: %d%%", username, score);
        sendQuizNotification(quizId, message);
    }
    
    public Map<String, QuizSocketClient> getConnectedClients() {
        return new HashMap<>(clients);
    }
    
    public int getTotalConnections() {
        return clients.size() + com.quizapp.servlets.QuizWebSocketServlet.getConnectedUsers();
    }
    
    public void setSocketServerRunning(boolean running) {
        this.socketServerRunning = running;
    }
    
    public boolean isSocketServerRunning() {
        return socketServerRunning;
    }
    
    public String getNetworkStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Network Status:\n");
        status.append("  Socket Server: ").append(socketServerRunning ? "✅ Running" : "❌ Stopped").append("\n");
        status.append("  Socket Clients: ").append(clients.size()).append("\n");
        status.append("  WebSocket Users: ").append(com.quizapp.servlets.QuizWebSocketServlet.getConnectedUsers()).append("\n");
        status.append("  Total Connections: ").append(getTotalConnections()).append("\n");
        
        Map<Integer, Integer> quizStats = com.quizapp.servlets.QuizWebSocketServlet.getQuizStats();
        if (!quizStats.isEmpty()) {
            status.append("  Active Quizzes:\n");
            for (Map.Entry<Integer, Integer> entry : quizStats.entrySet()) {
                status.append("    Quiz ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" users\n");
            }
        }
        
        return status.toString();
    }
}