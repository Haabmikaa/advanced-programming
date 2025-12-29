package com.quizapp;

import com.quizapp.util.DatabaseUtil;
import java.sql.*;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("🚀 Testing Database Setup for Quiz Web Application");
        System.out.println("===================================================\n");
        
        // Test connection
        boolean connected = DatabaseUtil.testConnection();
        if (!connected) {
            System.err.println("Failed to connect to database. Exiting...");
            System.exit(1);
        }
        
        // Print database info
        DatabaseUtil.printDatabaseInfo();
        
        // Test sample queries
        testSampleQueries();
        
        // Print pool stats
        DatabaseUtil.printPoolStats();
        
        System.out.println("\n✅ Database setup test completed successfully!");
    }
    
    private static void testSampleQueries() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            
            System.out.println("\n📊 SAMPLE DATA TEST");
            System.out.println("===================");
            
            // Test 1: Count users
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            if (rs.next()) {
                System.out.println("Total Users: " + rs.getInt("count"));
            }
            
            // Test 2: Count quizzes
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM quizzes");
            if (rs.next()) {
                System.out.println("Total Quizzes: " + rs.getInt("count"));
            }
            
            // Test 3: Count questions
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM questions");
            if (rs.next()) {
                System.out.println("Total Questions: " + rs.getInt("count"));
            }
            
            // Test 4: List user roles
            System.out.println("\n👥 USER ROLES DISTRIBUTION:");
            rs = stmt.executeQuery(
                "SELECT role, COUNT(*) as count FROM users GROUP BY role ORDER BY role");
            while (rs.next()) {
                System.out.println("  " + rs.getString("role") + ": " + rs.getInt("count"));
            }
            
            // Test 5: List quiz categories
            System.out.println("\n📚 QUIZ CATEGORIES:");
            rs = stmt.executeQuery(
                "SELECT category, COUNT(*) as count FROM quizzes GROUP BY category");
            while (rs.next()) {
                System.out.println("  " + rs.getString("category") + ": " + rs.getInt("count"));
            }
            
            // Test 6: Sample quiz data
            System.out.println("\n🎯 SAMPLE QUIZZES:");
            rs = stmt.executeQuery(
                "SELECT quiz_id, title, category, difficulty FROM quizzes LIMIT 3");
            while (rs.next()) {
                System.out.println("  • " + rs.getString("title") + 
                    " (" + rs.getString("category") + " - " + 
                    rs.getString("difficulty") + ")");
            }
            
        } catch (SQLException e) {
            System.err.println("Error during sample queries: " + e.getMessage());
        } finally {
            DatabaseUtil.closeResources(conn, stmt, rs);
        }
    }
}