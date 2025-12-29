<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${isEdit}">Edit Quiz</c:when>
            <c:otherwise>Create Quiz</c:otherwise>
        </c:choose>
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        :root {
            --gold: #D4AF37;
            --black: #0a0a0a;
            --white: #ffffff;
            --dark-gray: #1e1e1e;
            --card-bg: #161616;
            --text-muted: #a0a0a0;
            --border: #2a2a2a;
            --shadow: 0 10px 30px rgba(212, 175, 55, 0.1);
        }

        body {
            background-color: var(--black);
            color: var(--white);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.5;
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 900px;
            margin: 40px auto;
            padding: 20px;
        }

        .header-card {
            background: var(--card-bg);
            padding: 30px;
            border-radius: 15px;
            box-shadow: var(--shadow);
            margin-bottom: 30px;
            display: flex;
            align-items: center;
            gap: 25px;
            border: 1px solid var(--border);
        }

        .header-icon {
            width: 70px;
            height: 70px;
            background: var(--gold);
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 35px;
            color: var(--black);
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.3);
        }

        .config-card {
            background: var(--card-bg);
            padding: 30px;
            border-radius: 15px;
            box-shadow: var(--shadow);
            margin-bottom: 30px;
            border: 1px solid var(--border);
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            font-weight: 600;
            margin-bottom: 10px;
            color: var(--gold);
            text-transform: uppercase;
            font-size: 0.85rem;
            letter-spacing: 1px;
        }

        .form-control {
            width: 100%;
            padding: 14px;
            background: var(--black);
            border: 1px solid var(--border);
            border-radius: 10px;
            font-size: 15px;
            color: var(--white);
            box-sizing: border-box;
            transition: all 0.3s;
        }

        .form-control:focus {
            outline: none;
            border-color: var(--gold);
            box-shadow: 0 0 0 3px rgba(212, 175, 55, 0.15);
        }

        .q-card {
            background: var(--card-bg);
            padding: 30px;
            border-radius: 15px;
            box-shadow: var(--shadow);
            margin-bottom: 25px;
            border: 1px solid var(--border);
            position: relative;
            transition: all 0.3s ease;
        }

        .q-card:hover {
            border-color: var(--gold);
        }

        .q-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid var(--border);
        }

        .q-number {
            font-weight: 800;
            color: var(--gold);
            font-size: 20px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .btn-remove {
            background: rgba(239, 68, 68, 0.1);
            color: #ef4444;
            border: 1px solid rgba(239, 68, 68, 0.3);
            padding: 8px 16px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 13px;
            font-weight: 700;
            transition: all 0.3s;
            text-transform: uppercase;
        }

        .btn-remove:hover {
            background: #ef4444;
            color: var(--white);
            transform: translateY(-2px);
        }

        .options-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin-top: 15px;
        }

        .option-input-group {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .option-label {
            font-weight: 800;
            color: var(--gold);
            width: 25px;
            font-size: 18px;
        }

        .actions {
            display: flex;
            flex-direction: column;
            gap: 15px;
            margin-top: 40px;
            margin-bottom: 60px;
        }

        .btn {
            padding: 16px 30px;
            border-radius: 10px;
            font-weight: 700;
            cursor: pointer;
            text-align: center;
            transition: all 0.3s;
            border: none;
            font-size: 16px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .btn-primary {
            background: var(--gold);
            color: var(--black);
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.2);
        }

        .btn-primary:hover {
            background: var(--white);
            color: var(--black);
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(212, 175, 55, 0.3);
        }

        .btn-secondary {
            background: transparent;
            color: var(--gold);
            border: 2px solid var(--gold);
        }

        .btn-secondary:hover {
            background: var(--gold);
            color: var(--black);
            transform: translateY(-3px);
        }

        .tip-box {
            background: rgba(212, 175, 55, 0.05);
            border-left: 5px solid var(--gold);
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 30px;
            font-size: 15px;
            color: var(--white);
            border: 1px solid rgba(212, 175, 55, 0.1);
            border-left: 5px solid var(--gold);
        }

        textarea.form-control {
            min-height: 100px;
            resize: vertical;
        }

        select.form-control {
            cursor: pointer;
            appearance: none;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23D4AF37' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 15px center;
            background-size: 18px;
            padding-right: 45px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header-card">
            <div class="header-icon">
                <c:choose>
                    <c:when test="${isEdit}">✏️</c:when>
                    <c:otherwise>📝</c:otherwise>
                </c:choose>
            </div>
            <div>
                <h1 style="margin:0; font-size: 24px;">
                    <c:choose>
                        <c:when test="${isEdit}">Edit Quiz</c:when>
                        <c:otherwise>Create New Quiz</c:otherwise>
                    </c:choose>
                </h1>
                <p style="margin:4px 0 0; color: var(--text-light);">
                    Fill in the details below to 
                    <c:choose>
                        <c:when test="${isEdit}">update</c:when>
                        <c:otherwise>publish</c:otherwise>
                    </c:choose>
                    your quiz.
                </p>
            </div>
        </div>

        <div class="tip-box">
            <strong>Pro Tip:</strong> Clear and concise questions lead to better student engagement and more accurate results!
        </div>

        <div class="config-card">
            <input type="hidden" id="quizId" value="<c:out value='${not empty quiz and quiz.quizId > 0 ? quiz.quizId : ""}'/>">
            
            <div class="form-group">
                <label for="title">Quiz Title <span style="color: #ef4444;">*</span></label>
                <input type="text" id="title" class="form-control" placeholder="e.g., Introduction to Java" value="<c:out value='${not empty quiz ? quiz.title : ""}'/>" required>
            </div>

            <div class="form-group">
                <label for="description">Description</label>
                <textarea id="description" class="form-control" rows="2" placeholder="What is this quiz about?"><c:out value='${not empty quiz ? quiz.description : ""}'/></textarea>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                <div class="form-group">
                    <label for="category">Category <span style="color: #ef4444;">*</span></label>
                    <input type="text" id="category" class="form-control" placeholder="e.g., Computer Science" value="<c:out value='${not empty quiz ? quiz.category : ""}'/>" required>
                </div>
                <div class="form-group">
                    <label for="duration">Time Limit (Minutes)</label>
                    <input type="number" id="duration" class="form-control" min="1" value="<c:out value='${not empty quiz and quiz.durationMinutes > 0 ? quiz.durationMinutes : 30}'/>">
                </div>
            </div>
        </div>

        <div id="questionsContainer">
            <!-- Questions will be added here via JavaScript -->
        </div>

        <div class="actions">
            <button type="button" onclick="addNewQuestion()" class="btn btn-secondary">+ Add Another Question</button>
            <button type="button" onclick="saveQuiz()" id="saveBtn" class="btn btn-primary">Save and Publish Quiz</button>
        </div>
    </div>

    <script id="serverData" type="application/json">
        {
            "questions": <c:out value="${not empty questionsJson ? questionsJson : '[]'}" escapeXml="false" />,
            "contextPath": "${pageContext.request.contextPath}"
        }
    </script>

    <script>
        let qCount = 0;
        const container = document.getElementById('questionsContainer');
        const serverData = JSON.parse(document.getElementById('serverData').textContent);

        function addNewQuestion(data = null) {
            qCount++;
            const qId = `q-\${Date.now()}-\${qCount}`;
            const card = document.createElement('div');
            card.className = 'q-card';
            card.id = qId;
            
            const text = data ? data.questionText : '';
            const a = data ? data.optionA : '';
            const b = data ? data.optionB : '';
            const c = data ? data.optionC : '';
            const d = data ? data.optionD : '';
            const correct = data ? data.correctAnswer : 'A';

            card.innerHTML = `
                <div class="q-header">
                    <span class="q-number">Question \${qCount}</span>
                    <button type="button" class="btn-remove" onclick="removeQuestion('\${qId}')">Remove</button>
                </div>
                <div class="form-group">
                    <input type="text" class="form-control q-text" placeholder="Enter your question here..." value="\${escapeHtml(text)}" required>
                </div>
                <div class="options-grid">
                    <div class="option-input-group">
                        <span class="option-label">A</span>
                        <input type="text" class="form-control opt-a" placeholder="Option A" value="\${escapeHtml(a)}" required>
                    </div>
                    <div class="option-input-group">
                        <span class="option-label">B</span>
                        <input type="text" class="form-control opt-b" placeholder="Option B" value="\${escapeHtml(b)}" required>
                    </div>
                    <div class="option-input-group">
                        <span class="option-label">C</span>
                        <input type="text" class="form-control opt-c" placeholder="Option C" value="\${escapeHtml(c)}" required>
                    </div>
                    <div class="option-input-group">
                        <span class="option-label">D</span>
                        <input type="text" class="form-control opt-d" placeholder="Option D" value="\${escapeHtml(d)}" required>
                    </div>
                </div>
                <div class="form-group" style="margin-top: 16px; margin-bottom: 0;">
                    <label style="font-size: 12px; color: var(--text-light);">Correct Answer</label>
                    <select class="form-control q-correct" style="width: auto; min-width: 120px;">
                        <option value="A" \${correct === 'A' ? 'selected' : ''}>Option A</option>
                        <option value="B" \${correct === 'B' ? 'selected' : ''}>Option B</option>
                        <option value="C" \${correct === 'C' ? 'selected' : ''}>Option C</option>
                        <option value="D" \${correct === 'D' ? 'selected' : ''}>Option D</option>
                    </select>
                </div>
            `;
            container.appendChild(card);
            reindexQuestions();
        }

        function removeQuestion(id) {
            const el = document.getElementById(id);
            if (el) {
                el.remove();
                reindexQuestions();
            }
        }

        function reindexQuestions() {
            const cards = container.querySelectorAll('.q-card');
            cards.forEach((card, index) => {
                card.querySelector('.q-number').textContent = `Question \${index + 1}`;
            });
            qCount = cards.length;
        }

        async function saveQuiz() {
            const title = document.getElementById('title').value.trim();
            const category = document.getElementById('category').value.trim();
            
            if (!title || !category) {
                alert('Please fill in the Quiz Title and Category.');
                return;
            }

            const questions = [];
            const cards = container.querySelectorAll('.q-card');
            
            if (cards.length === 0) {
                alert('Please add at least one question.');
                return;
            }

            let isValid = true;
            cards.forEach(card => {
                const text = card.querySelector('.q-text').value.trim();
                const a = card.querySelector('.opt-a').value.trim();
                const b = card.querySelector('.opt-b').value.trim();
                const c = card.querySelector('.opt-c').value.trim();
                const d = card.querySelector('.opt-d').value.trim();
                
                if (!text || !a || !b || !c || !d) {
                    isValid = false;
                }

                questions.push({
                    text: text,
                    a: a,
                    b: b,
                    c: c,
                    d: d,
                    correct: card.querySelector('.q-correct').value
                });
            });

            if (!isValid) {
                alert('Please fill in all question texts and options.');
                return;
            }

            const quizId = document.getElementById('quizId').value;
            const payload = {
                quizId: quizId ? parseInt(quizId) : null,
                title: title,
                category: category,
                description: document.getElementById('description').value.trim(),
                duration: parseInt(document.getElementById('duration').value) || 30,
                questions: questions
            };

            const saveBtn = document.getElementById('saveBtn');
            saveBtn.disabled = true;
            saveBtn.textContent = 'Saving...';

            try {
                const response = await fetch(serverData.contextPath + '/quiz?action=saveFull', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                const result = await response.json();
                if (response.ok && result.success) {
                    alert('Quiz saved successfully!');
                    window.location.href = serverData.contextPath + '/pages/teacher/dashboard.jsp';
                } else {
                    throw new Error(result.error || 'Failed to save quiz');
                }
            } catch (err) {
                alert('Error: ' + err.message);
                saveBtn.disabled = false;
                saveBtn.textContent = 'Save and Publish Quiz';
            }
        }

        function escapeHtml(text) {
            if (!text) return '';
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        // Initialize
        if (serverData.questions && serverData.questions.length > 0) {
            serverData.questions.forEach(q => addNewQuestion(q));
        } else {
            addNewQuestion(); // Add one empty question by default
        }
    </script>
</body>
</html>
