package com.quizapp.networking;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizSocketServer {
    private static final int PORT = 5555;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private List<ClientHandler> clients;
    private Map<Integer, List<ClientHandler>> quizRooms;
    
    public QuizSocketServer() {
        this.clients = Collections.synchronizedList(new ArrayList<>());
        this.quizRooms = new ConcurrentHashMap<>();
        this.threadPool = Executors.newCachedThreadPool();
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("✅ Socket Server started on port " + PORT);
            System.out.println("👂 Waiting for clients...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔗 New client connected: " + clientSocket.getInetAddress());
                
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("❌ Server error: " + e.getMessage());
        }
    }
    
    public void broadcast(SocketMessage message, ClientHandler excludeClient) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != excludeClient && client.isConnected()) {
                    client.sendMessage(message);
                }
            }
        }
    }
    
    public void joinQuizRoom(int quizId, ClientHandler client) {
        quizRooms.computeIfAbsent(quizId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(client);
        System.out.println("📚 Client joined quiz room " + quizId + ": " + client.getUsername());
    }
    
    public void leaveQuizRoom(int quizId, ClientHandler client) {
        List<ClientHandler> room = quizRooms.get(quizId);
        if (room != null) {
            room.remove(client);
            System.out.println("🚪 Client left quiz room " + quizId + ": " + client.getUsername());
        }
    }
    
    public void broadcastToRoom(int quizId, SocketMessage message) {
        List<ClientHandler> room = quizRooms.get(quizId);
        if (room != null) {
            synchronized (room) {
                for (ClientHandler client : room) {
                    if (client.isConnected()) {
                        client.sendMessage(message);
                    }
                }
            }
        }
    }
    
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("👋 Client disconnected: " + client.getUsername());
        
        // Remove from all quiz rooms
        for (List<ClientHandler> room : quizRooms.values()) {
            room.remove(client);
        }
    }
    
    public int getConnectedClients() {
        return clients.size();
    }
    
    public Map<Integer, Integer> getQuizRoomStats() {
        Map<Integer, Integer> stats = new HashMap<>();
        for (Map.Entry<Integer, List<ClientHandler>> entry : quizRooms.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().size());
        }
        return stats;
    }
    
    public static void main(String[] args) {
        QuizSocketServer server = new QuizSocketServer();
        server.start();
    }
    
    // Client Handler inner class
    class ClientHandler implements Runnable {
        private Socket socket;
        private QuizSocketServer server;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private String username;
        private boolean connected;
        
        public ClientHandler(Socket socket, QuizSocketServer server) {
            this.socket = socket;
            this.server = server;
            this.connected = true;
        }
        
        @Override
        public void run() {
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());
                
                // Send welcome message
                sendMessage(new SocketMessage(SocketMessage.MessageType.CONNECT, 
                    "Server", "Welcome to Quiz Socket Server!"));
                
                // Handle client messages
                while (connected) {
                    try {
                        SocketMessage message = (SocketMessage) input.readObject();
                        handleMessage(message);
                    } catch (EOFException | SocketException e) {
                        break; // Client disconnected
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Client handler error: " + e.getMessage());
            } finally {
                disconnect();
            }
        }
        
        private void handleMessage(SocketMessage message) {
            switch (message.getType()) {
                case CONNECT:
                    this.username = message.getSender();
                    System.out.println("👤 User connected: " + username);
                    break;
                    
                case DISCONNECT:
                    disconnect();
                    break;
                    
                case QUIZ_START:
                    int quizId = (int) message.getData();
                    server.joinQuizRoom(quizId, this);
                    
                    // Notify others in the room
                    SocketMessage notification = new SocketMessage(
                        SocketMessage.MessageType.NOTIFICATION,
                        "System",
                        username + " started quiz " + quizId
                    );
                    server.broadcastToRoom(quizId, notification);
                    break;
                    
                case ANSWER_SUBMIT:
                    // Broadcast answer submission
                    server.broadcastToRoom((Integer) message.getData(), message);
                    break;
                    
                case QUIZ_END:
                    int endedQuizId = (int) message.getData();
                    server.leaveQuizRoom(endedQuizId, this);
                    
                    // Broadcast quiz completion
                    SocketMessage completionMsg = new SocketMessage(
                        SocketMessage.MessageType.NOTIFICATION,
                        "System",
                        username + " completed quiz " + endedQuizId
                    );
                    server.broadcast(completionMsg, this);
                    break;
                    
                default:
                    // Echo message back to all clients
                    server.broadcast(message, this);
            }
        }
        
        public void sendMessage(SocketMessage message) {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                System.err.println("Error sending message to " + username + ": " + e.getMessage());
            }
        }
        
        public void disconnect() {
            if (connected) {
                connected = false;
                server.removeClient(this);
                
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client connection: " + e.getMessage());
                }
            }
        }
        
        public boolean isConnected() { return connected; }
        public String getUsername() { return username != null ? username : "Unknown"; }
    }
}