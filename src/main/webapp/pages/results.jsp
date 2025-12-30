<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.quizapp.model.User, com.quizapp.model.Result" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
        return;
    }
    
    List<Result> results = (List<Result>) request.getAttribute("results");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Your Results - Quiz Web App</title>
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

        .results-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 40px 20px;
        }
        
        .page-header {
            background: linear-gradient(135deg, var(--black) 0%, var(--dark-gray) 100%);
            color: var(--white);
            padding: 40px;
            border-radius: 15px;
            margin-bottom: 40px;
            border: 1px solid var(--border);
            text-align: center;
            box-shadow: var(--shadow);
        }
        
        .page-header h1 {
            margin: 0 0 10px 0;
            font-size: 36px;
            color: var(--gold);
            text-transform: uppercase;
            letter-spacing: 2px;
        }
        
        .page-header p {
            margin: 0;
            opacity: 0.8;
            font-size: 18px;
        }
        
        .stats-summary {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 25px;
            margin-bottom: 40px;
        }
        
        .stat-box {
            background: var(--card-bg);
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            text-align: center;
            border: 1px solid var(--border);
            transition: all 0.3s;
        }

        .stat-box:hover {
            transform: translateY(-5px);
            border-color: var(--gold);
            box-shadow: 0 15px 35px rgba(212, 175, 55, 0.1);
        }
        
        .stat-icon {
            font-size: 40px;
            margin-bottom: 15px;
            display: block;
        }
        
        .stat-number {
            font-size: 32px;
            font-weight: 700;
            color: var(--gold);
            margin: 5px 0;
        }
        
        .stat-label {
            color: #a0a0a0;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 1.5px;
            font-weight: 600;
        }
        
        .results-table-container {
            background: var(--card-bg);
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            padding: 35px;
            margin-bottom: 40px;
            border: 1px solid var(--border);
        }
        
        .table-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }
        
        .table-header h3 {
            color: var(--gold);
            margin: 0;
            font-size: 24px;
            text-transform: uppercase;
            letter-spacing: 1.5px;
        }
        
        .export-btn {
            background: transparent;
            color: var(--gold);
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 700;
            font-size: 13px;
            border: 1px solid var(--gold);
            transition: all 0.3s;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .export-btn:hover {
            background: var(--gold);
            color: var(--black);
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.3);
        }
        
        .results-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .results-table th {
            background: rgba(255, 255, 255, 0.02);
            padding: 20px;
            text-align: left;
            color: var(--gold);
            font-weight: 700;
            border-bottom: 2px solid var(--gold);
            text-transform: uppercase;
            font-size: 12px;
            letter-spacing: 1.5px;
        }
        
        .results-table td {
            padding: 20px;
            border-bottom: 1px solid var(--border);
            color: var(--white);
            font-size: 15px;
        }

        .results-table tr:hover td {
            background: rgba(212, 175, 55, 0.03);
        }
        
        .score-cell {
            font-weight: 700;
            font-size: 18px;
        }
        
        .score-excellent { color: #4ade80; }
        .score-good { color: #facc15; }
        .score-average { color: #fb923c; }
        .score-poor { color: #f87171; }
        
        .grade-badge {
            display: inline-block;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 700;
            text-transform: uppercase;
        }
        
        .grade-a { background: rgba(74, 222, 128, 0.1); color: #4ade80; border: 1px solid rgba(74, 222, 128, 0.3); }
        .grade-b { background: rgba(250, 204, 21, 0.1); color: #facc15; border: 1px solid rgba(250, 204, 21, 0.3); }
        .grade-c { background: rgba(251, 146, 60, 0.1); color: #fb923c; border: 1px solid rgba(251, 146, 60, 0.3); }
        .grade-d { background: rgba(248, 113, 113, 0.1); color: #f87171; border: 1px solid rgba(248, 113, 113, 0.3); }
        .grade-f { background: rgba(239, 68, 68, 0.1); color: #ef4444; border: 1px solid rgba(239, 68, 68, 0.3); }
        
        .action-btn {
            padding: 8px 16px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            transition: all 0.3s;
        }

        .btn-view {
            color: var(--gold);
            border: 1px solid var(--gold);
        }

        .btn-view:hover {
            background: var(--gold);
            color: var(--black);
        }
        
        .btn-review { 
            background: transparent; 
            color: var(--white); 
            border: 1px solid var(--border);
        }
        .btn-review:hover {
            background: var(--border);
        }

        .btn-retake { 
            background: var(--gold); 
            color: var(--black); 
            border: 1px solid var(--gold);
        }
        .btn-retake:hover {
            background: #b8952e;
            transform: translateY(-2px);
        }
        
        .performance-chart-container {
            background: var(--card-bg);
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            padding: 30px;
            margin-bottom: 40px;
            border: 1px solid var(--border);
        }
        
        .chart-title {
            color: var(--gold);
            margin: 0 0 30px 0;
            font-size: 24px;
        }
        
        .chart-container {
            height: 350px;
            position: relative;
            padding-bottom: 30px;
        }
        
        .chart-grid {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 30px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }
        
        .chart-grid-line {
            border-bottom: 1px dashed #333333;
            flex: 1;
        }
        
        .chart-bars {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 30px;
            display: flex;
            align-items: flex-end;
            gap: 25px;
            padding: 0 50px;
        }
        
        .chart-bar {
            flex: 1;
            background: linear-gradient(to top, var(--gold), #b8952e);
            border-radius: 6px 6px 0 0;
            position: relative;
            transition: all 0.3s ease;
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.2);
        }
        
        .chart-bar:hover {
            filter: brightness(1.2);
            transform: scaleX(1.05);
        }
        
        .chart-label {
            position: absolute;
            bottom: -35px;
            left: 0;
            right: 0;
            text-align: center;
            font-size: 12px;
            color: #a0a0a0;
            font-weight: 500;
        }
        
        .chart-value {
            position: absolute;
            top: -30px;
            left: 0;
            right: 0;
            text-align: center;
            font-weight: 700;
            color: var(--gold);
            font-size: 14px;
        }
        
        .empty-state {
            text-align: center;
            padding: 80px 20px;
            background: var(--card-bg);
            border-radius: 15px;
            border: 1px dashed var(--border);
        }
        
        .empty-state h3 {
            color: var(--gold);
            font-size: 24px;
            margin-bottom: 15px;
        }

        .empty-state p {
            color: #a0a0a0;
            font-size: 18px;
            margin-bottom: 30px;
        }
        
        .empty-state .btn {
            display: inline-block;
            background: var(--gold);
            color: var(--black);
            padding: 15px 35px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 700;
            transition: all 0.3s;
        }

        .empty-state .btn:hover {
            background: #b8952e;
            transform: translateY(-3px);
            box-shadow: 0 10px 20px rgba(212, 175, 55, 0.2);
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
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </nav>
    <div class="results-container">
        <!-- Header -->
        <div class="page-header">
            <h1>Your Quiz Results</h1>
            <p>Track your progress and performance over time</p>
        </div>
        
        <!-- Stats Summary -->
        <div class="stats-summary">
            <div class="stat-box">
                <div class="stat-icon">📊</div>
                <div class="stat-number"><%= results != null ? results.size() : 0 %></div>
                <div class="stat-label">Quizzes Taken</div>
            </div>
            <div class="stat-box">
                <div class="stat-icon">🏆</div>
                <div class="stat-number">85%</div>
                <div class="stat-label">Average Score</div>
            </div>
            <div class="stat-box">
                <div class="stat-icon">⏱️</div>
                <div class="stat-number">3h 42m</div>
                <div class="stat-label">Total Time</div>
            </div>
            <div class="stat-box">
                <div class="stat-icon">📈</div>
                <div class="stat-number">+12%</div>
                <div class="stat-label">Improvement</div>
            </div>
        </div>
        
        <% if (results != null && !results.isEmpty()) { %>
            <!-- Performance Chart -->
            <div class="performance-chart-container">
                <h3 class="chart-title">📈 Performance Trend</h3>
                <div class="chart-container">
                    <div class="chart-grid">
                        <div class="chart-grid-line"></div>
                        <div class="chart-grid-line"></div>
                        <div class="chart-grid-line"></div>
                        <div class="chart-grid-line"></div>
                        <div class="chart-grid-line"></div>
                    </div>
                    <div class="chart-bars" id="performanceChart">
                        <!-- Chart bars will be generated by JavaScript -->
                    </div>
                </div>
            </div>
            
            <!-- Results Table -->
            <div class="results-table-container">
                <div class="table-header">
                    <h3>📋 Detailed Results</h3>
                    <a href="#" class="export-btn">Export as CSV</a>
                </div>
                <table class="results-table">
                    <thead>
                        <tr>
                            <th>Quiz Name</th>
                            <th>Date Taken</th>
                            <th>Score</th>
                            <th>Grade</th>
                            <th>Time Taken</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Result result : results) { %>
                        <tr>
                            <td><strong><%= result.getQuizName() %></strong></td>
                            <td><%= result.getDateTaken() %></td>
                            <td class="score-cell <%= result.getScore() >= 90 ? "score-excellent" : 
                                                       result.getScore() >= 80 ? "score-good" : 
                                                       result.getScore() >= 70 ? "score-average" : "score-poor" %>">
                                <%= result.getScore() %>%
                            </td>
                            <td>
                                <% 
                                    String grade;
                                    if (result.getScore() >= 90) grade = "A";
                                    else if (result.getScore() >= 80) grade = "B";
                                    else if (result.getScore() >= 70) grade = "C";
                                    else if (result.getScore() >= 60) grade = "D";
                                    else grade = "F";
                                %>
                                <span class="grade-badge grade-<%= grade.toLowerCase() %>"><%= grade %></span>
                            </td>
                            <td><%= result.getTimeTaken() %> min</td>
                            <td><span style="color: <%= result.isPassed() ? "#4ade80" : "#f87171" %>; font-weight: 600;">
                                <%= result.isPassed() ? "Passed ✓" : "Failed ✗" %>
                            </span></td>
                            <td>
                                <a href="#" class="action-btn btn-view">Review</a>
                                <a href="${pageContext.request.contextPath}/quiz?action=take&id=<%= result.getQuizId() %>" 
                                   class="action-btn btn-view">Retake</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } else { %>
            <!-- Empty State -->
            <div class="empty-state">
                <h3>No results yet</h3>
                <p>You haven't taken any quizzes yet. Start your learning journey now!</p>
                <a href="${pageContext.request.contextPath}/quiz?action=list" class="export-btn" style="padding: 15px 35px; display: inline-block;">
                    Browse Available Quizzes
                </a>
            </div>
        <% } %>
    </div>
    
    <script>
        // Generate performance chart
        function generatePerformanceChart() {
            const chartContainer = document.getElementById('performanceChart');
            if (!chartContainer) return;
            
            // Sample data - in real app, this would come from server
            const performanceData = [
                { quiz: 'Quiz 1', score: 85 },
                { quiz: 'Quiz 2', score: 78 },
                { quiz: 'Quiz 3', score: 92 },
                { quiz: 'Quiz 4', score: 88 },
                { quiz: 'Quiz 5', score: 95 },
                { quiz: 'Quiz 6', score: 81 }
            ];
            
            const maxScore = 100;
            
            performanceData.forEach((data, index) => {
                const barHeight = (data.score / maxScore) * 100;
                
                const bar = document.createElement('div');
                bar.className = 'chart-bar';
                bar.style.height = barHeight + '%';
                bar.innerHTML = '<div class="chart-value">' + data.score + '%</div>' +
                             '<div class="chart-label">' + data.quiz + '</div>';
                
                chartContainer.appendChild(bar);
            });
        }
        
        // Initialize when page loads
        document.addEventListener('DOMContentLoaded', generatePerformanceChart);
    </script>
</body>
</html>