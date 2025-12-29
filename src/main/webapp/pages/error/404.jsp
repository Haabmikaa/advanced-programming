<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>404 - Page Not Found</title>
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
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .error-container {
            max-width: 600px;
            width: 90%;
            padding: 50px;
            text-align: center;
            background: var(--card-bg);
            border-radius: 20px;
            box-shadow: var(--shadow);
            border: 1px solid var(--border);
        }
        
        .error-code {
            font-size: 140px;
            font-weight: 900;
            color: var(--gold);
            line-height: 1;
            margin-bottom: 20px;
            text-shadow: 0 5px 15px rgba(212, 175, 55, 0.3);
        }
        
        .error-title {
            font-size: 36px;
            color: var(--white);
            margin-bottom: 20px;
            font-weight: 700;
        }
        
        .error-message {
            color: #a0a0a0;
            margin-bottom: 40px;
            font-size: 18px;
            line-height: 1.8;
        }
        
        .error-actions {
            display: flex;
            gap: 20px;
            justify-content: center;
            margin-top: 30px;
        }
        
        .btn-error {
            padding: 14px 28px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 700;
            transition: all 0.3s;
            font-size: 16px;
        }
        
        .btn-primary {
            background: var(--gold);
            color: var(--black);
        }
        
        .btn-secondary {
            background: transparent;
            color: var(--gold);
            border: 1px solid var(--gold);
        }
        
        .btn-primary:hover {
            background: #b8952e;
            transform: translateY(-3px);
            box-shadow: 0 10px 20px rgba(212, 175, 55, 0.2);
        }
        
        .btn-secondary:hover {
            background: var(--gold);
            color: var(--black);
        }
        
        .error-image {
            font-size: 100px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-image">🔍</div>
        <div class="error-code">404</div>
        <h1 class="error-title">Page Not Found</h1>
        <p class="error-message">
            The page you're looking for doesn't exist or has been moved to another location.
            Please check the URL or navigate back to our homepage.
        </p>
        
        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/index.html" class="btn-error btn-primary">
                ← Back to Home
            </a>
            <a href="${pageContext.request.contextPath}/pages/login.jsp" class="btn-error btn-secondary">
                Go to Login
            </a>
        </div>
    </div>
</body>
</html>