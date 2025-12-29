package com.quizapp.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test class to validate all model classes work correctly
 */
public class ModelTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Testing Model Classes for Quiz Web Application");
        System.out.println("===================================================\n");
        
        // Test User Model
        testUserModel();
        
        // Test Quiz Model
        testQuizModel();
        
        // Test Question Model
        testQuestionModel();
        
        // Test Relationship between models
        testModelRelationships();
        
        // Test Business Logic
        testBusinessLogic();
        
        System.out.println("\n✅ All model tests completed successfully!");
    }
    
    private static void testUserModel() {
        System.out.println("👤 Testing User Model:");
        
        User student = new User("john_doe", "password123", "john@email.com", "John Doe", User.UserRole.STUDENT);
        User teacher = new User("prof_smith", "password123", "smith@university.edu", "Prof. Smith", User.UserRole.TEACHER);
        User admin = new User("admin", "admin123", "admin@quizapp.com", "System Admin", User.UserRole.ADMIN);
        
        System.out.println("  Student: " + student);
        System.out.println("  Teacher: " + teacher);
        System.out.println("  Admin: " + admin);
        
        // Test validation
        System.out.println("  Student valid? " + student.isValid());
        System.out.println("  Student role: " + student.getRole());
        System.out.println("  Is student? " + student.isStudent());
        System.out.println("  Is teacher? " + teacher.isTeacher());
        System.out.println("  Is admin? " + admin.isAdmin());
        
        // Test password hashing
        student.hashPassword();
        System.out.println("  Hashed password: " + student.getPassword());
        System.out.println("  Password verification: " + student.verifyPassword("password123"));
    }
    
    private static void testQuizModel() {
        System.out.println("\n📝 Testing Quiz Model:");
        
        User teacher = new User("prof_smith", "password123", "smith@university.edu", "Prof. Smith", User.UserRole.TEACHER);
        
        Quiz javaQuiz = new Quiz(
            "Java Programming Basics",
            "Test your knowledge of fundamental Java concepts",
            teacher,
            "Programming",
            Quiz.DifficultyLevel.EASY,
            30
        );
        
        javaQuiz.setPublished(true);
        
        System.out.println("  Quiz: " + javaQuiz);
        System.out.println("  Quiz valid? " + javaQuiz.isValid());
        System.out.println("  Category: " + javaQuiz.getCategory());
        System.out.println("  Difficulty: " + javaQuiz.getDifficulty());
        System.out.println("  Duration: " + javaQuiz.getDurationMinutes() + " minutes");
        System.out.println("  Is published? " + javaQuiz.isPublished());
        
        // Test quiz statistics
        System.out.println("  Total points: " + javaQuiz.getTotalPoints());
        System.out.println("  Average score: " + javaQuiz.getAverageScore());
    }
    
    private static void testQuestionModel() {
        System.out.println("\n❓ Testing Question Model:");
        
        // Multiple Choice Question
        Question mcq = new Question(
            "Which of these is not a Java keyword?",
            Question.QuestionType.MULTIPLE_CHOICE,
            2,
            "String"
        );
        mcq.setOptionA("int");
        mcq.setOptionB("String");
        mcq.setOptionC("float");
        mcq.setOptionD("double");
        mcq.setExplanation("String is a class, not a keyword");
        
        // True/False Question
        Question tf = new Question(
            "Java supports multiple inheritance",
            Question.QuestionType.TRUE_FALSE,
            1,
            "false"
        );
        
        // Short Answer Question
        Question sa = new Question(
            "Explain the difference between == and .equals() in Java",
            Question.QuestionType.SHORT_ANSWER,
            3,
            "== compares references, .equals() compares content"
        );
        
        System.out.println("  MCQ: " + mcq);
        System.out.println("  MCQ options: " + mcq.getOptions());
        System.out.println("  MCQ is correct? " + mcq.isCorrect("String"));
        System.out.println("  MCQ is correct? " + mcq.isCorrect("int"));
        
        System.out.println("  TF: " + tf);
        System.out.println("  TF is correct? " + tf.isCorrect("false"));
        System.out.println("  TF is correct? " + tf.isCorrect("true"));
        
        System.out.println("  SA: " + sa);
        System.out.println("  SA is correct? " + sa.isCorrect("== compares memory addresses"));
        System.out.println("  SA validate? " + sa.validateAnswer("Some answer"));
    }
    
    private static void testModelRelationships() {
        System.out.println("\n🔗 Testing Model Relationships:");
        
        // Create teacher
        User teacher = new User("teacher1", "pass", "teacher@edu.com", "Teacher One", User.UserRole.TEACHER);
        
        // Create quiz
        Quiz quiz = new Quiz("Test Quiz", "Description", teacher, "Testing", Quiz.DifficultyLevel.MEDIUM, 20);
        teacher.addCreatedQuiz(quiz);
        
        // Create questions
        Question q1 = new Question("Question 1?", Question.QuestionType.MULTIPLE_CHOICE, 2, "A");
        q1.setOptionA("Option A");
        q1.setOptionB("Option B");
        q1.setOptionC("Option C");
        q1.setOptionD("Option D");
        
        Question q2 = new Question("Question 2?", Question.QuestionType.TRUE_FALSE, 1, "true");
        
        quiz.addQuestion(q1);
        quiz.addQuestion(q2);
        
        System.out.println("  Teacher created quizzes: " + teacher.getCreatedQuizzes().size());
        System.out.println("  Quiz has questions: " + quiz.getQuestions().size());
        System.out.println("  Question belongs to quiz: " + q1.getQuiz().getTitle());
        
        // Create student and attempt
        User student = new User("student1", "pass", "student@edu.com", "Student One", User.UserRole.STUDENT);
        QuizAttempt attempt = new QuizAttempt(student, quiz);
        
        System.out.println("  Student attempts: " + student.getQuizAttempts().size());
        System.out.println("  Quiz attempts: " + quiz.getAttempts().size());
        System.out.println("  Attempt user: " + attempt.getUser().getUsername());
        System.out.println("  Attempt quiz: " + attempt.getQuiz().getTitle());
        
        // Add answers
        UserAnswer answer1 = new UserAnswer(attempt, q1, "A");
        UserAnswer answer2 = new UserAnswer(attempt, q2, "false");
        
        attempt.addUserAnswer(answer1);
        attempt.addUserAnswer(answer2);
        
        System.out.println("  Attempt has answers: " + attempt.getUserAnswers().size());
        System.out.println("  Answer 1 correct? " + answer1.isCorrect());
        System.out.println("  Answer 2 correct? " + answer2.isCorrect());
        
        // Complete attempt
        attempt.complete();
        System.out.println("  Attempt score: " + attempt.getScore());
        System.out.println("  Attempt percentage: " + attempt.getPercentage());
        System.out.println("  Attempt grade: " + attempt.getGrade());
        
        // Create result
        Result result = new Result(attempt);
        attempt.setResult(result);
        student.getResults().add(result);
        quiz.getResults().add(result);
        
        System.out.println("  Result percentage: " + result.getPercentage());
        System.out.println("  Result grade: " + result.getGrade());
        System.out.println("  Result passing? " + result.isPassing());
    }
    
    private static void testBusinessLogic() {
        System.out.println("\n⚙️ Testing Business Logic:");
        
        // Test RMI Server
        RMIServer server = new RMIServer("RMI-Server-1", "localhost", 1099);
        System.out.println("  RMI Server connection: " + server.getConnectionString());
        System.out.println("  RMI Server available? " + server.isAvailable());
        
        server.setLoadFactor(75);
        System.out.println("  RMI Server load: " + server.getLoadFactor() + "%");
        System.out.println("  RMI Server status message: " + server.getStatusMessage());
        
        // Test Socket Session
        User user = new User("testuser", "pass", "test@email.com", "Test User", User.UserRole.STUDENT);
        SocketSession session = new SocketSession(user);
        
        System.out.println("  Socket Session ID: " + session.getSessionId());
        System.out.println("  Socket Session connected? " + session.isConnected());
        System.out.println("  Socket Session duration: " + session.getFormattedDuration());
        
        // Test Log
        Log log = Log.createUserLoginLog(user, "192.168.1.1");
        System.out.println("  Log: " + log.getFormattedMessage());
        System.out.println("  Log color: " + log.getColor());
        System.out.println("  Log CSV: " + log.toCSV());
        
        // Test Collections and Generics usage
        System.out.println("\n📚 Testing Collections and Generics:");
        
        User user1 = new User("user1", "pass", "u1@email.com", "User One", User.UserRole.STUDENT);
        User user2 = new User("user2", "pass", "u2@email.com", "User Two", User.UserRole.STUDENT);
        User user3 = new User("user3", "pass", "u3@email.com", "User Three", User.UserRole.TEACHER);
        
        java.util.List<User> userList = new java.util.ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        
        System.out.println("  Total users: " + userList.size());
        
        // Filter students using streams
        long studentCount = userList.stream()
            .filter(User::isStudent)
            .count();
        System.out.println("  Student count: " + studentCount);
        
        // Map usernames
        java.util.List<String> usernames = userList.stream()
            .map(User::getUsername)
            .collect(Collectors.toList());

        System.out.println("  Usernames: " + usernames);
        
        // Group by role
        java.util.Map<User.UserRole, java.util.List<User>> usersByRole = userList.stream()
            .collect(java.util.stream.Collectors.groupingBy(User::getRole));
        
        System.out.println("  Users by role:");
        usersByRole.forEach((role, users) -> 
            System.out.println("    " + role + ": " + users.size() + " users")
        );
    }
}