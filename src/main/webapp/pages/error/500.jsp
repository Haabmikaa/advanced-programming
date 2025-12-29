<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>500 - Internal Server Error</title>
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
            border: 1px solid #ef4444;
        }
        
        .error-code {
            font-size: 140px;
            font-weight: 900;
            color: #ef4444;
            line-height: 1;
            margin-bottom: 20px;
            text-shadow: 0 5px 15px rgba(239, 68, 68, 0.3);
        }
        
        .error-title {
            font-size: 36px;
            color: var(--white);
            margin-bottom: 20px;
            font-weight: 700;
        }
        
        .error-message {
            color: #a0a0a0;
            margin-bottom: 30px;
            font-size: 18px;
            line-height: 1.8;
        }
        
        .error-details {
            background: rgba(239, 68, 68, 0.05);
            border: 1px solid rgba(239, 68, 68, 0.2);
            border-radius: 12px;
            padding: 20px;
            margin: 25px 0;
            text-align: left;
            font-family: 'JetBrains Mono', monospace;
            font-size: 13px;
            color: #f87171;
            max-height: 250px;
            overflow-y: auto;
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
            cursor: pointer;
            border: none;
        }
        
        .btn-primary {
            background: #ef4444;
            color: var(--white);
        }
        
        .btn-secondary {
            background: transparent;
            color: #ef4444;
            border: 1px solid #ef4444;
        }
        
        .btn-primary:hover {
            background: #dc2626;
            transform: translateY(-3px);
            box-shadow: 0 10px 20px rgba(239, 68, 68, 0.2);
        }
        
        .btn-secondary:hover {
            background: #ef4444;
            color: var(--white);
        }
        
        .error-image {
            font-size: 100px;
            margin-bottom: 20px;
        }
        
        .contact-info {
            margin-top: 30px;
            padding-top: 25px;
            border-top: 1px solid var(--border);
            color: #a0a0a0;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-image">⚠️</div>
        <div class="error-code">500</div>
        <h1 class="error-title">Internal Server Error</h1>
        <p class="error-message">
            Something went wrong on our servers. Our team has been notified and is working to fix the issue.
            Please try again in a few minutes.
        </p>
        
        <div class="error-details">
            <% 
                Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
                String message = (String) request.getAttribute("javax.servlet.error.message");
                if (message != null) { 
            %>
                <strong>Message:</strong> <%= message %><br><br>
            <% } %>
            <% if (throwable != null) { %>
                <strong>Stack Trace:</strong>
                <pre style="margin: 10px 0; font-size: 12px; white-space: pre-wrap;"><%
                    java.io.CharArrayWriter cw = new java.io.CharArrayWriter();
                    java.io.PrintWriter pw = new java.io.PrintWriter(cw);
                    throwable.printStackTrace(pw);
                    out.print(cw.toString());
                %></pre>
            <% } else { %>
                <p>No additional error details available.</p>
            <% } %>
        </div>
        
        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/index.html" class="btn-error btn-primary">
                ← Back to Home
            </a>
            <button onclick="window.location.reload()" class="btn-error btn-secondary">
                Refresh Page
            </button>
        </div>
        
        <div class="contact-info">
            <p>If the problem persists, please contact support at: support@quizapp.com</p>
        </div>
    </div>
</body>
</html>