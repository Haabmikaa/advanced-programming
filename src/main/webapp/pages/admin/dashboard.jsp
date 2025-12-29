<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.quizapp.model.User, com.quizapp.dao.UserDAO, com.quizapp.dao.QuizDAO, com.quizapp.dao.ResultDAO, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
        return;
    }

    UserDAO userDAO = new UserDAO();
    QuizDAO quizDAO = new QuizDAO();
    ResultDAO resultDAO = new ResultDAO();

    int totalUsers = userDAO.getCount();
    int totalQuizzes = quizDAO.getCount();
    int totalAttempts = resultDAO.getCount();
    List<User> allUsers = userDAO.getAll();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard | Gold & Black</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        :root {
            --gold: #D4AF37;
            --dark-gold: #B8860B;
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
        }

        .dashboard-container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 20px;
        }

        .dashboard-header {
            position: relative;
            background: url('https://previews.123rf.com/images/garagestock/garagestock1608/garagestock160804677/61248044-group-of-people-holding-the-admin-written-speech-bubble.jpg') no-repeat center center;
            background-size: cover;
            min-height: 300px;
            border-radius: 12px;
            margin-bottom: 30px;
            display: flex;
            flex-direction: column;
            justify-content: flex-end;
            overflow: hidden;
            border: 1px solid var(--border);
            box-shadow: var(--shadow);
        }

        .header-overlay {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.8) 100%);
            z-index: 1;
        }

        .header-content {
            position: relative;
            z-index: 2;
            padding: 30px;
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
            width: 100%;
            box-sizing: border-box;
        }

        .header-title h1 {
            color: var(--gold);
            margin: 0;
            text-transform: uppercase;
            letter-spacing: 2px;
        }

        .header-title p {
            margin: 5px 0 0;
            color: var(--white);
            opacity: 0.8;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: var(--card-bg);
            color: var(--white);
            padding: 25px;
            border-radius: 12px;
            border: 1px solid var(--border);
            border-left: 5px solid var(--gold);
            text-align: center;
            box-shadow: var(--shadow);
            transition: transform 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            border-color: var(--gold);
        }

        .stat-icon {
            font-size: 2.5rem;
            margin-bottom: 10px;
            display: block;
        }

        .stat-value {
            font-size: 2.5rem;
            font-weight: 800;
            display: block;
            margin: 5px 0;
            color: var(--gold);
        }

        .stat-label {
            text-transform: uppercase;
            font-size: 0.8rem;
            letter-spacing: 1.5px;
            color: #888;
            font-weight: 600;
        }

        .admin-grid {
            display: grid;
            grid-template-columns: 1fr;
            gap: 25px;
        }

        .card {
            background: var(--card-bg);
            color: var(--white);
            border-radius: 12px;
            padding: 25px;
            border: 1px solid var(--border);
            box-shadow: var(--shadow);
        }

        .card h3 {
            border-bottom: 1px solid var(--gold);
            padding-bottom: 15px;
            margin-top: 0;
            text-transform: uppercase;
            display: flex;
            align-items: center;
            gap: 10px;
            color: var(--gold);
            letter-spacing: 1px;
        }

        .user-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .user-table th {
            text-align: left;
            padding: 15px;
            background: rgba(212, 175, 55, 0.05);
            color: var(--gold);
            text-transform: uppercase;
            font-size: 0.8rem;
            letter-spacing: 1px;
            border-bottom: 1px solid var(--border);
        }

        .user-table td {
            padding: 15px;
            border-bottom: 1px solid var(--border);
            color: #ccc;
        }

        .user-table tr:hover td {
            background: rgba(255, 255, 255, 0.02);
            color: var(--white);
        }

        .role-badge {
            padding: 5px 12px;
            border-radius: 6px;
            font-size: 0.75rem;
            font-weight: bold;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .role-admin { background: var(--gold); color: var(--black); }
        .role-teacher { background: #333; color: var(--gold); border: 1px solid var(--gold); }
        .role-student { background: #1a1a1a; color: #aaa; border: 1px solid #444; }

        .btn-action {
            padding: 8px;
            border: 1px solid var(--border);
            background: #1a1a1a;
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.3s;
            color: var(--white);
        }

        .btn-edit:hover { background: var(--gold); border-color: var(--gold); color: var(--black); }
        .btn-delete:hover { background: #ef4444; border-color: #ef4444; color: var(--white); }

        .modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.85);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }

        .modal-content {
            background: var(--card-bg);
            color: var(--white);
            padding: 40px;
            border-radius: 15px;
            width: 100%;
            max-width: 450px;
            border: 1px solid var(--gold);
            box-shadow: var(--shadow);
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.8rem;
            color: var(--gold);
            letter-spacing: 1px;
        }

        .form-control {
            width: 100%;
            padding: 12px;
            background: var(--black);
            border: 1px solid var(--border);
            border-radius: 8px;
            color: var(--white);
            transition: border-color 0.3s;
        }

        .form-control:focus {
            outline: none;
            border-color: var(--gold);
        }

        .modal-footer {
            margin-top: 30px;
            display: flex;
            justify-content: flex-end;
            gap: 12px;
        }

        @media (max-width: 1000px) {
            .admin-grid { grid-template-columns: 1fr; }
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
            <a href="${pageContext.request.contextPath}/pages/admin/dashboard.jsp">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin?action=users">Users</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </nav>
<div class="dashboard-container">
    <div class="dashboard-header">
        <div class="header-overlay"></div>
        <div class="header-content">
            <div class="header-title">
                <h1>Admin Dashboard</h1>
                <p>Welcome back, <strong><%= user.getFullName() %></strong></p>
            </div>
            <div class="header-actions">
                <a href="${pageContext.request.contextPath}/logout" class="btn">Logout</a>
            </div>
        </div>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <span class="stat-icon">👥</span>
            <span class="stat-value"><%= totalUsers %></span>
            <span class="stat-label">Total Users</span>
        </div>
        <div class="stat-card">
            <span class="stat-icon">📚</span>
            <span class="stat-value"><%= totalQuizzes %></span>
            <span class="stat-label">Total Quizzes</span>
        </div>
        <div class="stat-card">
            <span class="stat-icon">📊</span>
            <span class="stat-value"><%= totalAttempts %></span>
            <span class="stat-label">Total Attempts</span>
        </div>
        <div class="stat-card">
            <span class="stat-icon">⚡</span>
            <span class="stat-value">99.9%</span>
            <span class="stat-label">System Uptime</span>
        </div>
    </div>

    <div class="admin-grid">
        <div class="card">
            <h3><span>👥</span> User Management</h3>
            <div style="display: flex; justify-content: flex-end; margin-bottom: 15px;">
                <button onclick="showAddUserModal()" class="btn">+ Add User</button>
            </div>
            <div style="overflow-x: auto;">
                <table class="user-table">
                    <thead>
                        <tr>
                            <th>User Details</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (User u : allUsers) { %>
                        <tr>
                            <td>
                                <strong><%= u.getFullName() %></strong><br>
                                <small><%= u.getUsername() %> | <%= u.getEmail() %></small>
                            </td>
                            <td>
                                <span class="role-badge role-<%= u.getRole().name().toLowerCase() %>">
                                    <%= u.getRole() %>
                                </span>
                            </td>
                            <td>
                                <span style="color: <%= u.isActive() ? "#28a745" : "#dc3545" %>; font-weight: bold;">
                                    <%= u.isActive() ? "● Active" : "○ Inactive" %>
                                </span>
                            </td>
                            <td>
                                <button onclick="editUser('<%= u.getUserId() %>', '<%= u.getUsername() %>', '<%= u.getFullName() %>', '<%= u.getEmail() %>', '<%= u.getRole() %>')" class="btn-action btn-edit">✏️</button>
                                <button onclick="deleteUser('<%= u.getUserId() %>', '<%= u.getFullName() %>')" class="btn-action btn-delete">🗑️</button>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>


<!-- User Modal -->
<div id="userModal" class="modal">
    <div class="modal-content">
        <h3 id="modalTitle">Add User</h3>
        <form id="userForm">
            <input type="hidden" id="userId">
            <div class="form-group">
                <label>Username</label>
                <input type="text" id="username" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Full Name</label>
                <input type="text" id="fullName" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" id="email" class="form-control" required>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" id="password" class="form-control" placeholder="Leave blank to keep current">
            </div>
            <div class="form-group">
                <label>Role</label>
                <select id="role" class="form-control" required>
                    <option value="STUDENT">Student</option>
                    <option value="TEACHER">Teacher</option>
                    <option value="ADMIN">Admin</option>
                </select>
            </div>
            <div class="modal-footer">
                <button type="button" onclick="closeModal()" class="btn btn-secondary">Cancel</button>
                <button type="submit" class="btn">Save User</button>
            </div>
        </form>
    </div>
</div>

<script>
    const modal = document.getElementById('userModal');
    const userForm = document.getElementById('userForm');
    const contextPath = '${pageContext.request.contextPath}';

    function showAddUserModal() {
        document.getElementById('modalTitle').textContent = 'Add New User';
        document.getElementById('userId').value = '';
        userForm.reset();
        document.getElementById('password').required = true;
        modal.style.display = 'flex';
    }

    function editUser(id, username, fullName, email, role) {
        document.getElementById('modalTitle').textContent = 'Edit User';
        document.getElementById('userId').value = id;
        document.getElementById('username').value = username;
        document.getElementById('fullName').value = fullName;
        document.getElementById('email').value = email;
        document.getElementById('role').value = role;
        document.getElementById('password').required = false;
        document.getElementById('password').placeholder = 'Leave blank to keep current';
        modal.style.display = 'flex';
    }

    function closeModal() {
        modal.style.display = 'none';
    }

    async function deleteUser(id, name) {
        if (confirm(`Are you sure you want to delete user ${name}?`)) {
            try {
                // Fixed URL and added proper error handling
                const response = await fetch(`${contextPath}/admin/user?action=delete&id=${id}`, {
                    method: 'POST'
                });
                
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                
                const result = await response.json();
                if (result.success) {
                    location.reload();
                } else {
                    alert('Error: ' + (result.error || 'Could not delete user. They may have related data in the system.'));
                }
            } catch (err) {
                console.error('Delete failed:', err);
                alert('An error occurred while trying to delete the user. Please try again.');
            }
        }
    }

    userForm.onsubmit = async (e) => {
        e.preventDefault();
        const formData = {
            userId: document.getElementById('userId').value,
            username: document.getElementById('username').value,
            fullName: document.getElementById('fullName').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            role: document.getElementById('role').value
        };
        
        try {
            const response = await fetch(`${contextPath}/admin/user?action=save`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData)
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const result = await response.json();
            if (result.success) {
                location.reload();
            } else {
                alert('Error: ' + (result.error || 'Failed to save user'));
            }
        } catch (err) {
            console.error('Save failed:', err);
            alert('Failed to save user. Check console for details.');
        }
    };

    window.onclick = (event) => {
        if (event.target == modal) closeModal();
    };
</script>

</body>
</html>
