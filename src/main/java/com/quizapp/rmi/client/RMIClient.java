package com.quizapp.rmi.client;

import java.rmi.Naming;
import java.util.List;

import com.quizapp.model.Question.QuestionDTO;
import com.quizapp.model.Quiz;
import com.quizapp.model.Result;
import com.quizapp.model.User;
import com.quizapp.rmi.service.QuizRemoteService;

public class RMIClient {
    private static final String RMI_URL = "rmi://localhost:1099/QuizRemoteService";
    private QuizRemoteService remoteService;
    
    public RMIClient() {
        try {
            // Look up the remote service
            remoteService = (QuizRemoteService) Naming.lookup(RMI_URL);
            System.out.println("✅ Connected to RMI Server at: " + RMI_URL);
        } catch (Exception e) {
            System.err.println("❌ Error connecting to RMI Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean isConnected() {
        return remoteService != null;
    }
    
    public User authenticate(String username, String password) {
        try {
            return remoteService.authenticateUser(username, password);
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return null;
        }
    }
    
    public List<Quiz> getPublishedQuizzes() {
        try {
            return remoteService.getAllPublishedQuizzes();
        } catch (Exception e) {
            System.err.println("Error getting quizzes: " + e.getMessage());
            return null;
        }
    }
    
    public List<QuestionDTO> getQuizQuestions(int quizId) {
        try {
            return remoteService.getQuestionsForQuiz(quizId);
        } catch (Exception e) {
            System.err.println("Error getting questions: " + e.getMessage());
            return null;
        }
    }
    
    public int startQuiz(int userId, int quizId) {
        try {
            return remoteService.startQuizAttempt(userId, quizId);
        } catch (Exception e) {
            System.err.println("Error starting quiz: " + e.getMessage());
            return -1;
        }
    }
    
    public boolean submitAnswer(int attemptId, int questionId, String answer) {
        try {
            return remoteService.submitAnswer(attemptId, questionId, answer);
        } catch (Exception e) {
            System.err.println("Error submitting answer: " + e.getMessage());
            return false;
        }
    }
    
    public Result completeQuiz(int attemptId) {
        try {
            return remoteService.completeQuizAttempt(attemptId);
        } catch (Exception e) {
            System.err.println("Error completing quiz: " + e.getMessage());
            return null;
        }
    }
    
    public List<Result> getUserResults(int userId) {
        try {
            return remoteService.getUserResults(userId);
        } catch (Exception e) {
            System.err.println("Error getting results: " + e.getMessage());
            return null;
        }
    }
    
    public String getServerStatus() {
        try {
            return remoteService.getServerStatus();
        } catch (Exception e) {
            return "Server not available: " + e.getMessage();
        }
    }
    
    // Test method
    public static void main(String[] args) {
        System.out.println("🧪 Testing RMI Client Connection");
        System.out.println("================================\n");
        
        RMIClient client = new RMIClient();
        
        if (!client.isConnected()) {
            System.err.println("❌ Failed to connect to RMI Server");
            System.err.println("Make sure RMI Server is running on localhost:1099");
            return;
        }
        
        // Test server status
        System.out.println("Server Status: " + client.getServerStatus());
        
        // Test authentication
        System.out.println("\n🔐 Testing Authentication:");
        User user = client.authenticate("alice_student", "student123");
        if (user != null) {
            System.out.println("✅ Authenticated as: " + user.getUsername());
            System.out.println("   Role: " + user.getRole());
            System.out.println("   Name: " + user.getFullName());
        } else {
            System.out.println("❌ Authentication failed");
        }
        
        // Test getting quizzes
        System.out.println("\n📝 Testing Quiz Retrieval:");
        List<Quiz> quizzes = client.getPublishedQuizzes();
        if (quizzes != null && !quizzes.isEmpty()) {
            System.out.println("✅ Found " + quizzes.size() + " published quizzes:");
            for (Quiz quiz : quizzes) {
                System.out.println("   - " + quiz.getTitle() + " (" + quiz.getCategory() + ")");
            }
            
            // Test getting questions for first quiz
            if (!quizzes.isEmpty()) {
                int quizId = quizzes.get(0).getQuizId();
                System.out.println("\n❓ Testing Question Retrieval for Quiz ID " + quizId + ":");
                List<QuestionDTO> questions = client.getQuizQuestions(quizId);
                if (questions != null && !questions.isEmpty()) {
                    System.out.println("✅ Found " + questions.size() + " questions");
                    for (int i = 0; i < Math.min(3, questions.size()); i++) {
                        QuestionDTO q = questions.get(i);
                        System.out.println("   " + (i+1) + ". " + 
                            q.getQuestionText().substring(0, Math.min(50, q.getQuestionText().length())) + "...");
                    }
                }
                
                // Test starting a quiz attempt
                if (user != null) {
                    System.out.println("\n🎯 Testing Quiz Attempt:");
                    int attemptId = client.startQuiz(user.getUserId(), quizId);
                    if (attemptId > 0) {
                        System.out.println("✅ Started quiz attempt with ID: " + attemptId);
                        
                        // Test submitting an answer
                        if (questions != null && !questions.isEmpty()) {
                            int questionId = questions.get(0).getQuestionId();
                            boolean submitted = client.submitAnswer(attemptId, questionId, "A");
                            System.out.println("✅ Submitted answer: " + submitted);
                        }
                        
                        // Test completing the attempt
                        Result result = client.completeQuiz(attemptId);
                        if (result != null) {
                            System.out.println("✅ Quiz completed!");
                            System.out.println("   Score: " + result.getTotalScore());
                            System.out.println("   Percentage: " + result.getPercentage() + "%");
                            System.out.println("   Grade: " + result.getGrade());
                        }
                    }
                }
            }
        }
        
        // Test getting user results
        if (user != null) {
            System.out.println("\n📊 Testing Results Retrieval:");
            List<Result> results = client.getUserResults(user.getUserId());
            if (results != null && !results.isEmpty()) {
                System.out.println("✅ Found " + results.size() + " results for user");
            } else {
                System.out.println("No results found for user");
            }
        }
        
        System.out.println("\n✅ RMI Client Test Completed Successfully!");
    }
}