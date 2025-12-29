package com.quizapp.util;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Database Utility Class for managing database connections
 * Uses Connection Pooling for better performance
 */
public class DatabaseUtil {
    private static BasicDataSource dataSource;
    private static Properties properties;
    
    static {
        try {
            // Load database properties
            properties = new Properties();
            InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream("database.properties");
            
            if (input != null) {
                properties.load(input);
                
                // Setup connection pool
                dataSource = new BasicDataSource();
                dataSource.setDriverClassName(properties.getProperty("db.driver"));
                dataSource.setUrl(properties.getProperty("db.url"));
                dataSource.setUsername(properties.getProperty("db.username"));
                dataSource.setPassword(properties.getProperty("db.password"));
                
                // Connection pool configuration
                dataSource.setInitialSize(Integer.parseInt(
                    properties.getProperty("db.pool.initialSize", "5")));
                dataSource.setMaxTotal(Integer.parseInt(
                    properties.getProperty("db.pool.maxTotal", "20")));
                dataSource.setMaxIdle(Integer.parseInt(
                    properties.getProperty("db.pool.maxIdle", "10")));
                dataSource.setMinIdle(Integer.parseInt(
                    properties.getProperty("db.pool.minIdle", "5")));
                dataSource.setMaxWaitMillis(Long.parseLong(
                    properties.getProperty("db.pool.maxWaitMillis", "10000")));
                
                // Validation settings
                dataSource.setValidationQuery(
                    properties.getProperty("db.pool.validationQuery", "SELECT 1"));
                dataSource.setTestOnBorrow(Boolean.parseBoolean(
                    properties.getProperty("db.pool.testOnBorrow", "true")));
                dataSource.setTestWhileIdle(Boolean.parseBoolean(
                    properties.getProperty("db.pool.testWhileIdle", "true")));
                
                System.out.println("✅ Database connection pool initialized successfully");
            } else {
                System.err.println("❌ Unable to find database.properties file");
            }
        } catch (Exception e) {
            System.err.println("❌ Error initializing database connection pool");
            e.printStackTrace();
        }
    }
    
    /**
     * Get a database connection from the pool
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database connection pool not initialized");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Close database resources
     */
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing database resources: " + e.getMessage());
        }
    }
    
    /**
     * Close database resources (without ResultSet)
     */
    public static void closeResources(Connection conn, Statement stmt) {
        closeResources(conn, stmt, null);
    }
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection test successful");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
        } finally {
            closeResources(conn, null, null);
        }
        return false;
    }
    
    /**
     * Get database metadata
     */
    public static void printDatabaseInfo() {
        Connection conn = null;
        try {
            conn = getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            
            System.out.println("\n📊 DATABASE INFORMATION");
            System.out.println("========================");
            System.out.println("Database: " + meta.getDatabaseProductName() + " " + 
                meta.getDatabaseProductVersion());
            System.out.println("Driver: " + meta.getDriverName() + " " + 
                meta.getDriverVersion());
            System.out.println("URL: " + meta.getURL());
            System.out.println("User: " + meta.getUserName());
            
            // Get table information
            ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"});
            System.out.println("\n📋 TABLES:");
            while (tables.next()) {
                System.out.println("  • " + tables.getString("TABLE_NAME"));
            }
            tables.close();
            
        } catch (SQLException e) {
            System.err.println("Error getting database info: " + e.getMessage());
        } finally {
            closeResources(conn, null, null);
        }
    }
    
    /**
     * Execute SQL script from file
     */
    public static void executeScript(String scriptPath) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            
            // Read script file
            String script = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(scriptPath)));
            
            // Split script by semicolon
            String[] queries = script.split(";");
            
            for (String query : queries) {
                query = query.trim();
                if (!query.isEmpty()) {
                    stmt.execute(query);
                }
            }
            
            conn.commit();
            System.out.println("✅ Script executed successfully: " + scriptPath);
            
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("❌ Error executing script: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Get connection pool statistics
     */
    public static void printPoolStats() {
        if (dataSource != null) {
            System.out.println("\n📈 CONNECTION POOL STATISTICS");
            System.out.println("=============================");
            System.out.println("Active Connections: " + dataSource.getNumActive());
            System.out.println("Idle Connections: " + dataSource.getNumIdle());
            System.out.println("Max Total: " + dataSource.getMaxTotal());
            System.out.println("Max Idle: " + dataSource.getMaxIdle());
        }
    }
}