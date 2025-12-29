// Main JavaScript file for Quiz Web App
// Version 1.0.0

// DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('Quiz Web App initialized');

    // Quiz Details Modal logic - available globally for any page (dashboard, quizzes)
    function closeQuizDetailsModal() {
      var modal = document.getElementById('quizDetailsModal');
      if (modal) modal.style.display = 'none';
    }
    if (document.getElementById('quizDetailsModal')) {
      document.querySelectorAll('.quiz-details-btn').forEach(function(btn) {
        btn.addEventListener('click', async function(e) {
          const quizId = btn.getAttribute('data-quiz-id');
          const contentDiv = document.getElementById('quizDetailsContent');
          contentDiv.innerHTML = '<div style="text-align:center;"><em>Loading...</em></div>';
          document.getElementById('quizDetailsModal').style.display = 'flex';
          try {
            const ctx = window.contextPath || '';
            const resp = await fetch(ctx + '/quiz?action=details_json&id=' + quizId);
            if (!resp.ok) {
              contentDiv.innerHTML = '<div style="color:red;">Failed to load details.</div>';
              return;
            }
            const quiz = await resp.json();
            let html = `
              <h2 style="color:#4f46e5; margin-bottom:6px;">${quiz.title}</h2>
              <div style="font-size:15px; color:#666; margin-bottom:12px;">${quiz.description || ''}</div>
              <div style="margin-bottom:8px;">
                <strong>Category:</strong> ${quiz.category} <br>
                <strong>Duration:</strong> ${quiz.durationMinutes} min <br>
                <strong>Difficulty:</strong> ${quiz.difficulty || '-'}
              </div>
              <div style="margin:12px 0;"><strong>Total Questions:</strong> ${quiz.questionCount}</div>
              <div style="margin:10px 0 0;"><em>Want to try?</em> <br>
                <a href="${ctx}/quiz?action=take&id=${quiz.quizId}" class="btn btn-primary" style="margin-top:10px; display:inline-block;">Take This Quiz</a>
              </div>
            `;
            contentDiv.innerHTML = html;
          } catch (e) {
            contentDiv.innerHTML = '<div style="color:red;">Failed to load details.</div>';
          }
        });
      });
      document.getElementById('closeQuizDetailsModal').onclick = closeQuizDetailsModal;
      document.getElementById('quizDetailsModal').addEventListener('click', function(e) {
        if (e.target === this) closeQuizDetailsModal();
      });
    }

    
    // Initialize components
    initPasswordToggle();
    initFormValidation();
    initTooltips();
    initThemeToggle();
    
    // Check for specific pages
    if (document.querySelector('.quiz-container')) {
        initQuizTimer();
        initQuizNavigation();
    }
    
    if (document.querySelector('.results-table')) {
        initResultsTable();
    }
    
    if (document.querySelector('.quiz-grid')) {
        initQuizFilters();
    }
});

// Password visibility toggle
function initPasswordToggle() {
    const toggleButtons = document.querySelectorAll('.toggle-password');
    toggleButtons.forEach(button => {
        button.addEventListener('click', function() {
            const input = this.previousElementSibling;
            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);
            this.textContent = type === 'password' ? '👁️' : '👁️‍🗨️';
        });
    });
}

// Form validation
function initFormValidation() {
    const forms = document.querySelectorAll('form[data-validate]');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const inputs = this.querySelectorAll('input[required], select[required]');
            let isValid = true;
            
            inputs.forEach(input => {
                if (!input.value.trim()) {
                    showError(input, 'This field is required');
                    isValid = false;
                } else {
                    clearError(input);
                }
                
                // Email validation
                if (input.type === 'email') {
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!emailRegex.test(input.value)) {
                        showError(input, 'Please enter a valid email address');
                        isValid = false;
                    }
                }
                
                // Password strength
                if (input.type === 'password' && input.value.length < 6) {
                    showError(input, 'Password must be at least 6 characters');
                    isValid = false;
                }
            });
            
            if (!isValid) {
                e.preventDefault();
            }
        });
    });
}

function showError(input, message) {
    clearError(input);
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    errorDiv.style.color = '#dc2626';
    errorDiv.style.fontSize = '12px';
    errorDiv.style.marginTop = '5px';
    input.parentNode.appendChild(errorDiv);
    input.style.borderColor = '#dc2626';
}

function clearError(input) {
    const errorDiv = input.parentNode.querySelector('.error-message');
    if (errorDiv) {
        errorDiv.remove();
    }
    input.style.borderColor = '#e5e7eb';
}

// Tooltips
function initTooltips() {
    const tooltipElements = document.querySelectorAll('[data-tooltip]');
    tooltipElements.forEach(element => {
        element.addEventListener('mouseenter', function(e) {
            const tooltip = document.createElement('div');
            tooltip.className = 'tooltip';
            tooltip.textContent = this.getAttribute('data-tooltip');
            tooltip.style.position = 'absolute';
            tooltip.style.background = '#374151';
            tooltip.style.color = 'white';
            tooltip.style.padding = '6px 12px';
            tooltip.style.borderRadius = '4px';
            tooltip.style.fontSize = '12px';
            tooltip.style.zIndex = '1000';
            tooltip.style.whiteSpace = 'nowrap';
            
            document.body.appendChild(tooltip);
            
            const rect = this.getBoundingClientRect();
            tooltip.style.left = (rect.left + window.scrollX) + 'px';
            tooltip.style.top = (rect.top + window.scrollY - tooltip.offsetHeight - 10) + 'px';
            
            this._tooltip = tooltip;
        });
        
        element.addEventListener('mouseleave', function() {
            if (this._tooltip) {
                this._tooltip.remove();
                this._tooltip = null;
            }
        });
    });
}

// Theme toggle (dark/light mode)
function initThemeToggle() {
    const themeToggle = document.getElementById('themeToggle');
    if (!themeToggle) return;
    
    const currentTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', currentTheme);
    
    themeToggle.addEventListener('click', function() {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        
        this.textContent = newTheme === 'light' ? '🌙' : '☀️';
    });
}

// Quiz Timer
function initQuizTimer() {
    const timerElement = document.getElementById('quizTimer');
    if (!timerElement) return;
    
    const duration = parseInt(timerElement.dataset.duration) || 1200; // 20 minutes in seconds
    let timeLeft = duration;
    
    function updateTimer() {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        
        timerElement.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        
        // Warning when less than 5 minutes
        if (timeLeft <= 300) {
            timerElement.style.color = '#dc2626';
            timerElement.style.fontWeight = 'bold';
            
            // Blink when less than 1 minute
            if (timeLeft <= 60) {
                timerElement.style.opacity = timerElement.style.opacity === '0.5' ? '1' : '0.5';
            }
        }
        
        if (timeLeft <= 0) {
            clearInterval(timerInterval);
            alert('Time is up! Submitting quiz...');
            document.querySelector('form#quizForm')?.submit();
        } else {
            timeLeft--;
        }
    }
    
    const timerInterval = setInterval(updateTimer, 1000);
    updateTimer(); // Initial call
}

// Quiz Navigation
function initQuizNavigation() {
    const questions = document.querySelectorAll('.question-container');
    if (questions.length === 0) return;
    
    let currentQuestion = 0;
    const totalQuestions = questions.length;
    
    function showQuestion(index) {
        // Hide all questions
        questions.forEach(q => q.style.display = 'none');
        
        // Show current question
        questions[index].style.display = 'block';
        currentQuestion = index;
        
        // Update navigation buttons
        document.getElementById('prevBtn').disabled = index === 0;
        document.getElementById('nextBtn').disabled = index === totalQuestions - 1;
        
        // Update progress
        updateProgress(index);
    }
    
    function updateProgress(index) {
        const progressDots = document.querySelectorAll('.progress-dot');
        progressDots.forEach((dot, i) => {
            dot.classList.remove('active');
            if (i === index) dot.classList.add('active');
        });
        
        // Update progress bar
        const progressBar = document.querySelector('.progress-bar');
        if (progressBar) {
            const progress = ((index + 1) / totalQuestions) * 100;
            progressBar.style.width = `${progress}%`;
        }
    }
    
    // Initialize navigation buttons
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    
    if (prevBtn) {
        prevBtn.addEventListener('click', () => {
            if (currentQuestion > 0) showQuestion(currentQuestion - 1);
        });
    }
    
    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            if (currentQuestion < totalQuestions - 1) showQuestion(currentQuestion + 1);
        });
    }
    
    // Progress dots click
    document.querySelectorAll('.progress-dot').forEach((dot, index) => {
        dot.addEventListener('click', () => showQuestion(index));
    });
    
    // Initialize first question
    showQuestion(0);
}

// Results Table
function initResultsTable() {
    const table = document.querySelector('.results-table');
    if (!table) return;
    
    // Sort functionality
    const headers = table.querySelectorAll('th[data-sort]');
    headers.forEach(header => {
        header.style.cursor = 'pointer';
        header.addEventListener('click', function() {
            const column = this.getAttribute('data-sort');
            const isAsc = this.classList.contains('asc');
            
            // Reset all headers
            headers.forEach(h => {
                h.classList.remove('asc', 'desc');
            });
            
            // Set new sort direction
            this.classList.toggle('asc', !isAsc);
            this.classList.toggle('desc', isAsc);
            
            // Sort table
            sortTable(table, Array.from(headers).indexOf(this), !isAsc);
        });
    });
}

function sortTable(table, column, ascending = true) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    
    rows.sort((a, b) => {
        const aVal = a.cells[column].textContent.trim();
        const bVal = b.cells[column].textContent.trim();
        
        // Handle numeric values
        const aNum = parseFloat(aVal);
        const bNum = parseFloat(bVal);
        
        if (!isNaN(aNum) && !isNaN(bNum)) {
            return ascending ? aNum - bNum : bNum - aNum;
        }
        
        // Handle dates
        const aDate = new Date(aVal);
        const bDate = new Date(bVal);
        if (!isNaN(aDate) && !isNaN(bDate)) {
            return ascending ? aDate - bDate : bDate - aDate;
        }
        
        // String comparison
        return ascending ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal);
    });
    
    // Reorder rows
    rows.forEach(row => tbody.appendChild(row));
}

// Quiz Filters
function initQuizFilters() {
    const searchInput = document.getElementById('quizSearch');
    const categorySelect = document.getElementById('categoryFilter');
    const difficultySelect = document.getElementById('difficultyFilter');
    
    if (!searchInput && !categorySelect && !difficultySelect) return;
    
    function filterQuizzes() {
        const searchTerm = searchInput?.value.toLowerCase() || '';
        const category = categorySelect?.value || '';
        const difficulty = difficultySelect?.value || '';
        
        const quizCards = document.querySelectorAll('.quiz-card');
        let visibleCount = 0;
        
        quizCards.forEach(card => {
            const title = card.querySelector('.quiz-title')?.textContent.toLowerCase() || '';
            const desc = card.querySelector('.quiz-description')?.textContent.toLowerCase() || '';
            const cardCategory = card.querySelector('.quiz-category')?.textContent.toLowerCase() || '';
            const cardDifficulty = card.querySelector('.difficulty')?.textContent.toLowerCase() || '';
            
            let matches = true;
            
            if (searchTerm && !title.includes(searchTerm) && !desc.includes(searchTerm)) {
                matches = false;
            }
            
            if (category && cardCategory !== category) {
                matches = false;
            }
            
            if (difficulty && cardDifficulty !== difficulty) {
                matches = false;
            }
            
            card.style.display = matches ? 'block' : 'none';
            if (matches) visibleCount++;
        });
        
        // Show/hide empty state
        const emptyState = document.querySelector('.empty-state');
        if (emptyState) {
            emptyState.style.display = visibleCount === 0 ? 'block' : 'none';
        }
    }
    
    // Add event listeners
    if (searchInput) searchInput.addEventListener('input', filterQuizzes);
    if (categorySelect) categorySelect.addEventListener('change', filterQuizzes);
    if (difficultySelect) difficultySelect.addEventListener('change', filterQuizzes);
}

// API Client
const QuizAPI = {
    baseUrl: '/api',
    
    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'same-origin'
        };
        
        const response = await fetch(url, { ...defaultOptions, ...options });
        
        if (!response.ok) {
            throw new Error(`API Error: ${response.status} ${response.statusText}`);
        }
        
        return response.json();
    },
    
    // User methods
    async login(username, password) {
        return this.request('/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
    },
    
    async register(userData) {
        return this.request('/register', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    },
    
    // Quiz methods
    async getQuizzes(filters = {}) {
        const query = new URLSearchParams(filters).toString();
        return this.request(`/quizzes?${query}`);
    },
    
    async getQuiz(id) {
        return this.request(`/quizzes/${id}`);
    },
    
    async submitQuizAttempt(attemptData) {
        return this.request('/attempts', {
            method: 'POST',
            body: JSON.stringify(attemptData)
        });
    },
    
    // Result methods
    async getResults(userId) {
        return this.request(`/users/${userId}/results`);
    },
    
    async getResultDetails(resultId) {
        return this.request(`/results/${resultId}`);
    }
};

// Session Management
const SessionManager = {
    get(key) {
        const value = sessionStorage.getItem(key);
        try {
            return JSON.parse(value);
        } catch {
            return value;
        }
    },
    
    set(key, value) {
        sessionStorage.setItem(key, JSON.stringify(value));
    },
    
    remove(key) {
        sessionStorage.removeItem(key);
    },
    
    clear() {
        sessionStorage.clear();
    },
    
    isAuthenticated() {
        return !!this.get('user');
    },
    
    getUser() {
        return this.get('user');
    },
    
    getRole() {
        return this.get('role');
    }
};

// Notification System
const Notification = {
    show(message, type = 'info', duration = 5000) {
        const container = document.getElementById('notification-container') || this.createContainer();
        
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">${message}</div>
            <button class="notification-close">&times;</button>
        `;
        
        container.appendChild(notification);
        
        // Auto-remove after duration
        setTimeout(() => {
            if (notification.parentNode) {
                notification.style.opacity = '0';
                setTimeout(() => notification.remove(), 300);
            }
        }, duration);
        
        // Close button
        notification.querySelector('.notification-close').addEventListener('click', () => {
            notification.remove();
        });
    },
    
    createContainer() {
        const container = document.createElement('div');
        container.id = 'notification-container';
        container.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 10000;
        `;
        document.body.appendChild(container);
        return container;
    },
    
    success(message) {
        this.show(message, 'success');
    },
    
    error(message) {
        this.show(message, 'error');
    },
    
    info(message) {
        this.show(message, 'info');
    },
    
    warning(message) {
        this.show(message, 'warning');
    }
};

// Export to global scope
window.QuizAPI = QuizAPI;
window.SessionManager = SessionManager;
window.Notification = Notification;

// Add CSS for notifications
const notificationStyles = document.createElement('style');
notificationStyles.textContent = `
    .notification {
        background: white;
        border-radius: 8px;
        padding: 15px;
        margin-bottom: 10px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        border-left: 4px solid #4f46e5;
        display: flex;
        align-items: center;
        justify-content: space-between;
        min-width: 300px;
        max-width: 400px;
        animation: slideIn 0.3s ease;
        transition: opacity 0.3s ease;
    }
    
    .notification-success {
        border-left-color: #10b981;
    }
    
    .notification-error {
        border-left-color: #dc2626;
    }
    
    .notification-warning {
        border-left-color: #f59e0b;
    }
    
    .notification-info {
        border-left-color: #3b82f6;
    }
    
    .notification-content {
        flex: 1;
        margin-right: 10px;
    }
    
    .notification-close {
        background: none;
        border: none;
        font-size: 20px;
        cursor: pointer;
        color: #6b7280;
        padding: 0;
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .notification-close:hover {
        color: #374151;
    }
    
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
`;
document.head.appendChild(notificationStyles);