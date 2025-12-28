package dashboard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils2 {

    private static final String URL = "jdbc:mysql://localhost:3306/your_database_name";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Error connecting to the database", e);
        }
    }
}
