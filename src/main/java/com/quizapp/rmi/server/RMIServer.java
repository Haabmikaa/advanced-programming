package com.quizapp.rmi.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    private static final String SERVICE_NAME = "QuizRemoteService";
    private static final int RMI_PORT = 1099;
    
    public static void main(String[] args) {
        try {
            System.out.println("🚀 Starting RMI Server for Quiz Web Application");
            System.out.println("================================================");
            
            // Create RMI registry on port 1099
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            System.out.println("✅ RMI Registry created on port " + RMI_PORT);
            
            // Create remote service object
            QuizRemoteServiceImpl remoteService = new QuizRemoteServiceImpl();
            
            // Bind the remote object to the registry
            registry.rebind(SERVICE_NAME, remoteService);
            System.out.println("✅ QuizRemoteService bound to registry as: " + SERVICE_NAME);
            
            // Alternative binding using Naming
            Naming.rebind("rmi://localhost:" + RMI_PORT + "/" + SERVICE_NAME, remoteService);
            System.out.println("✅ Service available at: rmi://localhost:" + RMI_PORT + "/" + SERVICE_NAME);
            
            System.out.println("\n📊 RMI Server Information:");
            System.out.println("  Server Name: Quiz-RMI-Server");
            System.out.println("  Port: " + RMI_PORT);
            System.out.println("  Service: " + SERVICE_NAME);
            System.out.println("  Status: Running and ready to accept connections");
            
            System.out.println("\n👂 Server is listening for RMI requests...");
            System.out.println("Press Ctrl+C to stop the server.\n");
            
            // Keep server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("❌ RMI Server error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}