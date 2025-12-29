<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.quizapp.model.User, com.quizapp.model.Quiz, com.quizapp.model.Question" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
        return;
    }
    
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    
    if (quiz == null || questions == null) {
        response.sendRedirect(request.getContextPath() + "/quiz?action=list");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title><%= quiz.getTitle() %> - Quiz Web App</title>
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

        .quiz-container {
            max-width: 850px;
            margin: 60px auto;
            padding: 40px;
            background: var(--card-bg);
            border-radius: 20px;
            box-shadow: var(--shadow);
            border: 1px solid var(--border);
        }
        
        .quiz-header {
            text-align: center;
            margin-bottom: 40px;
            padding-bottom: 30px;
            border-bottom: 1px solid var(--border);
        }
        
        .quiz-header h1 {
            color: var(--gold);
            margin-bottom: 15px;
            font-size: 2.5rem;
            text-transform: uppercase;
            letter-spacing: 2px;
            font-weight: 800;
        }
        
        .quiz-meta {
            display: flex;
            justify-content: center;
            gap: 25px;
            margin-top: 20px;
            color: #a0a0a0;
        }
        
        .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
            background: var(--black);
            padding: 8px 16px;
            border-radius: 8px;
            border: 1px solid var(--border);
            font-size: 0.9rem;
        }
        
        .timer {
            position: fixed;
            top: 30px;
            right: 30px;
            background: var(--gold);
            color: var(--black);
            padding: 18px 30px;
            border-radius: 12px;
            font-size: 20px;
            font-weight: 800;
            box-shadow: 0 5px 20px rgba(212, 175, 55, 0.4);
            z-index: 1000;
            border: 2px solid var(--black);
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .question-container {
            margin-bottom: 40px;
            animation: fadeIn 0.5s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .question-number {
            display: inline-block;
            background: var(--gold);
            color: var(--black);
            padding: 6px 18px;
            border-radius: 25px;
            font-size: 14px;
            margin-bottom: 20px;
            font-weight: 800;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .question-text {
            font-size: 22px;
            line-height: 1.5;
            margin-bottom: 30px;
            color: var(--white);
            font-weight: 500;
        }
        
        .options-container {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }
        
        .option {
            display: flex;
            align-items: center;
        }
        
        .option input[type="radio"] {
            display: none;
        }
        
        .option label {
            flex: 1;
            padding: 18px 25px;
            border: 2px solid var(--border);
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            color: var(--white);
            background: rgba(255, 255, 255, 0.02);
            font-size: 17px;
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .option label:hover {
            border-color: var(--gold);
            background: rgba(212, 175, 55, 0.05);
            transform: translateX(5px);
        }
        
        .option input[type="radio"]:checked + label {
            border-color: var(--gold);
            background: rgba(212, 175, 55, 0.15);
            color: var(--gold);
            box-shadow: 0 0 20px rgba(212, 175, 55, 0.1);
            font-weight: 600;
        }

        .option label strong {
            color: var(--gold);
            font-size: 20px;
            width: 25px;
        }
        
        .short-answer input {
            width: 100%;
            padding: 18px 25px;
            background: rgba(255, 255, 255, 0.02);
            border: 2px solid var(--border);
            border-radius: 12px;
            font-size: 18px;
            color: var(--white);
            transition: all 0.3s;
        }

        .short-answer input:focus {
            outline: none;
            border-color: var(--gold);
            background: rgba(212, 175, 55, 0.05);
            box-shadow: 0 0 20px rgba(212, 175, 55, 0.1);
        }
        
        .quiz-navigation {
            display: flex;
            justify-content: space-between;
            margin-top: 40px;
            padding-top: 30px;
            border-top: 1px solid var(--border);
        }
        
        .btn-nav {
            padding: 15px 35px;
            background: var(--gold);
            color: var(--black);
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .btn-nav:hover:not(:disabled) {
            background: #e5c05a;
            color: var(--black);
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.2);
        }
        
        .btn-nav:disabled {
            background: var(--dark-gray);
            color: #555;
            cursor: not-allowed;
            border: 1px solid var(--border);
        }

        #prevBtn {
            background: transparent;
            color: var(--gold);
            border: 2px solid var(--gold);
        }

        #prevBtn:hover:not(:disabled) {
            background: var(--gold);
            color: var(--black);
        }
        
        .question-progress {
            display: flex;
            justify-content: center;
            gap: 12px;
            margin: 30px 0;
            flex-wrap: wrap;
        }
        
        .progress-dot {
            width: 14px;
            height: 14px;
            border-radius: 50%;
            background: var(--dark-gray);
            cursor: pointer;
            border: 2px solid var(--border);
            transition: all 0.3s;
        }
        
        .progress-dot.active {
            background: var(--gold);
            box-shadow: 0 0 10px var(--gold);
            transform: scale(1.3);
            border-color: var(--gold);
        }
        
        .progress-dot.answered {
            border-color: var(--gold);
            background: rgba(212, 175, 55, 0.3);
        }

        #submitBtn {
            background: var(--gold);
            color: var(--black);
            min-width: 200px;
            font-size: 18px;
            box-shadow: 0 5px 20px rgba(212, 175, 55, 0.3);
        }

        #submitBtn:hover {
            background: #e5c05a;
            transform: translateY(-3px) scale(1.05);
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
    <div class="timer">
        <span>⏱ Time Left:</span>
        <span id="timer-display">--:--</span>
    </div>
    
    <div class="quiz-container">
        <div class="quiz-header">
            <h1><%= quiz.getTitle() %></h1>
            <p><%= quiz.getDescription() %></p>
            <div class="quiz-meta">
                <div class="meta-item">📝 <%= questions.size() %> Questions</div>
                <div class="meta-item">⏱️ <%= quiz.getDurationMinutes() %> minutes</div>
                <div class="meta-item">🎯 <%= quiz.getDifficulty() %> Level</div>
            </div>
        </div>
        
        <!-- Important: Ensure ID is quizForm for WebSocket interaction -->
        <form id="quizForm" action="${pageContext.request.contextPath}/result" method="post">
            <input type="hidden" name="quizId" value="<%= quiz.getQuizId() %>">
            
            <div class="question-progress">
                <% for (int i = 0; i < questions.size(); i++) { %>
                    <div class="progress-dot <%= i == 0 ? "active" : "" %>" 
                         onclick="showQuestion(<%= i %>)"></div>
                <% } %>
            </div>
            
            <% for (int i = 0; i < questions.size(); i++) { 
                Question question = questions.get(i);
            %>
                <div class="question-container" id="question-<%= i %>" 
                     style="<%= i != 0 ? "display: none;" : "" %>">
                    <div class="question-number">Question <%= i + 1 %> of <%= questions.size() %></div>
                    <div class="question-text"><%= question.getQuestionText() %></div>
                    
                    <% if (question.isMultipleChoice()) { %>
                        <div class="options-container">
                            <% String[] options = {"A", "B", "C", "D"}; %>
                            <% String[] optionTexts = {question.getOptionA(), question.getOptionB(), 
                                                      question.getOptionC(), question.getOptionD()}; %>
                            
                            <% for (int j = 0; j < 4; j++) { 
                                if (optionTexts[j] != null && !optionTexts[j].isEmpty()) {
                            %>
                                <div class="option">
                                    <input type="radio" 
                                           name="question_<%= question.getQuestionId() %>" 
                                           id="q<%= question.getQuestionId() %>_opt<%= j %>" 
                                           value="<%= options[j] %>">
                                    <label for="q<%= question.getQuestionId() %>_opt<%= j %>">
                                        <strong><%= options[j] %>.</strong> <%= optionTexts[j] %>
                                    </label>
                                </div>
                            <%   }
                               } %>
                        </div>
                    <% } else if (question.isTrueFalse()) { %>
                        <div class="options-container">
                            <div class="option">
                                <input type="radio" 
                                       name="question_<%= question.getQuestionId() %>" 
                                       id="q<%= question.getQuestionId() %>_true" 
                                       value="True">
                                <label for="q<%= question.getQuestionId() %>_true">True</label>
                            </div>
                            <div class="option">
                                <input type="radio" 
                                       name="question_<%= question.getQuestionId() %>" 
                                       id="q<%= question.getQuestionId() %>_false" 
                                       value="False">
                                <label for="q<%= question.getQuestionId() %>_false">False</label>
                            </div>
                        </div>
                    <% } else { %>
                        <div class="short-answer">
                            <input type="text" 
                                   name="question_<%= question.getQuestionId() %>" 
                                   placeholder="Type your answer here...">
                        </div>
                    <% } %>
                </div>
            <% } %>
            
            <div class="quiz-navigation">
                <button type="button" class="btn-nav" id="prevBtn" 
                        onclick="prevQuestion()" disabled>Previous</button>
                <button type="button" class="btn-nav" id="nextBtn" 
                        onclick="nextQuestion()">Next</button>
            </div>
            
            <div style="text-align: center; margin-top: 20px;">
                <button type="submit" class="btn-nav" id="submitBtn"
                        style="background: var(--gold); color: var(--black); min-width: 150px; border: 1px solid var(--gold);">Submit Quiz</button>
            </div>
        </form>
    </div>
    
    <script>
        // --- Navigation Logic ---
        let currentQuestion = 0;
        const totalQuestions = <%= questions.size() %>;
        
        // Initialize UI state on load
        window.addEventListener('load', () => {
            console.log("Quiz initialized with " + totalQuestions + " questions.");
            if (totalQuestions > 0) {
                showQuestion(0);
            }
        });

        function showQuestion(index) {
            console.log("Showing question: " + index);
            
            // Hide all questions
            const questions = document.querySelectorAll('.question-container');
            questions.forEach(q => q.style.display = 'none');
            
            // Show the target question
            const targetQ = document.getElementById('question-' + index);
            if (targetQ) {
                targetQ.style.display = 'block';
                currentQuestion = index;
            } else {
                console.error("Could not find question container for index: " + index);
            }
            
            // Update progress dots
            const dots = document.querySelectorAll('.progress-dot');
            dots.forEach((dot, i) => {
                dot.classList.remove('active');
                if (i === index) dot.classList.add('active');
            });
            
            // Update buttons state
            const prevBtn = document.getElementById('prevBtn');
            const nextBtn = document.getElementById('nextBtn');
            const submitBtn = document.getElementById('submitBtn');
            
            if (prevBtn) prevBtn.disabled = (index === 0);
            if (nextBtn) {
                if (index === totalQuestions - 1) {
                    nextBtn.style.display = 'none';
                    if (submitBtn) submitBtn.style.display = 'inline-block';
                } else {
                    nextBtn.style.display = 'inline-block';
                    nextBtn.disabled = false;
                    // if (submitBtn) submitBtn.style.display = 'none'; // Optional: hide submit until last
                }
            }
        }
        
        function nextQuestion() {
            if (currentQuestion < totalQuestions - 1) {
                showQuestion(currentQuestion + 1);
            }
        }
        
        function prevQuestion() {
            if (currentQuestion > 0) {
                showQuestion(currentQuestion - 1);
            }
        }

        // --- Local Timer Logic ---
        let totalSeconds = <%= quiz.getDurationMinutes() %> * 60;
        const timerElement = document.getElementById('timer-display');
        
        function updateTimer() {
            const minutes = Math.floor(totalSeconds / 60);
            const seconds = totalSeconds % 60;
            timerElement.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
            
            if (totalSeconds <= 0) {
                clearInterval(timerInterval);
                alert('Time is up! Your quiz will be submitted automatically.');
                document.getElementById('quizForm').submit();
            } else {
                totalSeconds--;
            }
        }
        const timerInterval = setInterval(updateTimer, 1000);

        // --- WebSocket Real-Time Monitoring ---
        const protocol = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
        const socketUrl = protocol + window.location.host + "${pageContext.request.contextPath}/quizws";
        const socket = new WebSocket(socketUrl);

        socket.onopen = function() {
            console.log("Connected to Real-time Quiz Monitor");
            // Join the specific quiz room so teacher can track this student
            socket.send(JSON.stringify({
                type: "join",
                quizId: <%= quiz.getQuizId() %>,
                userId: <%= user.getUserId() %>
            }));
        };

        socket.onmessage = function(event) {
            try {
                const data = JSON.parse(event.data);
                
                // If teacher triggers a force-stop or session termination
                if (data.type === "terminate_quiz") {
                    clearInterval(timerInterval); // Stop local clock
                    alert("⚠️ " + data.message);
                    
                    const quizForm = document.getElementById('quizForm');
                    if (quizForm) {
                        // Mark as forced timeout
                        const timeoutFlag = document.createElement("input");
                        timeoutFlag.type = "hidden";
                        timeoutFlag.name = "status";
                        timeoutFlag.value = "TIMEOUT";
                        quizForm.appendChild(timeoutFlag);
                        
                        quizForm.submit(); 
                    }
                }
            } catch (e) {
                console.warn("WebSocket message error:", e);
            }
        };

        socket.onclose = function() {
            console.log("Disconnected from Real-time Monitor");
        };

        // --- UI Helper: Mark Answered ---
        document.querySelectorAll('input[type="radio"], input[type="text"]').forEach(input => {
            input.addEventListener('change', function() {
                const progressDots = document.querySelectorAll('.progress-dot');
                progressDots[currentQuestion].classList.add('answered');
            });
        });
    </script>
</body>
</html>