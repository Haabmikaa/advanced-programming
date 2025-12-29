<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.quizapp.model.User, com.quizapp.model.Quiz, com.quizapp.model.Result, com.quizapp.dao.QuizDAO, com.quizapp.dao.ResultDAO, java.util.List" %>
<%
    // 1. Session and Authentication Check
    User user = (User) session.getAttribute("user");
    if (user == null || !user.isStudent()) {
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
        return;
    }

    // 2. Fetch Real Data
    QuizDAO quizDAO = new QuizDAO();
    ResultDAO resultDAO = new ResultDAO();
    
    List<Quiz> availableQuizzes = quizDAO.getQuizzesNotTakenByStudent(user.getUserId());
    List<Quiz> takenQuizzes = quizDAO.getQuizzesTakenByStudent(user.getUserId());
    List<Result> recentResults = resultDAO.getByUserId(user.getUserId());
    
    request.setAttribute("availableQuizzes", availableQuizzes);
    request.setAttribute("takenQuizzes", takenQuizzes);
    request.setAttribute("recentResults", recentResults);
%>
<!DOCTYPE html>
<html>
<head>
    <title>Student Dashboard - Quiz Web App</title>
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
            padding: 20px;
        }
        
        .dashboard-header {
            position: relative;
            background: url('https://images.unsplash.com/photo-1516321318423-f06f85e504b3?q=80&w=2070&auto=format&fit=crop') no-repeat center center;
            background-size: cover;
            min-height: 350px;
            border-radius: 15px;
            margin-bottom: 30px;
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
            font-size: 28px;
            color: var(--gold);
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        .user-details p {
            margin: 5px 0;
            opacity: 0.9;
            color: #ccc;
        }
        .logout-btn {
            background: transparent;
            color: var(--gold);
            border: 2px solid var(--gold);
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-size: 14px;
        }
        .logout-btn:hover {
            background: var(--gold);
            color: var(--black);
            transform: translateY(-2px);
        }
        .dashboard-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            row-gap: 35px;
            gap: 25px;
            margin-bottom: 30px;
        }
        .card {
            background: var(--card-bg);
            border-radius: 12px;
            padding: 25px;
            box-shadow: var(--shadow);
            border: 1px solid var(--border);
            color: var(--white);
            transition: transform 0.3s, border-color 0.3s;
        }
        .card:hover {
            transform: translateY(-5px);
            border-color: var(--gold);
        }
        .card h3 {
            color: var(--gold);
            margin-top: 0;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 1px solid var(--gold);
            text-transform: uppercase;
            letter-spacing: 1.5px;
            font-size: 1.2rem;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
        }
        .stat-item {
            text-align: center;
            padding: 15px;
            background: rgba(212, 175, 55, 0.05);
            border-radius: 8px;
            border: 1px solid rgba(212, 175, 55, 0.1);
            transition: all 0.3s;
        }
        .stat-item:hover {
            background: rgba(212, 175, 55, 0.1);
            border-color: var(--gold);
        }
        .stat-value {
            font-size: 28px;
            font-weight: 800;
            color: var(--gold);
            margin: 5px 0;
        }
        .stat-label {
            font-size: 12px;
            color: #888;
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
            padding: 15px;
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
            margin: 0 0 5px 0;
            color: var(--white);
            font-size: 1.1rem;
        }
        .quiz-info p {
            margin: 0;
            color: #888;
            font-size: 13px;
        }
        .quiz-actions {
            display: flex;
            gap: 10px;
        }
        .btn {
            padding: 8px 16px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
            font-size: 13px;
            transition: all 0.3s;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        .btn-primary {
            background: var(--gold);
            color: var(--black);
            border: 1px solid var(--gold);
        }
        .btn-primary:hover {
            background: transparent;
            color: var(--gold);
            transform: scale(1.05);
        }
        .btn-secondary {
            background: transparent;
            color: var(--white);
            border: 1px solid var(--white);
        }
        .btn-secondary:hover {
            border-color: var(--gold);
            color: var(--gold);
            transform: scale(1.05);
        }
        .results-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        .results-table th {
            background: rgba(212, 175, 55, 0.05);
            padding: 12px;
            text-align: left;
            color: var(--gold);
            font-weight: 700;
            border-bottom: 1px solid var(--gold);
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        .results-table td {
            padding: 12px;
            border-bottom: 1px solid var(--border);
            color: #ccc;
            font-size: 14px;
        }
        .grade {
            font-weight: 700;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 11px;
            text-transform: uppercase;
        }
        .grade-a { background: rgba(212, 175, 55, 0.1); color: var(--gold); border: 1px solid var(--gold); }
        .grade-d { background: rgba(239, 68, 68, 0.1); color: #ef4444; border: 1px solid #ef4444; }
        .grade-f { background: #1a1a1a; color: #666; border: 1px solid #333; }

        /* Modal Styles */
        .modal-content {
            background: var(--card-bg) !important;
            color: var(--white) !important;
            border: 1px solid var(--gold) !important;
            box-shadow: var(--shadow) !important;
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
            <a href="${pageContext.request.contextPath}/pages/student/dashboard.jsp">Dashboard</a>
            <a href="${pageContext.request.contextPath}/quiz?action=list">Available Quizzes</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </nav>
    <div class="dashboard-container">
        <!-- Header with Learning Banner Image as Background -->
        <div class="dashboard-header">
            <div class="header-overlay"></div>
            <div class="header-content">
                <div class="user-info">
                    <div class="user-details">
                        <h1>Welcome, <%= user.getFullName() %>!</h1>
                        <p>Student Dashboard | <%= user.getEmail() %></p>
                    </div>
                    <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Logout</a>
                </div>
                <!-- Learning Tip Section -->
                <div style="margin-top:20px; border-left: 3px solid var(--gold); padding-left: 20px;">
                    <h3 style="color:var(--gold); margin:0; font-size: 1.1rem; text-transform: uppercase; letter-spacing: 1px;">Learning Tip</h3>
                    <p style="color:#e0e7ff; max-width:600px; margin: 5px 0 0 0; font-size: 14px; line-height: 1.5;">Small, consistent steps lead to big achievements. Make learning a habit—just 15 focused minutes a day makes all the difference!</p>
                </div>
            </div>
        </div>
        
        <!-- Dashboard Content -->
        <div class="dashboard-grid">
            
            <!-- Recent Results Card (Moved up) -->
            <div class="card">
                <h3>📈 Recent Results</h3>
                <table class="results-table">
                    <thead>
                        <tr>
                            <th>Quiz</th>
                            <th>Score</th>
                            <th>Grade</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty recentResults}">
                                <c:forEach var="result" items="${recentResults}">
                                    <tr>
                                        <td>${result.quiz.title}</td>
                                        <td style="font-weight: 600; color: var(--gold);">
                                            ${result.percentage}%
                                        </td>
                                        <td>
                                            <span class="grade ${result.percentage >= 60 ? 'grade-a' : 'grade-d'}">
                                                <c:choose>
                                                    <c:when test="${not empty result.grade}">
                                                        ${result.grade}
                                                    </c:when>
                                                    <c:otherwise>
                                                        grade not submitted
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                        <td style="color: #ccc; font-size: 0.9em;">
                                            ${result.submittedAt}
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr><td colspan="4" style="text-align:center;">No results found.</td></tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <!-- Stats Card (Moved down) -->
            <div class="card">
                <h3>📊 Your Statistics</h3>
                <div class="stats-grid">
                    <div class="stat-item">
                        <div class="stat-value">${takenQuizzes.size()}</div>
                        <div class="stat-label">Quizzes Taken</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value">${availableQuizzes.size()}</div>
                        <div class="stat-label">Remaining</div>
                    </div>
                </div>
            </div>
            
            <!-- CONTAINER 1: Available Quizzes (New/Unfinished) -->
            <div class="card">
                <h3>🎯 Available Quizzes</h3>
                <ul class="quiz-list">
                    <c:choose>
                        <c:when test="${not empty availableQuizzes}">
                            <c:forEach var="quiz" items="${availableQuizzes}">
                                <li class="quiz-item">
                                    <div class="quiz-info">
                                        <h4>${quiz.title}</h4>
                                        <p>${quiz.category} • ${quiz.durationMinutes} minutes</p>
                                    </div>
                                    <div class="quiz-actions">
    <a href="${pageContext.request.contextPath}/quiz?action=take&id=${quiz.quizId}" class="btn btn-primary">Take Quiz</a>
    <button class="btn btn-secondary quiz-details-btn" data-quiz-id="${quiz.quizId}" type="button">Details</button>
</div>
                                </li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <li class="quiz-item"><p>No new quizzes available.</p></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>

            <!-- CONTAINER 2: Taken Quizzes (Completed) -->
            <div class="card">
                <h3>✅ Taken Quizzes</h3>
                <ul class="quiz-list">
                    <c:choose>
                        <c:when test="${not empty takenQuizzes}">
                            <c:forEach var="quiz" items="${takenQuizzes}">
                                <li class="quiz-item">
                                    <div class="quiz-info">
                                        <h4>${quiz.title}</h4>
                                        <p>Completed • ${quiz.category}</p>
                                    </div>
                                    <div class="quiz-actions">
                                        <!-- Finds the latest result for this quiz -->
                                        <c:set var="quizResultId" value="0" />
                                        <c:forEach var="res" items="${recentResults}">
                                            <c:if test="${res.quiz.quizId == quiz.quizId && quizResultId == 0}">
                                                <c:set var="quizResultId" value="${res.resultId}" />
                                            </c:if>
                                        </c:forEach>
                                        <a href="${pageContext.request.contextPath}/result?action=review&resultId=${quizResultId}" 
                                           class="btn btn-secondary">View Questions</a>
                                    </div>
                                </li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <li class="quiz-item"><p>You haven't completed any quizzes yet.</p></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </div>
<!-- Quiz Details Modal -->
<div id="quizDetailsModal" class="modal" style="display:none; position:fixed; top:0; left:0; width:100vw; height:100vh; background:rgba(0,0,0,0.8); z-index:10000; align-items:center; justify-content:center;">
  <div style="background:var(--dark-gray); border-radius:12px; max-width:520px; width:90vw; padding:32px 24px; box-shadow:0 4px 32px rgba(212, 175, 55, 0.2); position:relative; border: 1px solid var(--gold);">
    <button id="closeQuizDetailsModal" style="position:absolute;top:12px;right:18px;font-size:24px;border:none; background:none; cursor:pointer; color: var(--gold);">×</button>
    <div id="quizDetailsContent">
      <!-- Populated by JS -->
      <div style="text-align:center; color: var(--white);"><em>Loading...</em></div>
    </div>
  </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const modal = document.getElementById('quizDetailsModal');
        const content = document.getElementById('quizDetailsContent');
        const closeBtn = document.getElementById('closeQuizDetailsModal');

        // Close modal logic
        closeBtn.onclick = () => modal.style.display = 'none';
        window.onclick = (event) => { if (event.target == modal) modal.style.display = 'none'; };

        // Details button logic
        document.querySelectorAll('.quiz-details-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const quizId = this.getAttribute('data-quiz-id');
                modal.style.display = 'flex';
                content.innerHTML = '<div style="text-align:center; color: var(--white);"><em>Loading...</em></div>';

                fetch(`${pageContext.request.contextPath}/quiz?action=details_json&id=${quizId}`)
                    .then(res => res.json())
                    .then(data => {
                        content.innerHTML = `
                            <div style="text-align:center;">
                                <h2 style="margin:0 0 12px 0; color:var(--gold);">${data.title}</h2>
                                <div style="display:inline-block; padding:4px 12px; background:rgba(212, 175, 55, 0.1); color:var(--gold); border: 1px solid var(--gold); border-radius:12px; font-size:12px; margin-bottom:16px;">
                                    ${data.category}
                                </div>
                                <p style="color:#ccc; font-size:15px; line-height:1.6; margin-bottom:24px;">${data.description}</p>
                                <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px; margin-bottom:24px; text-align:left;">
                                    <div style="background:rgba(255,255,255,0.05); padding:12px; border-radius:8px; border: 1px solid rgba(212, 175, 55, 0.2);">
                                        <div style="font-size:12px; color:#94a3b8;">Duration</div>
                                        <div style="font-weight:600; color:var(--white);">${data.durationMinutes} min</div>
                                    </div>
                                    <div style="background:rgba(255,255,255,0.05); padding:12px; border-radius:8px; border: 1px solid rgba(212, 175, 55, 0.2);">
                                        <div style="font-size:12px; color:#94a3b8;">Difficulty</div>
                                        <div style="font-weight:600; color:var(--white);">${data.difficulty}</div>
                                    </div>
                                    <div style="background:rgba(255,255,255,0.05); padding:12px; border-radius:8px; border: 1px solid rgba(212, 175, 55, 0.2);">
                                        <div style="font-size:12px; color:#94a3b8;">Questions</div>
                                        <div style="font-weight:600; color:var(--white);">${data.questionCount} Qs</div>
                                    </div>
                                </div>
                                <a href="${pageContext.request.contextPath}/quiz?action=take&id=${data.quizId}" 
                                   class="btn btn-primary" style="display:block; text-align:center; padding:12px;">Start Quiz Now</a>
                            </div>
                        `;
                    })
                    .catch(err => {
                        content.innerHTML = '<div style="color:#ff4d4d; text-align:center;">Failed to load quiz details.</div>';
                    });
            });
        });
    });
</script>
</body>
</html>
