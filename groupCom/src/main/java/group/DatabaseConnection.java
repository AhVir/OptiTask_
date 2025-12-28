package group;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database connection URL and credentials
    private static final String URL = "jdbc:mysql://localhost:3306/groupFeature";
    private static final String USER = "root";  // Replace with your DB username
    private static final String PASSWORD = "";  // Replace with your DB password

    // Method to establish a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("Error from DatabaseConnection and getConnection() method");
            e.printStackTrace();
            throw new SQLException("Database connection error");
        }
    }
}
