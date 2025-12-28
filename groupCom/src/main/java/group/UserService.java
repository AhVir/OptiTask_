package group;

import java.sql.*;

public class UserService {

    // Check if the username is available
    public static boolean isUsernameAvailable(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Username is available if count is 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Register a new user
    public static void register(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);  // For production, hash the password here
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Validate login credentials
    public static boolean isValidLogin(String username, String password) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);  // In production, check hashed password
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if user exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get the user ID based on username and password
    public static int getUserIdByUsernameAndPassword(String username, String password) {
        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);  // In production, check hashed password
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");  // Return user ID if valid
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if invalid login
    }
}
