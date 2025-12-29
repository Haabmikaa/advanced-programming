package com.quizapp.util;

import java.rmi.Naming;
import com.quizapp.rmi.service.QuizRemoteService;

public class RMIServiceFactory {
    private static QuizRemoteService remoteService;
    private static final String RMI_URL = "rmi://localhost:1099/QuizRemoteService";
    
    public static synchronized QuizRemoteService getRemoteService() {
        if (remoteService == null) {
            try {
                remoteService = (QuizRemoteService) Naming.lookup(RMI_URL);
                System.out.println("✅ Connected to RMI Service at: " + RMI_URL);
            } catch (Exception e) {
                System.err.println("❌ Error connecting to RMI Service: " + e.getMessage());
                // Fallback to local service or throw exception
                throw new RuntimeException("RMI Service not available", e);
            }
        }
        return remoteService;
    }
    
    public static boolean isRMIAvailable() {
        try {
            getRemoteService();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String getServiceStatus() {
        try {
            return getRemoteService().getServerStatus();
        } catch (Exception e) {
            return "RMI Service Unavailable: " + e.getMessage();
        }
    }
}