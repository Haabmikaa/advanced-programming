// Test Database Connection
import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/quizdb";
        String username = "quizapp_user";
        String password = "quizapp_password123";
        
        System.out.println("Testing database connection...");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            Connection conn = DriverManager.getConnection(url, username, password);
            
            if (conn != null) {
                System.out.println("✅ Connection successful!");
                
                // Test query
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as user_count FROM users");
                
                if (rs.next()) {
                    System.out.println("Total users in database: " + rs.getInt("user_count"));
                }
                
                // Get database info
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("\nDatabase Information:");
                System.out.println("Database Name: " + meta.getDatabaseProductName());
                System.out.println("Database Version: " + meta.getDatabaseProductVersion());
                System.out.println("Driver Name: " + meta.getDriverName());
                System.out.println("Driver Version: " + meta.getDriverVersion());
                
                // List tables
                System.out.println("\nAvailable Tables:");
                rs = meta.getTables(null, null, "%", new String[]{"TABLE"});
                int tableCount = 0;
                while (rs.next()) {
                    System.out.println("  - " + rs.getString("TABLE_NAME"));
                    tableCount++;
                }
                System.out.println("Total tables: " + tableCount);
                
                // Close connections
                rs.close();
                stmt.close();
                conn.close();
                
                System.out.println("\n✅ All tests passed!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed!");
            e.printStackTrace();
        }
    }
}