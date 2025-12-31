<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - QuizWebApp</title>
    <link rel="stylesheet" href="../css/style.css">
    <style>
        :root {
            --gold: #D4AF37;
            --black: #0a0a0a;
            --white: #ffffff;
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
            min-height: 100vh;
        }

        .auth-wrapper {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 60px 20px;
            min-height: calc(100vh - 80px);
        }

        .login-container {
            max-width: 400px;
            margin: 80px auto;
            padding: 40px;
            background: var(--card-bg);
            border-radius: 15px;
            box-shadow: var(--shadow);
            border: 1px solid var(--border);
        }
        
        .login-container h2 {
            text-align: center;
            color: var(--gold);
            margin-bottom: 30px;
            text-transform: uppercase;
            letter-spacing: 3px;
            border-bottom: 1px solid var(--gold);
            padding-bottom: 15px;
            font-weight: 800;
        }
        
        .form-group {
            margin-bottom: 25px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: var(--gold);
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.75rem;
            letter-spacing: 1px;
        }
        
        .form-group input {
            width: 100%;
            padding: 14px;
            background: var(--black);
            border: 1px solid var(--border);
            border-radius: 8px;
            font-size: 16px;
            color: var(--white);
            transition: all 0.3s;
        }

        .form-group input:focus {
            outline: none;
            border-color: var(--gold);
            box-shadow: 0 0 10px rgba(212, 175, 55, 0.1);
        }
        
        .btn {
            width: 100%;
            padding: 14px;
            background: var(--gold);
            color: var(--black);
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 800;
            cursor: pointer;
            text-transform: uppercase;
            letter-spacing: 1px;
            transition: all 0.3s;
        }

        .btn:hover {
            background: var(--white);
            color: var(--black);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.2);
        }
        
        .error {
            color: #ef4444;
            text-align: center;
            margin-bottom: 20px;
            font-weight: 700;
            font-size: 14px;
            padding: 10px;
            background: rgba(239, 68, 68, 0.1);
            border-radius: 6px;
            border: 1px solid rgba(239, 68, 68, 0.2);
        }
        
        .register-link {
            text-align: center;
            margin-top: 25px;
            color: #888;
            font-size: 14px;
        }

        .register-link a {
            color: var(--gold);
            font-weight: 700;
            text-decoration: none;
            margin-left: 5px;
        }

        .register-link a:hover {
            text-decoration: underline;
        }
        
        .test-accounts {
            margin-top: 40px;
            padding: 20px;
            background: rgba(255, 255, 255, 0.02);
            border-radius: 10px;
            font-size: 12px;
            border: 1px solid var(--border);
        }

        .test-accounts h4 {
            color: var(--gold);
            margin-top: 0;
            margin-bottom: 10px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .test-accounts p {
            margin: 5px 0;
            color: #aaa;
        }

        .test-accounts strong {
            color: var(--white);
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
            <a href="${pageContext.request.contextPath}/index.html">Home</a>
            <a href="${pageContext.request.contextPath}/pages/register.jsp">Register</a>
        </div>
    </nav>
    <div class="auth-wrapper">
        <div class="login-container">
            <h2>Login to QuizWebApp</h2>
            
            <% String error = request.getParameter("error");
               if (error != null) { %>
                <div class="error"><%= error %></div>
            <% } %>
            
            <form action="../login" method="POST">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required>
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>
                
                <button type="submit" class="btn">Login</button>
            </form>
            
            <div class="register-link">
                <p>New user? <a href="register.jsp">Create an account</a></p>
            </div>
            
            <div class="test-accounts">
                <h4>Account Example:</h4>
                <p><strong>Student:</strong> alice_student / student123</p>
                <p><strong>Teacher:</strong> john_teacher / teacher123</p>
            </div>
        </div>
    </div>
</body>
</html>