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
            // Step 1: Register the driver class
            InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("database.properties");
            if (input != null) {
                properties.load(input);
                Class.forName(properties.getProperty("db.driver"));
            }
        } catch (Exception e) {
            System.err.println("Error registering JDBC driver: " + e.getMessage());
        }
    }
    
    // Step 2: Creating a connection
    public static Connection getConnection() throws SQLException {
        // Use environment variables if available (for production deployment), 
        // otherwise fall back to database.properties (for local development)
        String url = System.getenv("DB_URL");
        if (url == null) url = properties.getProperty("db.url");
        
        String username = System.getenv("DB_USERNAME");
        if (username == null) username = properties.getProperty("db.username");
        
        String password = System.getenv("DB_PASSWORD");
        if (password == null) password = properties.getProperty("db.password");
        
        // Return a new connection object every time
        return DriverManager.getConnection(url, username, password);
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