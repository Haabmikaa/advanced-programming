<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Review Quiz Results</title>
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
            --correct: #4ade80;
            --incorrect: #ef4444;
        }

        body {
            background-color: var(--black);
            color: var(--white);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            line-height: 1.6;
        }

        .review-container {
            max-width: 900px;
            margin: 60px auto;
            padding: 40px;
            background: var(--card-bg);
            border-radius: 20px;
            box-shadow: var(--shadow);
            border: 1px solid var(--border);
        }

        .review-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 40px;
            padding-bottom: 25px;
            border-bottom: 1px solid var(--gold);
        }

        .review-header h2 {
            color: var(--gold);
            margin: 0;
            font-size: 2rem;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-weight: 800;
        }

        .score-display {
            text-align: right;
        }

        .score-value {
            font-size: 2.5rem;
            font-weight: 900;
            color: var(--gold);
            line-height: 1;
        }

        .score-label {
            font-size: 0.9rem;
            color: #a0a0a0;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-top: 5px;
        }

        .question-card {
            background: var(--black);
            padding: 30px;
            margin-bottom: 30px;
            border-radius: 15px;
            border: 1px solid var(--border);
            position: relative;
            transition: transform 0.3s ease;
        }

        .question-card:hover {
            transform: translateY(-5px);
            border-color: rgba(212, 175, 55, 0.3);
        }

        .question-card.correct { 
            border-left: 6px solid var(--correct); 
        }
        
        .question-card.incorrect { 
            border-left: 6px solid var(--incorrect); 
        }
        
        .option {
            padding: 15px 20px;
            margin: 12px 0;
            border-radius: 10px;
            background: rgba(255, 255, 255, 0.02);
            border: 1px solid var(--border);
            color: #a0a0a0;
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 16px;
        }

        .option.selected { 
            font-weight: 700; 
            color: var(--white); 
            border-color: var(--gold);
        }

        .option.correct-choice { 
            background: rgba(74, 222, 128, 0.1); 
            border: 1px solid var(--correct); 
            color: var(--correct); 
        }

        .option.wrong-choice { 
            background: rgba(239, 68, 68, 0.1); 
            border: 1px solid var(--incorrect); 
            color: var(--incorrect); 
        }
        
        .status-badge {
            display: inline-flex;
            align-items: center;
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 800;
            margin-bottom: 20px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .status-correct { 
            background: rgba(74, 222, 128, 0.1); 
            color: var(--correct); 
            border: 1px solid var(--correct); 
        }

        .status-incorrect { 
            background: rgba(239, 68, 68, 0.1); 
            color: var(--incorrect); 
            border: 1px solid var(--incorrect); 
        }

        .question-text {
            font-size: 1.25rem;
            font-weight: 700;
            margin-bottom: 20px;
            color: var(--white);
            line-height: 1.4;
        }

        .answer-info {
            margin-top: 25px;
            padding: 15px 20px;
            border-radius: 10px;
            background: rgba(212, 175, 55, 0.05);
            border: 1px dashed rgba(212, 175, 55, 0.2);
            display: flex;
            align-items: center;
            gap: 20px;
            font-size: 15px;
        }

        .answer-info span b {
            color: var(--white);
            font-weight: 700;
        }

        .btn-primary {
            display: inline-block;
            background: var(--gold);
            color: var(--black);
            padding: 16px 40px;
            border-radius: 12px;
            text-decoration: none;
            font-weight: 800;
            transition: all 0.3s;
            border: none;
            text-transform: uppercase;
            letter-spacing: 1px;
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.2);
        }

        .btn-primary:hover {
            background: var(--white);
            color: var(--black);
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(212, 175, 55, 0.3);
        }
    </style>
</head>
<body>
    <div class="review-container">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; border-bottom: 2px solid var(--gold); padding-bottom: 15px;">
            <h2 style="color: var(--gold); margin: 0;">Review: ${result.quiz.title}</h2>
            <div style="text-align: right;">
                <div style="font-size: 24px; font-weight: 700; color: var(--gold);">${result.percentage}%</div>
                <div style="font-size: 14px; color: #ccc;">Grade: ${result.grade}</div>
            </div>
        </div>

        <c:forEach var="ua" items="${userAnswers}" varStatus="status">
            <div class="question-card ${ua.correct ? 'correct' : 'incorrect'}">
                <div class="status-badge ${ua.correct ? 'status-correct' : 'status-incorrect'}">
                    ${ua.correct ? '✓ Correct' : '✗ Incorrect'}
                </div>
                <p style="font-size: 18px; font-weight: 600; margin-bottom: 15px; color: var(--white);">
                    ${status.count}. ${ua.question.questionText}
                </p>
                
                <div class="options-list">
                    <div class="option ${ua.question.correctAnswer eq 'A' ? 'correct-choice' : (ua.userAnswer eq 'A' ? 'wrong-choice' : '')}">
                        A. ${ua.question.optionA}
                    </div>
                    <div class="option ${ua.question.correctAnswer eq 'B' ? 'correct-choice' : (ua.userAnswer eq 'B' ? 'wrong-choice' : '')}">
                        B. ${ua.question.optionB}
                    </div>
                    <div class="option ${ua.question.correctAnswer eq 'C' ? 'correct-choice' : (ua.userAnswer eq 'C' ? 'wrong-choice' : '')}">
                        C. ${ua.question.optionC}
                    </div>
                    <div class="option ${ua.question.correctAnswer eq 'D' ? 'correct-choice' : (ua.userAnswer eq 'D' ? 'wrong-choice' : '')}">
                        D. ${ua.question.optionD}
                    </div>
                </div>

                <div style="margin-top: 15px; padding-top: 15px; border-top: 1px dashed rgba(212, 175, 55, 0.3); font-size: 14px;">
                    <span style="color: #ccc;">Your Answer:</span> 
                    <span style="font-weight: 600; color: var(--white);">${ua.userAnswer}</span>
                    <span style="margin: 0 10px; color: #444;">|</span>
                    <span style="color: #4ade80;">Correct Answer:</span> 
                    <span style="font-weight: 600; color: #4ade80;">${ua.question.correctAnswer}</span>
                </div>
            </div>
        </c:forEach>

        <div style="text-align: center; margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/pages/student/dashboard.jsp" class="btn btn-primary">Back to Dashboard</a>
        </div>
    </div>
</body>
</html>