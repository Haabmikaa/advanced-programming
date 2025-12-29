<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register - Quiz Web App</title>
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
        }

        .auth-wrapper {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 60px 20px;
            min-height: calc(100vh - 80px);
        }

        .register-container {
            width: 100%;
            max-width: 450px;
            padding: 40px;
            background: var(--card-bg);
            border-radius: 20px;
            box-shadow: var(--shadow);
            border: 1px solid var(--border);
        }
        
        .register-header {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .register-header h1 {
            color: var(--gold);
            margin-bottom: 10px;
            font-size: 2rem;
            text-transform: uppercase;
            letter-spacing: 3px;
            font-weight: 800;
        }
        
        .register-header p {
            color: #888;
            font-size: 0.9rem;
        }
        
        .form-group {
            margin-bottom: 20px;
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
        
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 12px;
            background: var(--black);
            border: 1px solid var(--border);
            border-radius: 8px;
            font-size: 15px;
            color: var(--white);
            transition: all 0.3s;
            box-sizing: border-box;
        }
        
        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: var(--gold);
            box-shadow: 0 0 10px rgba(212, 175, 55, 0.1);
        }
        
        .form-group select option {
            background: var(--card-bg);
            color: var(--white);
        }
        
        .btn-register {
            width: 100%;
            padding: 14px;
            background: var(--gold);
            color: var(--black);
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 800;
            cursor: pointer;
            transition: all 0.3s;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-top: 10px;
        }
        
        .btn-register:hover {
            background: var(--white);
            color: var(--black);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(212, 175, 55, 0.2);
        }
        
        .register-footer {
            text-align: center;
            margin-top: 25px;
            color: #888;
            font-size: 0.9rem;
        }
        
        .register-footer a {
            color: var(--gold);
            text-decoration: none;
            font-weight: 700;
            margin-left: 5px;
        }
        
        .register-footer a:hover {
            text-decoration: underline;
        }

        .error-message {
            background: rgba(239, 68, 68, 0.1);
            color: #ef4444;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid rgba(239, 68, 68, 0.2);
            margin-bottom: 25px;
            text-align: center;
            font-size: 0.9rem;
            font-weight: 600;
        }

        .success-message {
            background: rgba(34, 197, 94, 0.1);
            color: #22c55e;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid rgba(34, 197, 94, 0.2);
            margin-bottom: 25px;
            text-align: center;
            font-size: 0.9rem;
            font-weight: 600;
        }

        .password-requirements {
            font-size: 0.7rem;
            color: #666;
            margin-top: 6px;
            font-style: italic;
        }
    </style>
    <script>
        function validateForm() {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const username = document.getElementById('username').value;
            
            // Check password match
            if (password !== confirmPassword) {
                alert('Passwords do not match!');
                return false;
            }
            
            // Check password strength
            if (password.length < 6) {
                alert('Password must be at least 6 characters long!');
                return false;
            }
            
            // Check username availability (simplified)
            if (username.length < 3) {
                alert('Username must be at least 3 characters long!');
                return false;
            }
            
            return true;
        }
    </script>
</head>
<body>
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/index.html" class="logo-container">
            <div class="logo-icon">A</div>
            <div class="logo-text">Astu<span>Quiz</span></div>
        </a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/index.html">Home</a>
            <a href="${pageContext.request.contextPath}/pages/login.jsp">Login</a>
        </div>
    </nav>
    <div class="auth-wrapper">
        <div class="register-container">
        <div class="register-header">
            <h1>Create Account</h1>
            <p>Join our quiz community</p>
        </div>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% if (request.getAttribute("success") != null) { %>
            <div class="success-message">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <form action="${pageContext.request.contextPath}/register" method="post" onsubmit="return validateForm()">
            <div class="form-group">
                <label for="fullName">Full Name *</label>
                <input type="text" id="fullName" name="fullName" required 
                       placeholder="Enter your full name">
            </div>
            
            <div class="form-group">
                <label for="username">Username *</label>
                <input type="text" id="username" name="username" required 
                       placeholder="Choose a username">
            </div>
            
            <div class="form-group">
                <label for="email">Email Address *</label>
                <input type="email" id="email" name="email" required 
                       placeholder="Enter your email">
            </div>
            
            <div class="form-group">
                <label for="role">Account Type *</label>
                <select id="role" name="role" required>
                    <option value="">Select your role</option>
                    <option value="STUDENT">Student</option>
                    <option value="TEACHER">Teacher</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="password">Password *</label>
                <input type="password" id="password" name="password" required 
                       placeholder="Create a password">
                <div class="password-requirements">
                    Must be at least 6 characters long
                </div>
            </div>
            
            <div class="form-group">
                <label for="confirmPassword">Confirm Password *</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required 
                       placeholder="Confirm your password">
            </div>
            
            <button type="submit" class="btn-register">Create Account</button>
        </form>
        
        <div class="register-footer">
            <p>Already have an account? <a href="login.jsp">Login here</a></p>
        </div>
    </div>
    </div>
</body>
</html>