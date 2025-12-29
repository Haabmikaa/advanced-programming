<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.quizapp.model.Quiz, com.quizapp.model.Question, java.util.List" %>
<%
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    if (quiz == null) {
        response.sendRedirect(request.getContextPath() + "/quiz?action=list");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz Details - <%= quiz.getTitle() %></title>
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

        .details-container { 
            max-width: 700px; 
            margin: 60px auto; 
            padding: 40px; 
            background: var(--card-bg); 
            border-radius: 15px; 
            box-shadow: var(--shadow);
            border: 1px solid var(--border);
        }

        .details-header { text-align: center; margin-bottom: 30px; }
        .details-header h1 { color: var(--gold); margin-bottom: 10px; }
        
        .meta-info { 
            display: flex; 
            justify-content: center; 
            gap: 20px; 
            margin: 24px 0; 
            flex-wrap: wrap;
        }

        .meta-item { 
            background: var(--black); 
            padding: 15px 25px; 
            border-radius: 10px; 
            font-size: 16px; 
            text-align: center; 
            border: 1px solid var(--border);
            color: var(--gold);
            min-width: 120px;
        }

        .desc { 
            text-align: center; 
            font-size: 18px; 
            color: #a0a0a0; 
            margin-bottom: 25px; 
            line-height: 1.6;
        }

        .questions-summary {
            margin-top: 30px; 
            padding: 20px;
            background: rgba(212, 175, 55, 0.05);
            border-radius: 10px;
            border: 1px solid var(--border);
        }

        .questions-summary h3 { 
            margin-top: 0;
            margin-bottom: 15px; 
            color: var(--gold);
            font-size: 20px;
        }

        ul {margin: 0; padding-left: 20px; list-style-type: none;}
        li {
            margin-bottom: 10px; 
            font-size: 16px;
            display: flex;
            align-items: center;
        }
        li::before {
            content: "•";
            color: var(--gold);
            font-weight: bold;
            display: inline-block;
            width: 1em;
            margin-left: -1em;
        }

        .btns { margin-top: 40px; display: flex; justify-content: center; gap: 20px; }
        
        .btn { 
            padding: 14px 30px; 
            border-radius: 8px; 
            text-decoration: none; 
            font-weight: 600; 
            font-size: 16px; 
            transition: all 0.3s; 
            display: inline-block;
            text-align: center;
        }

        .btn-primary { 
            background: var(--gold); 
            color: var(--black); 
            border: none;
        }

        .btn-primary:hover { 
            background: #b8952e; 
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.3);
        }

        .btn-outline {
            background: transparent;
            color: var(--gold);
            border: 1px solid var(--gold);
        }

        .btn-outline:hover {
            background: var(--gold);
            color: var(--black);
        }
    </style>
</head>
<body>
    <div class="details-container">
        <div class="details-header">
            <h1><%= quiz.getTitle() %> - Quiz Details</h1>
            <div class="desc"><%= quiz.getDescription() %></div>
            <div class="meta-info">
                <div class="meta-item">📝 <%= (questions != null) ? questions.size() : 0 %> Questions</div>
                <div class="meta-item">⏱️ <%= quiz.getDurationMinutes() %> minutes</div>
                <div class="meta-item">🎯 Difficulty: <%= quiz.getDifficulty() %></div>
            </div>
        </div>
        <div class="questions-summary">
            <h3>Question Types</h3>
            <ul>
                <li>Multiple choice: <%= (questions != null) ? questions.stream().filter(q -> q.isMultipleChoice()).count() : 0 %></li>
                <li>True/False: <%= (questions != null) ? questions.stream().filter(q -> q.isTrueFalse()).count() : 0 %></li>
                <li>Short Answer: <%= (questions != null) ? questions.stream().filter(q -> !(q.isMultipleChoice() || q.isTrueFalse())).count() : 0 %></li>
            </ul>
        </div>
        <div class="btns">
            <a href="${pageContext.request.contextPath}/quiz?action=take&id=<%= quiz.getQuizId() %>" class="btn btn-primary">Take this Quiz</a>
            <a href="javascript:history.back()" class="btn btn-outline">Back</a>
        </div>
    </div>
</body>
</html>


