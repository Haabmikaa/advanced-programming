<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.quizapp.model.User, com.quizapp.model.Quiz, com.quizapp.model.Result, com.quizapp.dao.QuizDAO, com.quizapp.dao.ResultDAO, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !user.isTeacher()) {
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
        return;
    }

    QuizDAO quizDAO = new QuizDAO();
    ResultDAO resultDAO = new ResultDAO();
    
    // Fetch real data filtered by current teacher
    List<Quiz> teacherQuizzes = quizDAO.getByCreator(user.getUserId());
    List<Result> allSubmissions = resultDAO.getByTeacherId(user.getUserId());
    
    request.setAttribute("teacherQuizzes", teacherQuizzes);
    request.setAttribute("allSubmissions", allSubmissions);
%>
<!DOCTYPE html>
<html>
<head>
    <title>Teacher Dashboard - Quiz Web App</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        :root {
            --gold: #D4AF37;
            --black: #0a0a0a;
            --white: #ffffff;
            --dark-gray: #1e1e1e;
            --card-bg: #161616;
            --border: #2a2a2a;
            --shadow: 0 10px 30px rgba(212, 175, 55, 0.1);
        }

        body {
            background-color: var(--black);
            color: var(--white);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
        }

        .dashboard-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 40px 20px;
        }
        
        .dashboard-header {
            position: relative;
            background: url('https://images.unsplash.com/photo-1524178232363-1fb2b075b655?q=80&w=2070&auto=format&fit=crop') no-repeat center center;
            background-size: cover;
            min-height: 350px;
            border-radius: 15px;
            margin-bottom: 40px;
            border: 1px solid var(--border);
            box-shadow: var(--shadow);
            overflow: hidden;
            display: flex;
            flex-direction: column;
            justify-content: flex-end;
        }
        
        .header-overlay {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(to bottom, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0.8) 100%);
            z-index: 1;
        }

        .header-content {
            position: relative;
            z-index: 2;
            padding: 40px;
            width: 100%;
            box-sizing: border-box;
        }
        
        .user-info {
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
        }
        
        .user-details h1 {
            margin: 0 0 10px 0;
            font-size: 32px;
            color: var(--gold);
            text-transform: uppercase;
            letter-spacing: 2px;
        }
        
        .user-details p {
            margin: 5px 0;
            opacity: 0.8;
            font-size: 18px;
        }
        
        .action-buttons {
            display: flex;
            gap: 15px;
        }
        
        .btn {
            padding: 12px 25px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 700;
            transition: all 0.3s;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-size: 14px;
            display: inline-block;
        }
        
        .btn-primary {
            background: var(--gold);
            color: var(--black);
            border: 1px solid var(--gold);
        }
        
        .btn-primary:hover {
            background: transparent;
            color: var(--gold);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.3);
        }
        
        .btn-secondary {
            background: transparent;
            color: var(--white);
            border: 1px solid var(--white);
        }
        
        .btn-secondary:hover {
            border-color: var(--gold);
            color: var(--gold);
            transform: translateY(-2px);
        }
        
        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
            gap: 30px;
            margin-bottom: 40px;
        }
        
        .card {
            background: var(--card-bg);
            color: var(--white);
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.5);
            border: 1px solid var(--border);
            display: flex;
            flex-direction: column;
            transition: transform 0.3s, border-color 0.3s;
        }

        .card:hover {
            transform: translateY(-5px);
            border-color: var(--gold);
        }

        .card-scrollable {
            max-height: 450px;
            overflow-y: auto;
            padding-right: 10px;
            margin-top: 10px;
        }

        .card-scrollable::-webkit-scrollbar {
            width: 6px;
        }
        .card-scrollable::-webkit-scrollbar-thumb {
            background: var(--gold);
            border-radius: 10px;
        }
        
        .card h3 {
            color: var(--gold);
            text-transform: uppercase;
            letter-spacing: 1.5px;
            border-bottom: 1px solid var(--gold);
            padding-bottom: 15px;
            margin-bottom: 25px;
            margin-top: 0;
            font-size: 20px;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }
        
        .stat-item {
            text-align: center;
            padding: 20px;
            background: rgba(212, 175, 55, 0.05);
            border-radius: 12px;
            border: 1px solid rgba(212, 175, 55, 0.1);
            transition: all 0.3s;
        }

        .stat-item:hover {
            background: rgba(212, 175, 55, 0.1);
            border-color: var(--gold);
        }
        
        .stat-value {
            font-size: 32px;
            font-weight: 700;
            color: var(--gold);
            margin: 5px 0;
        }
        
        .stat-label {
            font-size: 12px;
            color: #a0a0a0;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-weight: 600;
        }
        
        .quiz-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        
        .quiz-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px;
            border-bottom: 1px solid var(--border);
            transition: background 0.3s;
        }
        
        .quiz-item:hover {
            background: rgba(255, 255, 255, 0.02);
        }

        .quiz-item:last-child {
            border-bottom: none;
        }
        
        .quiz-info h4 {
            margin: 0 0 8px 0;
            color: var(--white);
            font-size: 18px;
        }
        
        .quiz-meta {
            font-size: 13px;
            color: #a0a0a0;
        }

        .badge {
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .badge-gold {
            background: rgba(212, 175, 55, 0.1);
            color: var(--gold);
            border: 1px solid var(--gold);
        }
        
        .quiz-actions {
            display: flex;
            gap: 15px;
        }
        
        .action-link {
            color: var(--gold);
            text-decoration: none;
            font-size: 13px;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .action-link:hover {
            text-decoration: underline;
            opacity: 0.8;
        }

        .table-responsive {
            width: 100%;
            overflow-x: auto;
        }

        .custom-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .custom-table th {
            text-align: left;
            padding: 15px;
            color: var(--gold);
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 1px;
            border-bottom: 2px solid var(--gold);
        }

        .custom-table td {
            padding: 15px;
            border-bottom: 1px solid var(--border);
            font-size: 14px;
            color: var(--white);
        }

        .score-excellent { color: #4ade80; font-weight: 700; }
        .score-good { color: #facc15; font-weight: 700; }
        .score-poor { color: #f87171; font-weight: 700; }
        
        .quiz-info p {
            margin: 0;
            color: #ccc;
            font-size: 14px;
        }
        
        .quiz-status {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .status-published { background: var(--gold); color: var(--black); }
        .status-draft { background: #333; color: #ccc; }
        
        .quiz-actions {
            display: flex;
            gap: 10px;
        }
        
        .btn-small {
            padding: 6px 12px;
            border-radius: 6px;
            font-size: 12px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
        }
        
        .btn-edit { background: var(--black); color: var(--gold); border: 1px solid var(--gold); }
        .btn-edit:hover { background: var(--gold); color: var(--black); }
        .btn-view { background: #333; color: var(--white); }
        .btn-delete { background: #ff4444; color: white; }
        
        .create-quiz-form {
            background: rgba(255, 255, 255, 0.02);
            padding: 20px;
            border-radius: 8px;
            margin-top: 15px;
            border: 1px solid var(--border);
        }
        
        .form-row {
            display: flex;
            flex-direction: column;
            gap: 10px;
            margin-bottom: 15px;
        }

        .form-row input,
        .form-row select {
            width: 100%;
            padding: 12px;
            background: var(--black);
            border: 1px solid var(--border);
            border-radius: 8px;
            font-size: 14px;
            color: var(--white);
            transition: border-color 0.3s;
        }

        .form-row input:focus,
        .form-row select:focus {
            outline: none;
            border-color: var(--gold);
        }
        
        .student-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        
        .student-table th {
            background: var(--black);
            padding: 12px;
            text-align: left;
            color: var(--gold);
            font-weight: 600;
            border-bottom: 2px solid var(--gold);
            text-transform: uppercase;
            font-size: 0.8rem;
        }
        
        .student-table td {
            padding: 12px;
            border-bottom: 1px solid var(--border);
            color: var(--white);
        }
        
        .performance-chart {
            height: 200px;
            background: rgba(255, 255, 255, 0.02);
            border-radius: 8px;
            margin-top: 15px;
            display: flex;
            align-items: flex-end;
            padding: 20px;
            gap: 15px;
            border: 1px solid var(--border);
        }
        
        .chart-bar {
            flex: 1;
            background: var(--gold);
            border-radius: 4px 4px 0 0;
            position: relative;
            transition: all 0.3s;
        }
        
        .chart-bar:hover {
            background: var(--white);
            box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
        }

        .chart-value {
            position: absolute;
            top: -25px;
            left: 0;
            right: 0;
            text-align: center;
            font-size: 11px;
            font-weight: 700;
            color: var(--gold);
        }
        
        .chart-label {
            position: absolute;
            bottom: -30px;
            left: 0;
            right: 0;
            text-align: center;
            font-size: 11px;
            color: #a0a0a0;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            font-weight: 600;
        }

        /* Modal Styles */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.8);
        }

        .modal-content {
            background-color: var(--card-bg);
            color: var(--white);
            margin: 5% auto;
            padding: 30px;
            border-radius: 16px;
            width: 80%;
            max-width: 800px;
            max-height: 85vh;
            overflow-y: auto;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3);
            border: 1px solid var(--gold);
        }

        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid var(--gold);
        }

        .close-modal {
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
            color: var(--gold);
        }

        .question-item-edit {
            background: rgba(255, 255, 255, 0.02);
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 15px;
            border: 1px solid var(--border);
        }

        .add-question-box {
            background: rgba(212, 175, 55, 0.05);
            padding: 20px;
            border-radius: 12px;
            border: 2px dashed var(--gold);
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/index.html" class="logo-container">
            <div class="logo-icon">A</div>
            <div class="logo-text">Astu<span>Quiz</span></div>
        </a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/pages/teacher/dashboard.jsp">Dashboard</a>
            <a href="${pageContext.request.contextPath}/quiz?action=create">Create Quiz</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </nav>
    <div class="dashboard-container">
        <!-- Header with Hero Background Image -->
        <div class="dashboard-header">
            <div class="header-overlay"></div>
            <div class="header-content">
                <div class="user-info">
                    <div class="user-details">
                        <h1>Welcome, Professor <%= user.getFullName() %>!</h1>
                        <p>Teacher Dashboard | <%= user.getEmail() %></p>
                    </div>
                    <div class="action-buttons">
                        <a href="${pageContext.request.contextPath}/quiz?action=create" class="btn btn-primary">+ Create Quiz</a>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary">Logout</a>
                    </div>
                </div>
                <!-- Teaching Inspiration Section -->
                <div style="margin-top:25px; border-left: 3px solid var(--gold); padding-left: 20px;">
                    <h3 style="color:var(--gold); margin:0; font-size: 1.1rem; text-transform: uppercase; letter-spacing: 1px;">Teaching Inspiration</h3>
                    <p style="color:#f9fafb; max-width:600px; margin: 8px 0 0 0; font-size: 15px; line-height: 1.6; font-style: italic;">
                        "Education is the passport to the future, for tomorrow belongs to those who prepare for it today."
                        <span style="display:block; margin-top:5px; font-weight:700; font-style: normal; color:var(--gold);">- Malcolm X</span>
                    </p>
                </div>
            </div>
        </div>
        
        <!-- Dashboard Content -->
        <div class="dashboard-grid">
            <!-- Stats Card -->
            <%
                int totalAttempts = allSubmissions.size();
                double totalPercentage = 0;
                java.util.Set<Integer> uniqueStudents = new java.util.HashSet<>();
                for (Result r : allSubmissions) {
                    totalPercentage += r.getPercentage();
                    uniqueStudents.add(r.getUser().getUserId());
                }
                int avgScore = totalAttempts > 0 ? (int)(totalPercentage / totalAttempts) : 0;
                int activeStudents = uniqueStudents.size();
            %>
            <div class="card">
                <h3>📊 Teaching Statistics</h3>
                <div class="stats-grid">
                    <div class="stat-item">
                        <div class="stat-value"><%= teacherQuizzes.size() %></div>
                        <div class="stat-label">Active Quizzes</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value"><%= totalAttempts %></div>
                        <div class="stat-label">Total Attempts</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value"><%= avgScore %>%</div>
                        <div class="stat-label">Avg. Student Score</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value"><%= activeStudents %></div>
                        <div class="stat-label">Active Students</div>
                    </div>
                </div>
            </div>
            
            <!-- Create Quiz Card -->
            <div class="card">
                <h3>➕ Create New Quiz</h3>
                <form action="${pageContext.request.contextPath}/quiz" method="GET">
                    <input type="hidden" name="action" value="create">
                    <div class="create-quiz-form">
                        <div class="form-row">
                            <input type="text" name="pre_title" placeholder="Quiz Title">
                            <select name="pre_category">
                                <option>Programming</option>
                                <option>Mathematics</option>
                                <option>Science</option>
                            </select>
                        </div>
                        <div class="form-row">
                            <input type="number" name="pre_duration" placeholder="Duration (minutes)">
                            <select name="pre_difficulty">
                                <option value="EASY">Easy</option>
                                <option value="MEDIUM">Medium</option>
                                <option value="HARD">Hard</option>
                            </select>
                        </div>
                        <!-- Changed to submit button -->
                        <button type="submit" class="btn btn-primary" style="width: 100%">Create Quiz Template</button>
                    </div>
                </form>
            </div>
                        
            <!-- Your Quizzes Card -->
            <div class="card">
                <h3>📚 Your Quizzes</h3>
                <div class="card-scrollable">
                    <ul class="quiz-list" id="teacher-quiz-list">
                        <c:choose>
                            <c:when test="${not empty teacherQuizzes}">
                                <c:forEach var="quiz" items="${teacherQuizzes}">
                                    <li class="quiz-item">
                                        <div class="quiz-info">
                                            <h4>${quiz.title}</h4>
                                            <p>${quiz.category} • ${quiz.totalQuestions} questions</p>
                                        </div>
                                        <div class="quiz-actions">
                                            <span class="quiz-status status-published">Published</span>
                                            <a href="${pageContext.request.contextPath}/quiz?action=edit&id=${quiz.quizId}" class="btn-small btn-edit" style="text-decoration:none; display:inline-block;">Edit Questions</a>
                                        </div>
                                    </li>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <li class="quiz-item"><p>No quizzes created yet.</p></li>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>
            
            <!-- Student Performance Card -->
            <div class="card">
                <h3>📈 Student Performance</h3>
                <div class="performance-chart">
                    <c:forEach var="quiz" items="${teacherQuizzes}" varStatus="status">
                        <c:if test="${status.index < 5}">
                            <%-- Calculate real average for this quiz --%>
                            <%
                                Quiz q = (Quiz)pageContext.getAttribute("quiz");
                                double avg = resultDAO.getAverageScoreByQuizId(q.getQuizId());
                                pageContext.setAttribute("avgScore", (int)avg);
                            %>
                            <div class="chart-bar" style="height: ${avgScore}%">
                                <div class="chart-value">${avgScore}%</div>
                                <div class="chart-label">${quiz.title}</div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
                <p style="font-size: 12px; color: #64748b; margin-top: 35px; text-align: center;">Average score per quiz (%)</p>
            </div>
            
            <!-- Recent Submissions Card -->
            <div class="card">
                <h3>🔄 Recent Submissions</h3>
                <div class="card-scrollable">
                    <table class="student-table">
                        <thead>
                            <tr>
                                <th>Student</th>
                                <th>Quiz</th>
                                <th>Score</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty allSubmissions}">
                                    <c:forEach var="res" items="${allSubmissions}">
                                        <tr>
                                            <td>${res.user.fullName}</td>
                                            <td>${res.quiz.title}</td>
                                            <td style="font-weight: 600; color: #0ea5e9;">${res.percentage}%</td>
                                            <td style="font-size: 0.85em; color: #6b7280;">${res.submittedAt}</td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="4" style="text-align:center;">No submissions yet.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script>
</body>
</html>