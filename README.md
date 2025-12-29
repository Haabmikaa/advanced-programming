# Distributed Quiz Web Application

## Advanced Programming Course - Final Project

### 📋 Project Overview
A distributed quiz web application demonstrating advanced Java programming concepts including:
- **Servlets** for web layer
- **RMI** (Remote Method Invocation) for distributed processing
- **JDBC** with MySQL for database operations
- **Networking** with sockets for real-time communication
- **Multithreading** for concurrent user handling
- **Collections & Generics** for data management

### 🎯 Features
1. **User Roles**
   - Students: Take quizzes, view results
   - Teachers: Create quizzes, manage questions, view analytics
   - Admin: User management, system monitoring

2. **Quiz System**
   - Real-time quiz taking
   - Multiple question types (MCQ, True/False)
   - Timer-based quizzes
   - Instant scoring

3. **Distributed Architecture**
   - RMI services for quiz processing
   - Socket communication for live updates
   - Load balancing with multiple servers

### 🛠️ Technologies
- **Backend**: Java 8, Servlets, JSP, RMI, JDBC
- **Frontend**: HTML5, CSS3, JavaScript
- **Database**: MySQL 8.0
- **Server**: Apache Tomcat 9+
- **Build Tool**: Maven 3.6+

### 📁 Project Structure


### 🚀 Quick Start
1. **Clone/Download** the project
2. **Install MySQL** and create database
3. **Configure** database.properties
4. **Build** with Maven: `mvn clean package`
5. **Deploy** WAR file to Tomcat
6. **Start** RMI registry
7. **Access** at: http://localhost:8080/QuizWebApp

### 📊 Database Schema
- **users** (id, username, password, role, email, created_at)
- **quizzes** (id, title, description, duration, created_by, created_at)
- **questions** (id, quiz_id, question_text, question_type, options, correct_answer)
- **results** (id, user_id, quiz_id, score, total_questions, taken_at)
- **answers** (id, result_id, question_id, user_answer, is_correct)

### 👥 Team
- **Developer**: [CSE , Group 1 and 2 students //2018]
- **Student ID**: [
        Name                                          Id
   1, ermias nigussie                          ugr/34318/16 
   2, 
   3
   4
   5

]
- **Course**: Advanced Programming
- **University**: Adama Science and Technology University

### 📚 Concepts Demonstrated
1. Object-Oriented Programming
2. Client-Server Architecture
3. Distributed Computing with RMI
4. Database Programming with JDBC
5. Web Development with Servlets
6. Concurrent Programming
7. Network Programming
8. Design Patterns

### 📄 License
Educational Project - Made by computer science and engineering students at adama science and technology university