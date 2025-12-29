package com.quizapp.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quizapp.dao.QuestionDAO;
import com.quizapp.dao.QuizDAO;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.model.User;
import com.quizapp.util.GsonUtil;

public class QuizServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Check if user is authenticated
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
                return;
            }

            String action = request.getParameter("action");

            if ("details_json".equals(action)) {
                sendQuizDetailsJson(request, response);
            } else if ("questions_json".equals(action)) {
                sendQuizQuestionsJson(request, response);
            } else if ("list".equals(action)) {
                listQuizzes(request, response);
            } else if ("view".equals(action)) {
                viewQuiz(request, response);
            } else if ("take".equals(action)) {
                takeQuiz(request, response);
            } else if ("edit".equals(action)) {
                editQuiz(request, response);
            } else if ("create".equals(action)) {
                // Check for pre-fill parameters from the dashboard
                String preTitle = request.getParameter("pre_title");
                String preCategory = request.getParameter("pre_category");
                String preDuration = request.getParameter("pre_duration");
                String preDifficulty = request.getParameter("pre_difficulty");

                Quiz quiz = new Quiz();
                quiz.setTitle(preTitle != null ? preTitle : "");
                quiz.setCategory(preCategory != null ? preCategory : "");
                try {
                    quiz.setDurationMinutes(preDuration != null ? Integer.parseInt(preDuration) : 30);
                } catch (NumberFormatException e) {
                    quiz.setDurationMinutes(30);
                }
                if (preDifficulty != null) {
                    try {
                        quiz.setDifficulty(Quiz.DifficultyLevel.valueOf(preDifficulty));
                    } catch (Exception e) {
                        quiz.setDifficulty(Quiz.DifficultyLevel.MEDIUM);
                    }
                }
                
                request.setAttribute("quiz", quiz);
                request.setAttribute("isEdit", false);
                request.setAttribute("questionsJson", "[]"); // Always provide an empty array for new quizzes
                
                request.getRequestDispatcher("/pages/teacher/create-quiz-dynamic.jsp")
                        .forward(request, response);
            } else {
                listQuizzes(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    // ========================= JSON DETAILS =========================
    private void sendQuizDetailsJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int quizId = Integer.parseInt(request.getParameter("id"));

            QuizDAO quizDAO = new QuizDAO();
            QuestionDAO questionDAO = new QuestionDAO();

            Quiz quiz = quizDAO.getById(quizId);
            List<Question> questions = questionDAO.getByQuizId(quizId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            JsonObject obj = new JsonObject();
            obj.addProperty("quizId", quiz.getQuizId());
            obj.addProperty("title", quiz.getTitle());
            obj.addProperty("category", quiz.getCategory() != null ? quiz.getCategory().toString() : "-");
            obj.addProperty("durationMinutes", quiz.getDurationMinutes());
            obj.addProperty("description", quiz.getDescription());

            obj.addProperty("difficulty",
                    quiz.getDifficulty() != null ? quiz.getDifficulty().toString() : "-");


            obj.addProperty("questionCount", questions != null ? questions.size() : 0);

            response.getWriter().write(obj.toString());

        } catch (Exception e) {
            response.setStatus(400);
            response.getWriter().write("{}");
        }
    }

    private void sendQuizQuestionsJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int quizId = Integer.parseInt(request.getParameter("id"));
            QuestionDAO questionDAO = new QuestionDAO();
            List<Question> questions = questionDAO.getByQuizId(quizId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            JsonArray qArray = new JsonArray();
            for (Question q : questions) {
                JsonObject qObj = new JsonObject();
                qObj.addProperty("questionId", q.getQuestionId());
                qObj.addProperty("text", q.getQuestionText());
                qObj.addProperty("a", q.getOptionA());
                qObj.addProperty("b", q.getOptionB());
                qObj.addProperty("c", q.getOptionC());
                qObj.addProperty("d", q.getOptionD());
                qObj.addProperty("correct", q.getCorrectAnswer());
                qArray.add(qObj);
            }

            response.getWriter().write(qArray.toString());
        } catch (Exception e) {
            response.setStatus(400);
            response.getWriter().write("[]");
        }
    }

    // ========================= POST =========================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Check if user is authenticated
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"Session expired. Please log in again.\"}");
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if ("add_question".equals(action)) {
            handleAddQuestion(request, response);
            return;
        }

        if (!"saveFull".equals(action)) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"Invalid action\"}");
            return;
        }

        try {
            BufferedReader reader = request.getReader();
            StringBuilder jsonBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }

            JsonObject data = GsonUtil.getGson().fromJson(jsonBody.toString(), JsonObject.class);

            User teacher = (User) request.getSession().getAttribute("user");
            if (teacher == null) {
                response.setStatus(401);
                response.getWriter().write("{\"error\":\"Session expired. Please log in again.\"}");
                return;
            }

            QuizDAO quizDAO = new QuizDAO();
            QuestionDAO qDao = new QuestionDAO();
            Quiz quiz;
            
            Integer quizId = null;
            if (data.has("quizId") && !data.get("quizId").isJsonNull()) {
                quizId = data.get("quizId").getAsInt();
            }

            if (quizId != null) {
                // Update existing quiz
                quiz = quizDAO.getById(quizId);
                if (quiz == null || quiz.getCreatedBy().getUserId() != teacher.getUserId()) {
                    response.setStatus(403);
                    response.getWriter().write("{\"error\":\"Unauthorized or quiz not found\"}");
                    return;
                }
                quiz.setTitle(data.get("title").getAsString());
                quiz.setCategory(data.get("category").getAsString());
                quiz.setDurationMinutes(data.has("duration") ? data.get("duration").getAsInt() : 30);
                quiz.setDescription(data.has("description") ? data.get("description").getAsString() : quiz.getDescription());
                quizDAO.update(quiz);
                
                // Delete old questions to replace with new ones
                qDao.deleteByQuizId(quizId);
            } else {
                // Create new quiz
                quiz = new Quiz();
                quiz.setTitle(data.get("title").getAsString());
                quiz.setCategory(data.get("category").getAsString());
                quiz.setDurationMinutes(data.has("duration") ? data.get("duration").getAsInt() : 30);
                quiz.setDescription(data.has("description")
                        ? data.get("description").getAsString()
                        : "Quiz by " + teacher.getFullName());

                quiz.setDifficulty(Quiz.DifficultyLevel.MEDIUM);
                quiz.setCreatedBy(teacher);
                quiz.setPublished(true);
                quiz.setMaxAttempts(1);
                quizDAO.insert(quiz);
            }

            JsonArray qArray = data.getAsJsonArray("questions");

            int saved = 0, failed = 0;

            for (JsonElement el : qArray) {
                try {
                    JsonObject qObj = el.getAsJsonObject();

                    Question q = new Question();
                    q.setQuiz(quiz);
                    q.setQuestionText(qObj.get("text").getAsString());
                    q.setType(Question.QuestionType.MULTIPLE_CHOICE);
                    q.setPoints(1);

                    q.setOptionA(qObj.get("a").getAsString());
                    q.setOptionB(qObj.get("b").getAsString());
                    q.setOptionC(qObj.get("c").getAsString());
                    q.setOptionD(qObj.get("d").getAsString());
                    q.setCorrectAnswer(qObj.get("correct").getAsString());

                    if (qDao.insert(q)) saved++;
                    else failed++;

                } catch (Exception e) {
                    failed++;
                }
            }

            JsonObject result = new JsonObject();
            result.addProperty("success", true);
            result.addProperty("quizId", quiz.getQuizId());
            result.addProperty("questionsSaved", saved);
            result.addProperty("questionsFailed", failed);

            response.getWriter().write(result.toString());

        } catch (Exception e) {
            response.setStatus(500);
            JsonObject err = new JsonObject();
            err.addProperty("error", e.getMessage());
            response.getWriter().write(err.toString());
        }
    }

    private void handleAddQuestion(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            BufferedReader reader = request.getReader();
            JsonObject data = GsonUtil.getGson().fromJson(reader, JsonObject.class);

            int quizId = data.get("quizId").getAsInt();
            QuizDAO quizDAO = new QuizDAO();
            Quiz quiz = quizDAO.getById(quizId);

            if (quiz == null) {
                response.setStatus(404);
                response.getWriter().write("{\"error\":\"Quiz not found\"}");
                return;
            }

            Question q = new Question();
            q.setQuiz(quiz);
            q.setQuestionText(data.get("text").getAsString());
            q.setType(Question.QuestionType.MULTIPLE_CHOICE);
            q.setPoints(1);
            q.setOptionA(data.get("a").getAsString());
            q.setOptionB(data.get("b").getAsString());
            q.setOptionC(data.get("c").getAsString());
            q.setOptionD(data.get("d").getAsString());
            q.setCorrectAnswer(data.get("correct").getAsString());

            QuestionDAO qDao = new QuestionDAO();
            if (qDao.insert(q)) {
                JsonObject res = new JsonObject();
                res.addProperty("success", true);
                res.addProperty("questionId", q.getQuestionId());
                response.getWriter().write(res.toString());
            } else {
                response.setStatus(500);
                response.getWriter().write("{\"error\":\"Failed to save question\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ========================= HELPERS =========================
    private void listQuizzes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        QuizDAO quizDAO = new QuizDAO();
        request.setAttribute("quizzes", quizDAO.getPublishedQuizzes());
        request.getRequestDispatcher("/pages/quizzes.jsp").forward(request, response);
    }

    private void viewQuiz(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int quizId = Integer.parseInt(request.getParameter("id"));
        QuizDAO quizDAO = new QuizDAO();
        QuestionDAO questionDAO = new QuestionDAO();

        request.setAttribute("quiz", quizDAO.getById(quizId));
        request.setAttribute("questions", questionDAO.getByQuizId(quizId));
        request.getRequestDispatcher("/pages/quiz-details.jsp").forward(request, response);
    }

    private void editQuiz(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int quizId = Integer.parseInt(request.getParameter("id"));
            QuizDAO quizDAO = new QuizDAO();
            QuestionDAO questionDAO = new QuestionDAO();

            Quiz quiz = quizDAO.getById(quizId);
            List<Question> questions = questionDAO.getByQuizId(quizId);

            request.setAttribute("quiz", quiz);
            request.setAttribute("questions", questions);
            request.setAttribute("isEdit", true);
            
            // Convert questions to JSON string for the JSP
            String questionsJson = GsonUtil.getGson().toJson(questions);
            request.setAttribute("questionsJson", questionsJson);
                
            request.getRequestDispatcher("/pages/teacher/create-quiz-dynamic.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/quiz?action=create");
            }
    }

    private void takeQuiz(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/pages/student/dashboard.jsp");
            return;
        }

        try {
            int quizId = Integer.parseInt(idParam);
            QuizDAO quizDAO = new QuizDAO();
            QuestionDAO questionDAO = new QuestionDAO();

            request.setAttribute("quiz", quizDAO.getById(quizId));
            request.setAttribute("questions", questionDAO.getByQuizId(quizId));
            request.getRequestDispatcher("/pages/student/take-quiz.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/pages/student/dashboard.jsp");
        }
    }
}
