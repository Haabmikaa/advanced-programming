package com.quizapp.dao;

import com.quizapp.model.User;
import com.quizapp.model.Quiz;
import com.quizapp.model.Question;

public class DAOTest {
    public static void main(String[] args) {
        System.out.println("🧪 Testing DAO Classes");
        System.out.println("======================\n");
        
        // Test UserDAO
        testUserDAO();
        
        // Test QuizDAO
        testQuizDAO();
        
        // Test QuestionDAO
        testQuestionDAO();
        
        System.out.println("\n✅ DAO Tests Completed");
    }
    
    private static void testUserDAO() {
        System.out.println("Testing UserDAO:");
        UserDAO userDAO = new UserDAO();
        
        // Test count
        int userCount = userDAO.getCount();
        System.out.println("  Total users in DB: " + userCount);
        
        // Test get all users
        System.out.println("  All users:");
        for (User user : userDAO.getAll()) {
            System.out.println("    - " + user.getUsername() + " (" + user.getRole() + ")");
        }
        
        // Test authentication
        User authenticated = userDAO.authenticate("alice_student", "student123");
        if (authenticated != null) {
            System.out.println("  Authentication successful for: " + authenticated.getUsername());
        } else {
            System.out.println("  Authentication failed");
        }
    }
    
    private static void testQuizDAO() {
        System.out.println("\nTesting QuizDAO:");
        QuizDAO quizDAO = new QuizDAO();
        
        // Test count
        int quizCount = quizDAO.getCount();
        System.out.println("  Total quizzes in DB: " + quizCount);
        
        // Test get published quizzes
        System.out.println("  Published quizzes:");
        for (Quiz quiz : quizDAO.getPublishedQuizzes()) {
            System.out.println("    - " + quiz.getTitle() + " (" + quiz.getCategory() + ")");
        }
    }
    
    private static void testQuestionDAO() {
        System.out.println("\nTesting QuestionDAO:");
        QuestionDAO questionDAO = new QuestionDAO();
        
        // Test count
        int questionCount = questionDAO.getCount();
        System.out.println("  Total questions in DB: " + questionCount);
        
        // Test get questions by quiz
        System.out.println("  Questions for Quiz ID 1:");
        for (Question question : questionDAO.getByQuizId(1)) {
            System.out.println("    - " + question.getQuestionText().substring(0, 
                Math.min(50, question.getQuestionText().length())) + "...");
        }
    }
}