package com.quizapp.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class QuizSocketClient {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String username;
    private Consumer<SocketMessage> messageHandler;
    private boolean connected;
    
    public QuizSocketClient(String host, int port, String username) {
        this.username = username;
        try {
            socket = new Socket(host, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            connected = true;
            
            // Send connection message
            sendConnectMessage();
            
            // Start listener thread
            new Thread(this::listenForMessages).start();
            
        } catch (IOException e) {
            System.err.println("❌ Failed to connect to socket server: " + e.getMessage());
        }
    }
    
    private void sendConnectMessage() {
        SocketMessage connectMsg = new SocketMessage(
            SocketMessage.MessageType.CONNECT,
            username,
            "Connected to quiz server"
        );
        sendMessage(connectMsg);
    }
    
    private void listenForMessages() {
        try {
            while (connected) {
                SocketMessage message = (SocketMessage) input.readObject();
                if (messageHandler != null) {
                    messageHandler.accept(message);
                }
                System.out.println("📨 Received: " + message);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (connected) {
                System.err.println("Connection lost: " + e.getMessage());
                disconnect();
            }
        }
    }
    
    public void sendMessage(SocketMessage message) {
        if (connected && output != null) {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                System.err.println("Error sending message: " + e.getMessage());
            }
        }
    }
    
    public void sendQuizStart(int quizId) {
        SocketMessage message = new SocketMessage(
            SocketMessage.MessageType.QUIZ_START,
            username,
            "Started quiz " + quizId
        );
        message.setData(quizId);
        sendMessage(message);
    }
    
    public void sendAnswerSubmit(int quizId, int questionId, String answer) {
        SocketMessage message = new SocketMessage(
            SocketMessage.MessageType.ANSWER_SUBMIT,
            username,
            "Submitted answer for question " + questionId
        );
        Map<String, Object> data = new HashMap<>();
        data.put("quizId", quizId);
        data.put("questionId", questionId);
        data.put("answer", answer);
        message.setData(data);
        sendMessage(message);
    }
    
    public void sendQuizEnd(int quizId, int score) {
        SocketMessage message = new SocketMessage(
            SocketMessage.MessageType.QUIZ_END,
            username,
            "Completed quiz with score: " + score
        );
        message.setData(quizId);
        sendMessage(message);
    }
    
    public void sendTimeUpdate(int quizId, int timeRemaining) {
        SocketMessage message = new SocketMessage(
            SocketMessage.MessageType.TIME_UPDATE,
            username,
            "Time remaining: " + timeRemaining + "s"
        );
        message.setData(timeRemaining);
        sendMessage(message);
    }
    
    public void disconnect() {
        if (connected) {
            connected = false;
            SocketMessage disconnectMsg = new SocketMessage(
                SocketMessage.MessageType.DISCONNECT,
                username,
                "Disconnecting"
            );
            sendMessage(disconnectMsg);
            
            try {
                if (output != null) output.close();
                if (input != null) input.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
            
            System.out.println("👋 Disconnected from socket server");
        }
    }
    
    public void setMessageHandler(Consumer<SocketMessage> handler) {
        this.messageHandler = handler;
    }
    
    public boolean isConnected() { return connected; }
    public String getUsername() { return username; }
    
    public static void main(String[] args) {
        System.out.println("🔌 Testing Socket Client");
        System.out.println("=======================\n");
        
        // Test multiple clients
        QuizSocketClient client1 = new QuizSocketClient("localhost", 5555, "Alice");
        QuizSocketClient client2 = new QuizSocketClient("localhost", 5555, "Bob");
        
        client1.setMessageHandler(message -> {
            System.out.println("Client1 received: " + message.getContent());
        });
        
        client2.setMessageHandler(message -> {
            System.out.println("Client2 received: " + message.getContent());
        });
        
        // Wait for connection
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        if (client1.isConnected() && client2.isConnected()) {
            System.out.println("✅ Both clients connected");
            
            // Test quiz start
            client1.sendQuizStart(1);
            client2.sendQuizStart(1);
            
            // Test answer submission
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            client1.sendAnswerSubmit(1, 1, "A");
            client2.sendAnswerSubmit(1, 1, "B");
            
            // Test quiz end
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            client1.sendQuizEnd(1, 85);
            
            // Wait and disconnect
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            
            client1.disconnect();
            client2.disconnect();
            
            System.out.println("\n✅ Socket client test completed");
        } else {
            System.err.println("❌ Failed to connect clients");
        }
    }
}