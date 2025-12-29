package com.quizapp.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.quizapp.dao.QuestionDAO;
import com.quizapp.dao.QuizAttemptDAO;
import com.quizapp.dao.QuizDAO;
import com.quizapp.dao.ResultDAO;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.model.QuizAttempt;
import com.quizapp.model.Result;
import com.quizapp.model.User;

public class ResultServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        
        if ("review".equals(action)) {
            reviewQuiz(request, response);
            return;
        }
        
        ResultDAO resultDAO = new ResultDAO();
        List<Result> results = resultDAO.getByUserId(user.getUserId());
        request.setAttribute("results", results);
        request.getRequestDispatcher("/pages/student/dashboard.jsp").forward(request, response);
    }

    private void reviewQuiz(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int resultId = Integer.parseInt(request.getParameter("resultId"));
            ResultDAO resultDAO = new ResultDAO();
            Result result = resultDAO.getById(resultId);
            
            if (result == null) {
                response.sendRedirect(request.getContextPath() + "/pages/student/dashboard.jsp");
                return;
            }
            
            // Need to load quiz title if resultDAO.getById doesn't do it
            if (result.getQuiz() == null) {
                QuizDAO quizDAO = new QuizDAO();
                result.setQuiz(quizDAO.getById(result.getQuiz().getQuizId()));
            }

            com.quizapp.dao.UserAnswerDAO uaDAO = new com.quizapp.dao.UserAnswerDAO();
            // Assuming we can get attemptId from result
            // Let's check ResultDAO.getById implementation
            
            List<com.quizapp.model.UserAnswer> userAnswers = uaDAO.getByAttemptId(result.getAttempt().getAttemptId());
            
            request.setAttribute("result", result);
            request.setAttribute("userAnswers", userAnswers);
            request.getRequestDispatcher("/pages/student/view-questions.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/pages/student/dashboard.jsp");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
            return;
        }
        
        try {
            int quizId = Integer.parseInt(request.getParameter("quizId"));
            User user = (User) session.getAttribute("user");
            
            // 1. Fetch Quiz and Questions
            QuizDAO quizDAO = new QuizDAO();
            QuestionDAO questionDAO = new QuestionDAO();
            Quiz quiz = quizDAO.getById(quizId);
            List<Question> questions = questionDAO.getByQuizId(quizId);
            
            // 2. Calculate Score
            int correctCount = 0;
            int totalQuestions = questions.size();
            
            for (Question q : questions) {
                String studentAnswer = request.getParameter("question_" + q.getQuestionId());
                if (studentAnswer != null && studentAnswer.trim().equalsIgnoreCase(q.getCorrectAnswer().trim())) {
                    correctCount++;
                }
            }
            
            double percentage = (totalQuestions > 0) ? ((double) correctCount / totalQuestions) * 100 : 0;
            
            // 3. Create Quiz Attempt
            QuizAttemptDAO attemptDAO = new QuizAttemptDAO();
            QuizAttempt attempt = new QuizAttempt();
            attempt.setQuiz(quiz);
            attempt.setUser(user);
            attempt.setScore(percentage);
            attempt.setStatus(QuizAttempt.AttemptStatus.COMPLETED);
            attemptDAO.insert(attempt);
            
            // 4. Save Result
            ResultDAO resultDAO = new ResultDAO();
            Result result = new Result();
            result.setUser(user);
            result.setQuiz(quiz);
            result.setAttempt(attempt);
            result.setTotalScore((double) correctCount);
            result.setPercentage(percentage);
            result.setTimeTakenSeconds(0); // Optional: track time if needed
            result.setSubmittedAt(java.time.LocalDateTime.now());
            
            resultDAO.insert(result);

            // 5. Save Individual User Answers
            com.quizapp.dao.UserAnswerDAO uaDAO = new com.quizapp.dao.UserAnswerDAO();
            for (Question q : questions) {
                String studentAnswer = request.getParameter("question_" + q.getQuestionId());
                com.quizapp.model.UserAnswer ua = new com.quizapp.model.UserAnswer();
                ua.setAttempt(attempt);
                ua.setQuestion(q);
                ua.setUserAnswer(studentAnswer != null ? studentAnswer : "");
                ua.setCorrect(studentAnswer != null && studentAnswer.trim().equalsIgnoreCase(q.getCorrectAnswer().trim()));
                ua.setPointsEarned(ua.isCorrect() ? q.getPoints() : 0.0);
                ua.setAnsweredAt(java.time.LocalDateTime.now());
                uaDAO.insert(ua);
            }
            
            // 6. Redirect back to Student Dashboard
            response.sendRedirect(request.getContextPath() + "/pages/student/dashboard.jsp");
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error submitting quiz: " + e.getMessage());
            request.getRequestDispatcher("/pages/error/500.jsp").forward(request, response);
        }
    }
    
    private double calculateScore(HttpServletRequest request, int quizId) {
        return 0.0;
    }
}