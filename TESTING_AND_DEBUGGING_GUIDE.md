# Testing & Debugging Guide for Quiz Web App

## 📋 Table of Contents
1. [Verified Servlet Forward Paths](#verified-servlet-forward-paths)
2. [Testing Teacher's Save Function](#testing-teachers-save-function)
3. [Debugging Checklist](#debugging-checklist)
4. [Common Issues & Solutions](#common-issues--solutions)

---

## ✅ Verified Servlet Forward Paths

All forward paths in `QuizServlet.java` have been updated to use **absolute paths** (starting with `/`) from the webapp root:

### **Current Forward Paths:**

| Action | Method | Forward Path | Status |
|--------|--------|--------------|--------|
| `action=list` | GET | `/pages/quizzes.jsp` | ✅ Verified |
| `action=view` | GET | `/pages/quiz-details.jsp` | ✅ Verified |
| `action=take` | GET | `/pages/student/take-quiz.jsp` | ✅ Verified |
| `action=create` | GET | `/pages/teacher/create-quiz-dynamic.jsp` | ✅ Verified |
| `action=details_json` | GET | Returns JSON (no forward) | ✅ Verified |
| `action=saveFull` | POST | Returns JSON (no forward) | ✅ Verified |

### **Path Format Explanation:**
- **Absolute paths** (starting with `/`): Resolved from webapp root
  - Example: `/pages/student/take-quiz.jsp` → `{webapp}/pages/student/take-quiz.jsp`
- **Relative paths** (no leading `/`): Resolved from current servlet context
  - ❌ **Avoid** - Can cause 404 errors if context path changes

---

## 🧪 Testing Teacher's Save Function

### **Step 1: Access Create Quiz Page**

1. **Login as Teacher:**
   ```
   URL: http://localhost:8080/QuizWebApp/login
   ```

2. **Navigate to Create Quiz:**
   ```
   URL: http://localhost:8080/QuizWebApp/quiz?action=create
   ```
   OR click "Create Quiz" button from teacher dashboard

### **Step 2: Fill Out Quiz Form**

**Required Fields:**
- ✅ **Quiz Title** (required)
- ✅ **Category** (required)
- ⚠️ **Description** (optional - auto-generated if empty)
- ⚠️ **Time Limit** (defaults to 30 minutes)
- ✅ **At least one question** (required)

**Example Test Data:**
```
Title: "Java Basics Test"
Category: "Programming"
Description: "Test your Java knowledge"
Time Limit: 20
```

**Add Questions:**
1. Click "+ Add Question Row"
2. Fill in:
   - Question text: "What is Java?"
   - Option A: "A programming language"
   - Option B: "A coffee brand"
   - Option C: "An island"
   - Option D: "A car model"
   - Correct Option: A

### **Step 3: Submit and Monitor**

1. **Click "Finalize and Save to Database"**
2. **Open Browser Developer Tools:**
   - Press `F12` or `Ctrl+Shift+I` (Windows) / `Cmd+Option+I` (Mac)
   - Go to **Network** tab
   - Filter by **XHR** or **Fetch**

3. **Watch for Request:**
   ```
   Method: POST
   URL: /QuizWebApp/quiz?action=saveFull
   Status: Should be 200 (OK)
   ```

4. **Check Response:**
   ```json
   {
     "success": true,
     "quizId": 123,
     "message": "Quiz saved successfully with 3 questions",
     "questionsSaved": 3,
     "questionsFailed": 0
   }
   ```

### **Step 4: Check Server Logs**

**Look for these log messages in your server console:**

```
📥 Received quiz data: {"title":"Java Basics Test",...}
💾 Saving quiz: Java Basics Test
✅ Quiz saved with ID: 123
  ✅ Question saved: What is Java?...
  ✅ Question saved: What is a class?...
📊 Summary: 3 questions saved, 0 failed
✅ Quiz creation completed successfully!
```

### **Step 5: Verify in Database**

**Check `quizzes` table:**
```sql
SELECT * FROM quizzes ORDER BY quiz_id DESC LIMIT 1;
```

**Check `questions` table:**
```sql
SELECT * FROM questions WHERE quiz_id = [LAST_QUIZ_ID];
```

---

## 🔍 Debugging Checklist

### **If "Take Quiz" or "Details" Shows 404:**

✅ **Check 1: Verify JSP Files Exist**
```bash
# Check if files exist in correct location:
src/main/webapp/pages/student/take-quiz.jsp
src/main/webapp/pages/quiz-details.jsp
```

✅ **Check 2: Verify Servlet Mapping**
- Open `web.xml`
- Ensure `QuizServlet` is mapped to `/quiz`
- Check servlet class path is correct

✅ **Check 3: Check Server Logs**
- Look for `FileNotFoundException` or `ServletException`
- Check if path resolution is correct

✅ **Check 4: Test Direct URL**
```
http://localhost:8080/QuizWebApp/pages/student/take-quiz.jsp
```
If this works, the issue is in servlet forwarding.

### **If "Save to Database" Doesn't Work:**

✅ **Check 1: Browser Console (F12)**
- Look for JavaScript errors
- Check Network tab for failed requests
- Verify request payload is correct JSON

✅ **Check 2: Server Console Logs**
- Look for error messages starting with `❌`
- Check for SQL exceptions
- Verify database connection

✅ **Check 3: Validate Form Data**
- Ensure all required fields are filled
- Check for empty questions
- Verify JSON structure matches expected format

✅ **Check 4: Database Connection**
```java
// Test connection in QuizDAO
Connection conn = DBConnection.getConnection();
if (conn == null) {
    System.err.println("❌ Database connection failed!");
}
```

✅ **Check 5: User Session**
- Ensure teacher is logged in
- Check session attribute `user` exists
- Verify user role is `TEACHER`

### **Common Error Messages & Solutions:**

| Error | Cause | Solution |
|-------|-------|----------|
| `400: Quiz title is required` | Empty title field | Fill in quiz title |
| `400: At least one question is required` | No questions added | Add at least one question |
| `403: Only teachers can create quizzes` | Wrong user role | Login as teacher |
| `500: Failed to save quiz to database` | Database error | Check DB connection, logs |
| `404: Page not found` | Wrong JSP path | Verify forward path in servlet |

---

## 🐛 Common Issues & Solutions

### **Issue 1: Quiz Saves but Questions Don't**

**Symptoms:**
- Quiz appears in database
- Questions table is empty

**Debug Steps:**
1. Check server logs for question save errors
2. Verify `quiz.getQuizId()` is set after insert
3. Check QuestionDAO insert method

**Solution:**
```java
// In QuizServlet, ensure quiz ID is set:
boolean quizSaved = qDao.insert(quiz);
if (quizSaved && quiz.getQuizId() > 0) {
    // Now link questions to quiz
    q.setQuiz(quiz);
}
```

### **Issue 2: JSON Parsing Errors**

**Symptoms:**
- `500: Server error`
- `NullPointerException` in servlet

**Debug Steps:**
1. Check browser Network tab - view request payload
2. Verify JSON is valid (use JSON validator)
3. Check servlet logs for parsing errors

**Solution:**
```javascript
// In create-quiz-dynamic.jsp, ensure valid JSON:
const data = {
    title: title.trim(),  // Remove whitespace
    category: category.trim(),
    questions: questions.filter(q => q.text) // Remove empty questions
};
```

### **Issue 3: Session Expired**

**Symptoms:**
- `403: Only teachers can create quizzes`
- User appears logged out

**Solution:**
- Check session timeout in `web.xml` (currently 30 minutes)
- Re-login as teacher
- Ensure session is maintained during request

### **Issue 4: Database Constraint Violations**

**Symptoms:**
- `SQLException: Cannot insert NULL`
- Foreign key constraint errors

**Debug Steps:**
1. Check database schema
2. Verify all required fields are set
3. Check foreign key relationships

**Solution:**
```java
// Ensure all required fields are set:
quiz.setDescription(description != null ? description : "Default description");
quiz.setDifficulty(Quiz.DifficultyLevel.MEDIUM);
quiz.setMaxAttempts(1);
```

---

## 📊 Testing Scenarios

### **Scenario 1: Happy Path**
1. ✅ Login as teacher
2. ✅ Create quiz with 3 questions
3. ✅ Save successfully
4. ✅ Verify in database
5. ✅ View quiz in student dashboard

### **Scenario 2: Validation Errors**
1. ✅ Try saving with empty title → Should show error
2. ✅ Try saving with no questions → Should show error
3. ✅ Try saving as student → Should show 403 error

### **Scenario 3: Edge Cases**
1. ✅ Save quiz with 1 question (minimum)
2. ✅ Save quiz with 50 questions (stress test)
3. ✅ Save quiz with special characters in title
4. ✅ Save quiz with very long description

---

## 🔧 Quick Debug Commands

### **Check Servlet Mapping:**
```bash
grep -r "QuizServlet" src/main/webapp/WEB-INF/web.xml
```

### **Check Forward Paths:**
```bash
grep -r "getRequestDispatcher" src/main/java/com/quizapp/servlets/QuizServlet.java
```

### **Test Database Connection:**
```java
// Add to QuizServlet.doPost():
Connection conn = DBConnection.getConnection();
System.out.println("DB Connection: " + (conn != null ? "OK" : "FAILED"));
```

### **View Request Payload:**
```javascript
// In browser console:
console.log('Request data:', JSON.stringify(data));
```

---

## 📝 Summary

### **✅ What's Fixed:**
1. ✅ All servlet forward paths use absolute paths (`/pages/...`)
2. ✅ Teacher save function includes all required fields
3. ✅ Comprehensive error handling and logging
4. ✅ JSON response with success/error details
5. ✅ Client-side validation before submission
6. ✅ Server-side validation for security

### **✅ What to Monitor:**
- Server console logs (for debugging)
- Browser Network tab (for request/response)
- Database tables (for data verification)
- Browser console (for JavaScript errors)

---

**Last Updated:** After implementing quiz details modal and teacher save function fixes.

