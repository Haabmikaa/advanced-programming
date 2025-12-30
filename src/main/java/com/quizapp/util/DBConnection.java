package com.quizapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DBConnection {
    private static Properties properties = new Properties();
    
    static {
        try {
            // Load local properties if they exist
            InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("database.properties");
            if (input != null) {
                properties.load(input);
            }
            
            // Register Driver - check environment variable or properties or default
            String driver = System.getenv("DB_DRIVER");
            if (driver == null) driver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            
            Class.forName(driver);
            System.out.println("✅ JDBC Driver registered: " + driver);
        } catch (Exception e) {
            System.err.println("❌ Error registering JDBC driver: " + e.getMessage());
        }
    }
    
    // Step 2: Creating a connection
    public static Connection getConnection() throws SQLException {
        // Use environment variables if available (for production deployment), 
        // otherwise fall back to database.properties (for local development)
        String url = System.getenv("DB_URL");
        if (url == null) {
            url = properties.getProperty("db.url");
        } else {
            // If using Aiven/Render, ensure SSL is enabled if not already in the string
            if (!url.contains("useSSL=")) {
                url += (url.contains("?") ? "&" : "?") + "useSSL=true&requireSSL=true";
            }
        }
        
        String username = System.getenv("DB_USERNAME");
        if (username == null) username = properties.getProperty("db.username");
        
        String password = System.getenv("DB_PASSWORD");
        if (password == null) password = properties.getProperty("db.password");
        
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("❌ DATABASE CONNECTION FAILURE!");
            System.err.println("Attempted URL: " + maskUrl(url));
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    // Helper to hide password in logs if it's in the URL
    private static String maskUrl(String url) {
        if (url == null) return "null";
        return url.replaceAll(":([^/@:]+)@", ":****@");
    }
    
    // Step 5: Closing a connection (handled by DAOs using close() or try-with-resources)
    public static void closeConnection(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}