package com.quizapp.rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import com.quizapp.model.Question.QuestionDTO;
import com.quizapp.model.Quiz;
import com.quizapp.model.Result;
import com.quizapp.model.User;

public interface QuizRemoteService extends Remote {
    
    // User operations
    User authenticateUser(String username, String password) throws RemoteException;
    boolean registerUser(User user) throws RemoteException;
    
    // Quiz operations
    List<Quiz> getAllPublishedQuizzes() throws RemoteException;
    Quiz getQuizById(int quizId) throws RemoteException;
    List<Quiz> getQuizzesByCategory(String category) throws RemoteException;
    List<Quiz> getQuizzesByCreator(int userId) throws RemoteException;
    
    // Question operations
    List<QuestionDTO> getQuestionsForQuiz(int quizId) throws RemoteException;
    boolean validateAnswer(int questionId, String userAnswer) throws RemoteException;
    
    // Quiz attempt operations
    int startQuizAttempt(int userId, int quizId) throws RemoteException;
    boolean submitAnswer(int attemptId, int questionId, String answer) throws RemoteException;
    Result completeQuizAttempt(int attemptId) throws RemoteException;
    
    // Result operations
    List<Result> getUserResults(int userId) throws RemoteException;
    Result getQuizResult(int attemptId) throws RemoteException;
    double getAverageScoreForQuiz(int quizId) throws RemoteException;
    
    // System operations
    String getServerStatus() throws RemoteException;
    int getActiveConnections() throws RemoteException;
    boolean isServerAlive() throws RemoteException;
}