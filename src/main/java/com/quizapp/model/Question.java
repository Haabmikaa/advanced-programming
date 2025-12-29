package com.quizapp.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Question entity representing individual questions in a quiz
 */
public class Question extends BaseEntity {
    private int questionId;
    private Quiz quiz;
    private String questionText;
    private QuestionType type;
    private int points;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String explanation;
    
    public enum QuestionType {
        MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER
    }
    
    // Constructors
    public Question() {
        super();
        this.points = 1;
        this.type = QuestionType.MULTIPLE_CHOICE;
    }
    
    public Question(String questionText, QuestionType type, int points, String correctAnswer) {
        this();
        this.questionText = questionText;
        this.type = type;
        this.points = points;
        this.correctAnswer = correctAnswer;
    }
    
    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    
    public Quiz getQuiz() {
        return quiz;
    }
    
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public QuestionType getType() {
        return type;
    }
    
    public void setType(QuestionType type) {
        this.type = type;
    }
    
    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }
    
    public String getOptionA() {
        return optionA;
    }
    
    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }
    
    public String getOptionB() {
        return optionB;
    }
    
    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }
    
    public String getOptionC() {
        return optionC;
    }
    
    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }
    
    public String getOptionD() {
        return optionD;
    }
    
    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    // Business logic methods
    public List<String> getOptions() {
        if (type == QuestionType.TRUE_FALSE) {
            return Arrays.asList("True", "False");
        }
        
        List<String> options = Arrays.asList(optionA, optionB, optionC, optionD);
        return options.stream()
            .filter(option -> option != null && !option.trim().isEmpty())
            .collect(Collectors.toList());

    }
    
    public boolean isMultipleChoice() {
        return type == QuestionType.MULTIPLE_CHOICE;
    }
    
    public boolean isTrueFalse() {
        return type == QuestionType.TRUE_FALSE;
    }
    
    public boolean isShortAnswer() {
        return type == QuestionType.SHORT_ANSWER;
    }
    
    public boolean isCorrect(String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }
        
        switch (type) {
            case MULTIPLE_CHOICE:
            case TRUE_FALSE:
                return correctAnswer.equalsIgnoreCase(userAnswer.trim());
            case SHORT_ANSWER:
                // For short answers, check for partial match
                String correctLower = correctAnswer.toLowerCase().trim();
                String userLower = userAnswer.toLowerCase().trim();
                return userLower.contains(correctLower) || correctLower.contains(userLower);
            default:
                return false;
        }
    }
    
    public boolean validateAnswer(String userAnswer) {
        if (userAnswer == null) return false;
        
        switch (type) {
            case MULTIPLE_CHOICE:
                List<String> validOptions = getOptions();
                return validOptions.stream()
                    .anyMatch(option -> option.equalsIgnoreCase(userAnswer.trim()));
            case TRUE_FALSE:
                return userAnswer.trim().equalsIgnoreCase("True") || 
                       userAnswer.trim().equalsIgnoreCase("False");
            case SHORT_ANSWER:
                return !userAnswer.trim().isEmpty() && userAnswer.trim().length() <= 500;
            default:
                return false;
        }
    }
    
    public String getFormattedQuestion() {
        StringBuilder sb = new StringBuilder();
        sb.append(questionText).append("\n");
        
        if (isMultipleChoice()) {
            char optionChar = 'A';
            for (String option : getOptions()) {
                sb.append(optionChar++).append(") ").append(option).append("\n");
            }
        } else if (isTrueFalse()) {
            sb.append("A) True\nB) False\n");
        }
        
        return sb.toString();
    }
    
    // Helper method for RMI/serialization
    public QuestionDTO toDTO() {
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionId(questionId);
        dto.setQuestionText(questionText);
        dto.setType(type.name());
        dto.setPoints(points);
        dto.setOptionA(optionA);
        dto.setOptionB(optionB);
        dto.setOptionC(optionC);
        dto.setOptionD(optionD);
        dto.setOptions(getOptions());
        return dto;
    }
    
    // Data Transfer Object for serialization
    public static class QuestionDTO implements java.io.Serializable {
        private int questionId;
        private String questionText;
        private String type;
        private int points;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private List<String> options;
        
        // Getters and Setters for DTO
        public int getQuestionId() { return questionId; }
        public void setQuestionId(int questionId) { this.questionId = questionId; }
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public int getPoints() { return points; }
        public void setPoints(int points) { this.points = points; }
        public String getOptionA() { return optionA; }
        public void setOptionA(String optionA) { this.optionA = optionA; }
        public String getOptionB() { return optionB; }
        public void setOptionB(String optionB) { this.optionB = optionB; }
        public String getOptionC() { return optionC; }
        public void setOptionC(String optionC) { this.optionC = optionC; }
        public String getOptionD() { return optionD; }
        public void setOptionD(String optionD) { this.optionD = optionD; }
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", type=" + type +
                ", points=" + points +
                ", questionText='" + questionText.substring(0, Math.min(50, questionText.length())) + "..." + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return questionId == question.questionId && 
               questionText.equals(question.questionText);
    }
    
    @Override
    public int hashCode() {
        int result = questionId;
        result = 31 * result + questionText.hashCode();
        return result;
    }
}