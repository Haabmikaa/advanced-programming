-- ===========================================
-- DISTRIBUTED QUIZ WEB APPLICATION DATABASE
-- Advanced Programming Final Project
-- ===========================================

-- Create database
-- DROP DATABASE IF EXISTS quizdb;
-- CREATE DATABASE quizdb;
-- USE quizdb;

-- ===========================================
-- 1. USERS TABLE
-- ===========================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') DEFAULT 'STUDENT',
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT chk_email CHECK (email LIKE '%@%.%')
);

-- ===========================================
-- 2. QUIZZES TABLE
-- ===========================================
CREATE TABLE quizzes (
    quiz_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    created_by INT,
    category VARCHAR(50),
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    duration_minutes INT DEFAULT 30,
    max_attempts INT DEFAULT 1,
    is_published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_category (category),
    INDEX idx_difficulty (difficulty)
);

-- ===========================================
-- 3. QUESTIONS TABLE
-- ===========================================
CREATE TABLE questions (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    quiz_id INT NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('MULTIPLE_CHOICE', 'TRUE_FALSE', 'SHORT_ANSWER') DEFAULT 'MULTIPLE_CHOICE',
    points INT DEFAULT 1,
    option_a TEXT,
    option_b TEXT,
    option_c TEXT,
    option_d TEXT,
    correct_answer VARCHAR(500) NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE,
    INDEX idx_quiz_id (quiz_id)
);

-- ===========================================
-- 4. QUIZ_ATTEMPTS TABLE
-- ===========================================
CREATE TABLE quiz_attempts (
    attempt_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    quiz_id INT NOT NULL,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    score DECIMAL(5,2) DEFAULT 0,
    total_questions INT DEFAULT 0,
    correct_answers INT DEFAULT 0,
    status ENUM('IN_PROGRESS', 'COMPLETED', 'TIMEOUT') DEFAULT 'IN_PROGRESS',
    ip_address VARCHAR(45),
    user_agent TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE,
    INDEX idx_user_quiz (user_id, quiz_id),
    INDEX idx_status (status)
);

-- ===========================================
-- 5. USER_ANSWERS TABLE
-- ===========================================
CREATE TABLE user_answers (
    answer_id INT PRIMARY KEY AUTO_INCREMENT,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    user_answer VARCHAR(500),
    is_correct BOOLEAN DEFAULT FALSE,
    points_earned DECIMAL(5,2) DEFAULT 0,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
    INDEX idx_attempt (attempt_id),
    INDEX idx_question (question_id)
);

-- ===========================================
-- 6. RESULTS TABLE (For Analytics)
-- ===========================================
CREATE TABLE results (
    result_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    quiz_id INT NOT NULL,
    attempt_id INT NOT NULL,
    total_score DECIMAL(5,2) DEFAULT 0,
    percentage DECIMAL(5,2) DEFAULT 0,
    time_taken_seconds INT DEFAULT 0,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE CASCADE,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(attempt_id) ON DELETE CASCADE,
    INDEX idx_user_results (user_id),
    INDEX idx_quiz_results (quiz_id)
);

-- ===========================================
-- 7. RMI_SERVERS TABLE (For Distributed Processing)
-- ===========================================
CREATE TABLE rmi_servers (
    server_id INT PRIMARY KEY AUTO_INCREMENT,
    server_name VARCHAR(100) UNIQUE NOT NULL,
    host_address VARCHAR(100) NOT NULL,
    port INT NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'MAINTENANCE') DEFAULT 'ACTIVE',
    load_factor INT DEFAULT 0,
    last_heartbeat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status (status)
);

-- ===========================================
-- 8. SOCKET_SESSIONS TABLE (For Real-time)
-- ===========================================
CREATE TABLE socket_sessions (
    session_id VARCHAR(100) PRIMARY KEY,
    user_id INT,
    quiz_id INT,
    attempt_id INT,
    socket_address VARCHAR(100),
    connected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    disconnected_at TIMESTAMP NULL,
    status ENUM('CONNECTED', 'DISCONNECTED', 'TIMEOUT') DEFAULT 'CONNECTED',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id) ON DELETE SET NULL,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(attempt_id) ON DELETE SET NULL,
    INDEX idx_user_session (user_id),
    INDEX idx_status (status)
);

-- ===========================================
-- 9. LOGS TABLE (For System Monitoring)
-- ===========================================
CREATE TABLE logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    log_level ENUM('INFO', 'WARN', 'ERROR', 'DEBUG') DEFAULT 'INFO',
    component VARCHAR(50),
    message TEXT,
    user_id INT NULL,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_level (log_level),
    INDEX idx_component (component),
    INDEX idx_created (created_at)
);

-- ===========================================
-- 10. SAMPLE DATA INSERTION
-- ===========================================

-- Note: Admin user is now automatically created by the application on first run
-- using environment variables ADMIN_USERNAME and ADMIN_PASSWORD.

-- Insert Teacher Users (password: teacher123)
INSERT INTO users (username, password, email, full_name, role) VALUES
('john_teacher', 'teacher123', 'john@university.edu', 'John Smith', 'TEACHER'),
('sarah_teacher', 'teacher123', 'sarah@university.edu', 'Sarah Johnson', 'TEACHER');

-- Insert Student Users (password: student123)
INSERT INTO users (username, password, email, full_name, role) VALUES
('alice_student', 'student123', 'alice@student.edu', 'Alice Brown', 'STUDENT'),
('bob_student', 'student123', 'bob@student.edu', 'Bob Wilson', 'STUDENT'),
('charlie_student', 'student123', 'charlie@student.edu', 'Charlie Davis', 'STUDENT'),
('diana_student', 'student123', 'diana@student.edu', 'Diana Miller', 'STUDENT');

-- Insert Sample Quizzes
INSERT INTO quizzes (title, description, created_by, category, difficulty, duration_minutes, is_published) VALUES
('Java Programming Basics', 'Test your knowledge of fundamental Java concepts', 2, 'Programming', 'EASY', 20, TRUE),
('Advanced Java Concepts', 'Multithreading, Collections, and Generics', 2, 'Programming', 'MEDIUM', 30, TRUE),
('Database Management Systems', 'SQL, JDBC, and database concepts', 3, 'Database', 'MEDIUM', 25, TRUE),
('Web Technologies', 'Servlets, JSP, and web concepts', 3, 'Web Development', 'HARD', 40, TRUE);

-- Insert Questions for Java Basics Quiz
INSERT INTO questions (quiz_id, question_text, question_type, points, option_a, option_b, option_c, option_d, correct_answer, explanation) VALUES
(1, 'Which of these is not a Java keyword?', 'MULTIPLE_CHOICE', 1, 'int', 'String', 'float', 'double', 'B', 'String is a class, not a keyword'),
(1, 'What is the size of int in Java?', 'MULTIPLE_CHOICE', 1, '16-bit', '32-bit', '64-bit', 'Depends on platform', 'B', 'int is always 32-bit in Java'),
(1, 'Which method is the entry point of a Java program?', 'MULTIPLE_CHOICE', 1, 'main()', 'init()', 'start()', 'run()', 'A', 'main() method is the entry point'),
(1, 'Java supports multiple inheritance', 'TRUE_FALSE', 1, NULL, NULL, NULL, NULL, 'false', 'Java supports single inheritance through classes'),
(1, 'Explain the difference between == and .equals() in Java', 'SHORT_ANSWER', 2, NULL, NULL, NULL, NULL, '== compares references, .equals() compares content', '== checks memory address, .equals() checks actual content');

-- Insert Questions for Advanced Java Quiz
INSERT INTO questions (quiz_id, question_text, question_type, points, option_a, option_b, option_c, option_d, correct_answer, explanation) VALUES
(2, 'Which interface must be implemented for RMI?', 'MULTIPLE_CHOICE', 2, 'Serializable', 'Remote', 'Runnable', 'Cloneable', 'B', 'Remote interface is required for RMI'),
(2, 'Which collection maintains insertion order?', 'MULTIPLE_CHOICE', 2, 'HashSet', 'TreeSet', 'LinkedHashSet', 'HashMap', 'C', 'LinkedHashSet maintains insertion order'),
(2, 'What is thread synchronization?', 'MULTIPLE_CHOICE', 2, 'Running multiple threads', 'Controlling thread execution order', 'Preventing race conditions', 'Thread communication', 'C', 'Synchronization prevents race conditions'),
(2, 'Servlets are thread-safe by default', 'TRUE_FALSE', 2, NULL, NULL, NULL, NULL, 'false', 'Servlets are not thread-safe by default'),
(2, 'Explain the purpose of JDBC DriverManager', 'SHORT_ANSWER', 3, NULL, NULL, NULL, NULL, 'Manages JDBC drivers and connections', 'DriverManager handles driver registration and connection establishment');

-- Insert RMI Servers
INSERT INTO rmi_servers (server_name, host_address, port, status, load_factor) VALUES
('RMI-Server-1', 'localhost', 1099, 'ACTIVE', 0),
('RMI-Server-2', 'localhost', 1100, 'ACTIVE', 0);

-- Insert Sample Logs
INSERT INTO logs (log_level, component, message, user_id) VALUES
('INFO', 'SYSTEM', 'Database initialized successfully', 1),
('INFO', 'RMI', 'RMI Server started on port 1099', 1),
('INFO', 'WEB', 'Web application deployed successfully', 1);

-- ===========================================
-- 11. CREATE VIEWS FOR REPORTING
-- ===========================================

-- View for Quiz Statistics
CREATE VIEW quiz_statistics AS
SELECT 
    q.quiz_id,
    q.title,
    q.category,
    q.difficulty,
    COUNT(DISTINCT qa.attempt_id) as total_attempts,
    COUNT(DISTINCT qa.user_id) as unique_users,
    AVG(r.percentage) as average_score,
    MAX(r.percentage) as highest_score,
    MIN(r.percentage) as lowest_score
FROM quizzes q
LEFT JOIN quiz_attempts qa ON q.quiz_id = qa.quiz_id
LEFT JOIN results r ON qa.attempt_id = r.attempt_id
WHERE qa.status = 'COMPLETED'
GROUP BY q.quiz_id, q.title, q.category, q.difficulty;

-- View for User Performance
CREATE VIEW user_performance AS
SELECT 
    u.user_id,
    u.username,
    u.full_name,
    u.role,
    COUNT(DISTINCT qa.attempt_id) as quizzes_taken,
    SUM(r.total_score) as total_points,
    AVG(r.percentage) as average_percentage,
    MAX(r.percentage) as best_score,
    MIN(r.percentage) as worst_score
FROM users u
LEFT JOIN quiz_attempts qa ON u.user_id = qa.user_id
LEFT JOIN results r ON qa.attempt_id = r.attempt_id
WHERE qa.status = 'COMPLETED'
GROUP BY u.user_id, u.username, u.full_name, u.role;

-- ===========================================
-- 12. CREATE STORED PROCEDURES
-- ===========================================

-- Procedure to calculate quiz score
DELIMITER //
CREATE PROCEDURE CalculateQuizScore(IN p_attempt_id INT)
BEGIN
    DECLARE v_total_score DECIMAL(5,2);
    DECLARE v_total_questions INT;
    DECLARE v_correct_answers INT;
    DECLARE v_percentage DECIMAL(5,2);
    
    -- Calculate total score
    SELECT SUM(points_earned), COUNT(*), SUM(CASE WHEN is_correct THEN 1 ELSE 0 END)
    INTO v_total_score, v_total_questions, v_correct_answers
    FROM user_answers
    WHERE attempt_id = p_attempt_id;
    
    -- Calculate percentage
    SET v_percentage = (v_total_score / (v_total_questions * 1.0)) * 100;
    
    -- Update quiz attempt
    UPDATE quiz_attempts 
    SET score = v_total_score,
        total_questions = v_total_questions,
        correct_answers = v_correct_answers,
        status = 'COMPLETED',
        end_time = CURRENT_TIMESTAMP
    WHERE attempt_id = p_attempt_id;
    
    -- Insert into results
    INSERT INTO results (user_id, quiz_id, attempt_id, total_score, percentage, time_taken_seconds)
    SELECT qa.user_id, qa.quiz_id, p_attempt_id, v_total_score, v_percentage,
           TIMESTAMPDIFF(SECOND, qa.start_time, CURRENT_TIMESTAMP)
    FROM quiz_attempts qa
    WHERE qa.attempt_id = p_attempt_id;
    
    SELECT v_total_score as total_score, v_total_questions as total_questions, 
           v_correct_answers as correct_answers, v_percentage as percentage;
END //
DELIMITER ;

-- Procedure to get active users
DELIMITER //
CREATE PROCEDURE GetActiveUsers()
BEGIN
    SELECT u.user_id, u.username, u.full_name, u.role,
           COUNT(DISTINCT ss.session_id) as active_sessions,
           MAX(ss.connected_at) as last_connection
    FROM users u
    LEFT JOIN socket_sessions ss ON u.user_id = ss.user_id AND ss.status = 'CONNECTED'
    WHERE u.is_active = TRUE
    GROUP BY u.user_id, u.username, u.full_name, u.role
    ORDER BY u.role, u.username;
END //
DELIMITER ;

-- ===========================================
-- 13. CREATE TRIGGERS
-- ===========================================

-- Trigger to update user last login
DELIMITER //
CREATE TRIGGER UpdateLastLogin
AFTER INSERT ON socket_sessions
FOR EACH ROW
BEGIN
    IF NEW.status = 'CONNECTED' THEN
        UPDATE users 
        SET last_login = CURRENT_TIMESTAMP 
        WHERE user_id = NEW.user_id;
    END IF;
END //
DELIMITER ;

-- Trigger to log user actions
DELIMITER //
CREATE TRIGGER LogQuizAttempt
AFTER INSERT ON quiz_attempts
FOR EACH ROW
BEGIN
    INSERT INTO logs (log_level, component, message, user_id)
    VALUES ('INFO', 'QUIZ', CONCAT('User ', NEW.user_id, ' started quiz ', NEW.quiz_id), NEW.user_id);
END //
DELIMITER ;

-- ===========================================
-- 14. CREATE INDEXES FOR PERFORMANCE
-- ===========================================

CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_quizzes_created ON quizzes(created_at);
CREATE INDEX idx_questions_type ON questions(question_type);
CREATE INDEX idx_attempts_time ON quiz_attempts(start_time);
CREATE INDEX idx_answers_time ON user_answers(answered_at);
CREATE INDEX idx_logs_time ON logs(created_at);

-- ===========================================
-- 15. SHOW ALL TABLES
-- ===========================================
SHOW TABLES;

-- ===========================================
-- 16. SHOW TABLE DESCRIPTIONS
-- ===========================================
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH,
    CREATE_TIME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'quizdb';

-- ===========================================
-- 17. VERIFY DATA
-- ===========================================
SELECT 'Users' as Table_Name, COUNT(*) as Record_Count FROM users
UNION ALL
SELECT 'Quizzes', COUNT(*) FROM quizzes
UNION ALL
SELECT 'Questions', COUNT(*) FROM questions
UNION ALL
SELECT 'RMI Servers', COUNT(*) FROM rmi_servers;

-- ===========================================
-- END OF DATABASE SETUP
-- ===========================================