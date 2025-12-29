package com.quizapp.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.quizapp.dao.QuestionDAO;
import com.quizapp.dao.QuizAttemptDAO;
import com.quizapp.dao.QuizDAO;
import com.quizapp.dao.ResultDAO;
import com.quizapp.dao.UserDAO;
import com.quizapp.model.Question;
import com.quizapp.model.Question.QuestionDTO;
import com.quizapp.model.Quiz;
import com.quizapp.model.QuizAttempt;
import com.quizapp.model.Result;
import com.quizapp.model.User;
import com.quizapp.rmi.service.QuizRemoteService;

public class QuizRemoteServiceImpl extends UnicastRemoteObject implements QuizRemoteService {
    
    private UserDAO userDAO;
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private QuizAttemptDAO attemptDAO;
    private ResultDAO resultDAO;
    private AtomicInteger activeConnections;
    
    public QuizRemoteServiceImpl() throws RemoteException {
        super();
        this.userDAO = new UserDAO();
        this.quizDAO = new QuizDAO();
        this.questionDAO = new QuestionDAO();
        this.attemptDAO = new QuizAttemptDAO();
        this.resultDAO = new ResultDAO();
        this.activeConnections = new AtomicInteger(0);
    }
    
    @Override
    public User authenticateUser(String username, String password) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            return userDAO.authenticate(username, password);
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public boolean registerUser(User user) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            return userDAO.insert(user);
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public List<Quiz> getAllPublishedQuizzes() throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            return quizDAO.getPublishedQuizzes();
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public Quiz getQuizById(int quizId) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            return quizDAO.getById(quizId);
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public List<Quiz> getQuizzesByCategory(String category) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            return quizDAO.getByCategory(category);
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public List<Quiz> getQuizzesByCreator(int userId) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            return quizDAO.getByCreator(userId);
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public List<QuestionDTO> getQuestionsForQuiz(int quizId) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            List<Question> questions = questionDAO.getByQuizId(quizId);
            List<QuestionDTO> dtos = new ArrayList<>();
            
            for (Question question : questions) {
                dtos.add(question.toDTO());
            }
            
            return dtos;
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public boolean validateAnswer(int questionId, String userAnswer) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            Question question = questionDAO.getById(questionId);
            return question != null && question.isCorrect(userAnswer);
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public int startQuizAttempt(int userId, int quizId) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            // Get user and quiz
            User user = userDAO.getById(userId);
            Quiz quiz = quizDAO.getById(quizId);
            
            if (user == null || quiz == null) {
                return -1;
            }
            
            // Check if user can attempt this quiz
            if (!quiz.canUserAttempt(user)) {
                return -1;
            }
            
            // Check for existing in-progress attempt
            QuizAttempt existing = attemptDAO.getActiveAttempt(userId, quizId);
            if (existing != null) {
                return existing.getAttemptId();
            }
            
            // Create new attempt
            QuizAttempt attempt = new QuizAttempt(user, quiz);
            if (attemptDAO.insert(attempt)) {
                return attempt.getAttemptId();
            }
            
            return -1;
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public boolean submitAnswer(int attemptId, int questionId, String answer) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            // Get the attempt and question
            QuizAttempt attempt = attemptDAO.getById(attemptId);
            Question question = questionDAO.getById(questionId);
            
            if (attempt == null || question == null) {
                return false;
            }
            
            // Check if attempt is still in progress
            if (!attempt.isInProgress()) {
                return false;
            }
            
            // For RMI, we need to create a UserAnswer and store it
            // This is simplified - in real app, you'd have UserAnswerDAO
            // For now, we'll update the attempt score directly
            
            boolean isCorrect = question.isCorrect(answer);
            double pointsEarned = isCorrect ? question.getPoints() : 0;
            
            // Update attempt score
            attempt.setScore(attempt.getScore() + pointsEarned);
            if (isCorrect) {
                attempt.setCorrectAnswers(attempt.getCorrectAnswers() + 1);
            }
            
            return attemptDAO.update(attempt);
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public Result completeQuizAttempt(int attemptId) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            QuizAttempt attempt = attemptDAO.getById(attemptId);
            if (attempt == null) {
                return null;
            }
            
            // Complete the attempt
            attempt.complete();
            attemptDAO.update(attempt);
            
            // Create result
            Result result = new Result(attempt);
            resultDAO.insert(result);
            
            return result;
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public List<Result> getUserResults(int userId) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            return resultDAO.getByUserId(userId);
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public Result getQuizResult(int attemptId) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            return resultDAO.getById(attemptId);
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public double getAverageScoreForQuiz(int quizId) throws RemoteException {
        activeConnections.incrementAndGet();
        try {
            return resultDAO.getAverageScoreByQuizId(quizId);
        } finally {
            activeConnections.decrementAndGet();
        }
    }
    
    @Override
    public String getServerStatus() throws RemoteException {
        return "RMI Server Status: Active | Connections: " + activeConnections.get() + 
               " | Time: " + LocalDateTime.now();
    }
    
    @Override
    public int getActiveConnections() throws RemoteException {
        return activeConnections.get();
    }
    
    @Override
    public boolean isServerAlive() throws RemoteException {
        return true;
    }
}