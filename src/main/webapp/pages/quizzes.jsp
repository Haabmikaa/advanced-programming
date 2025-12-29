<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.quizapp.model.User, com.quizapp.model.Quiz" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Available Quizzes - Quiz Web App</title>
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

        .quizzes-container {
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
            text-align: center;
            border: 1px solid var(--border);
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
        
        .filters {
            background: var(--card-bg);
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            margin-bottom: 40px;
            border: 1px solid var(--border);
        }
        
        .filter-row {
            display: flex;
            gap: 25px;
            flex-wrap: wrap;
        }
        
        .filter-group {
            flex: 1;
            min-width: 250px;
        }
        
        .filter-group label {
            display: block;
            margin-bottom: 10px;
            color: var(--gold);
            font-weight: 600;
            text-transform: uppercase;
            font-size: 12px;
            letter-spacing: 1px;
        }
        
        .filter-group select,
        .filter-group input {
            width: 100%;
            padding: 12px 15px;
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid var(--border);
            border-radius: 8px;
            font-size: 15px;
            color: var(--white);
            transition: all 0.3s;
        }

        .filter-group select:focus,
        .filter-group input:focus {
            outline: none;
            border-color: var(--gold);
            background: rgba(255, 255, 255, 0.08);
            box-shadow: 0 0 10px rgba(212, 175, 55, 0.1);
        }

        .filter-group select option {
            background: var(--dark-gray);
            color: var(--white);
        }
        
        .quiz-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 30px;
            margin-bottom: 50px;
        }
        
        .quiz-card {
            background: var(--card-bg);
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            border: 1px solid var(--border);
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            display: flex;
            flex-direction: column;
        }
        
        .quiz-card:hover {
            transform: translateY(-10px);
            border-color: var(--gold);
            box-shadow: 0 20px 40px rgba(212, 175, 55, 0.15);
        }
        
        .quiz-header {
            background: linear-gradient(135deg, rgba(212, 175, 55, 0.1) 0%, transparent 100%);
            color: var(--gold);
            padding: 25px;
            border-bottom: 1px solid var(--border);
        }
        
        .quiz-header h3 {
            margin: 0 0 12px 0;
            font-size: 22px;
            letter-spacing: 0.5px;
        }
        
        .quiz-category {
            display: inline-block;
            background: rgba(212, 175, 55, 0.1);
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 700;
            border: 1px solid rgba(212, 175, 55, 0.3);
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .quiz-content {
            padding: 25px;
            flex-grow: 1;
        }
        
        .quiz-meta {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
            color: #a0a0a0;
            font-size: 14px;
        }
        
        .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .quiz-description {
            color: #ccc;
            margin-bottom: 25px;
            line-height: 1.6;
            font-size: 15px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .difficulty {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 11px;
            font-weight: 700;
            margin-bottom: 20px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .difficulty-easy { background: rgba(74, 222, 128, 0.1); color: #4ade80; border: 1px solid rgba(74, 222, 128, 0.3); }
        .difficulty-medium { background: rgba(251, 191, 36, 0.1); color: #fbbf24; border: 1px solid rgba(251, 191, 36, 0.3); }
        .difficulty-hard { background: rgba(239, 68, 68, 0.1); color: #ef4444; border: 1px solid rgba(239, 68, 68, 0.3); }
        
        .quiz-footer {
            padding: 20px 25px;
            background: rgba(212, 175, 55, 0.05);
            border-top: 1px solid var(--border);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .btn-quiz {
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 700;
            font-size: 14px;
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
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.3);
        }

        .btn-outline {
            background: transparent;
            color: var(--white);
            border: 1px solid var(--border);
        }

        .btn-outline:hover {
            border-color: var(--gold);
            color: var(--gold);
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
    <div class="quizzes-container">
        <!-- Header with Quiz Banner & Inspiration -->
        <div class="page-header">
            <h1>Available Quizzes</h1>
            <p>Test your knowledge and grow your skills with our curated collection of quizzes</p>
        </div>
        
        <!-- Filters -->
        <div class="filters">
            <div class="filter-row">
                <div class="filter-group">
                    <label for="category">Category</label>
                    <select id="category">
                        <option value="">All Categories</option>
                        <option value="programming">Programming</option>
                        <option value="mathematics">Mathematics</option>
                        <option value="science">Science</option>
                        <option value="history">History</option>
                        <option value="language">Language</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label for="difficulty">Difficulty</label>
                    <select id="difficulty">
                        <option value="">All Levels</option>
                        <option value="easy">Easy</option>
                        <option value="medium">Medium</option>
                        <option value="hard">Hard</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label for="duration">Duration</label>
                    <select id="duration">
                        <option value="">Any Duration</option>
                        <option value="short">Short (1-10 min)</option>
                        <option value="medium">Medium (11-30 min)</option>
                        <option value="long">Long (31+ min)</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label for="search">Search</label>
                    <input type="text" id="search" placeholder="Search quizzes...">
                </div>
            </div>
        </div>
        
        <!-- Quiz Grid -->
        <div class="quiz-grid">
            <% if (quizzes != null && !quizzes.isEmpty()) { 
                for (Quiz quiz : quizzes) { 
            %>
                <div class="quiz-card">
                    <div class="quiz-header">
                        <span class="quiz-category"><%= quiz.getCategory() %></span>
                        <h3><%= quiz.getTitle() %></h3>
                    </div>
                    <div class="quiz-content">
                        <div class="quiz-meta">
                            <div class="meta-item">⏱️ <%= quiz.getDurationMinutes() %> min</div>
                            <div class="meta-item">📝 10 questions</div>
                            <div class="meta-item">👤 By: <%= quiz.getCreatedBy() %></div>
                        </div>
                        <div class="quiz-description">
                            <%= quiz.getDescription() %>
                        </div>
                        <span class="difficulty <%= "difficulty-" + quiz.getDifficulty().name().toLowerCase() %>">
                            <%= quiz.getDifficulty() %>
                        </span>
                    </div>
                    <div class="quiz-footer">
                        <span>🎯 Passing: 70%</span>
                        <div>
                            <button class="btn-quiz btn-outline quiz-details-btn" data-quiz-id="<%= quiz.getQuizId() %>" type="button">Details</button>
                            <a href="${pageContext.request.contextPath}/quiz?action=take&id=<%= quiz.getQuizId() %>" 
                               class="btn-quiz btn-primary">Take Quiz</a>
                        </div>
                    </div>
                </div>
            <% } } else { %>
                <div class="empty-state">
                    <h3>No quizzes available</h3>
                    <p>Check back later for new quizzes or create your own!</p>
                    <% if (user != null && user.isTeacher()) { %>
                        <a href="${pageContext.request.contextPath}/quiz?action=create" 
                           class="btn-quiz btn-primary" style="margin-top: 15px;">
                            Create Your First Quiz
                        </a>
                    <% } %>
                </div>
            <% } %>
        </div>
        
        <!-- Pagination -->
        <div class="pagination">
            <button class="page-btn">« Previous</button>
            <button class="page-btn active">1</button>
            <button class="page-btn">2</button>
            <button class="page-btn">3</button>
            <button class="page-btn">4</button>
            <button class="page-btn">5</button>
            <button class="page-btn">Next »</button>
        </div>
    </div>
    
    <script>
        // Filter functionality
        document.getElementById('search').addEventListener('input', filterQuizzes);
        document.getElementById('category').addEventListener('change', filterQuizzes);
        document.getElementById('difficulty').addEventListener('change', filterQuizzes);
        document.getElementById('duration').addEventListener('change', filterQuizzes);
        
        function filterQuizzes() {
            const searchTerm = document.getElementById('search').value.toLowerCase();
            const category = document.getElementById('category').value;
            const difficulty = document.getElementById('difficulty').value;
            const duration = document.getElementById('duration').value;
            
            const quizCards = document.querySelectorAll('.quiz-card');
            
            quizCards.forEach(card => {
                const title = card.querySelector('h3').textContent.toLowerCase();
                const desc = card.querySelector('.quiz-description').textContent.toLowerCase();
                const cardCategory = card.querySelector('.quiz-category').textContent.toLowerCase();
                const cardDifficulty = card.querySelector('.difficulty').textContent.toLowerCase();
                const durationText = card.querySelector('.meta-item').textContent;
                const cardDuration = parseInt(durationText.match(/\d+/));
                
                let matches = true;
                
                // Search term
                if (searchTerm && !title.includes(searchTerm) && !desc.includes(searchTerm)) {
                    matches = false;
                }
                
                // Category filter
                if (category && !cardCategory.includes(category)) {
                    matches = false;
                }
                
                // Difficulty filter
                if (difficulty && !cardDifficulty.includes(difficulty)) {
                    matches = false;
                }
                
                // Duration filter
                if (duration && cardDuration) {
                    if (duration === 'short' && cardDuration > 10) matches = false;
                    if (duration === 'medium' && (cardDuration <= 10 || cardDuration > 30)) matches = false;
                    if (duration === 'long' && cardDuration <= 30) matches = false;
                }
                
                card.style.display = matches ? 'block' : 'none';
            });
            
            // Show empty state if no matches
            const visibleCards = document.querySelectorAll('.quiz-card[style="display: block"]');
            const emptyState = document.querySelector('.empty-state');
            if (emptyState) {
                emptyState.style.display = visibleCards.length === 0 ? 'block' : 'none';
            }
        }
    </script>
<!-- Quiz Details Modal -->
<div id="quizDetailsModal" class="modal" style="display:none; position:fixed; top:0; left:0; width:100vw; height:100vh; background:rgba(0,0,0,0.85); z-index:10000; align-items:center; justify-content:center;">
  <div style="background:var(--card-bg); border: 1px solid var(--border); border-radius:12px; max-width:520px; width:90vw; padding:32px 24px; box-shadow:var(--shadow); position:relative; color: var(--white);">
    <button id="closeQuizDetailsModal" style="position:absolute;top:12px;right:18px;font-size:24px;border:none; background:none; cursor:pointer; color: var(--gold);">×</button>
    <div id="quizDetailsContent">
      <!-- Populated by JS -->
      <div style="text-align:center;"><em>Loading...</em></div>
    </div>
  </div>
</div>
</body>
</html>