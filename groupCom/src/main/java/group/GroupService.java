package group;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupService {
    private final Connection connection;

    public GroupService(Connection connection) {
        this.connection = connection;
    }

    public List<String> listExistingGroups() {
        List<String> groupNames = new ArrayList<>();
        String query = "SELECT group_name FROM groups";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                groupNames.add(resultSet.getString("group_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupNames;
    }

    public boolean groupExists(String groupName) {
        String query = "SELECT COUNT(*) FROM groups WHERE group_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, groupName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> listExistingGroupsForUser(int userId) {
        List<String> groupNames = new ArrayList<>();
        String query = """
                SELECT g.group_name FROM groups g
                JOIN group_members gm ON g.id = gm.group_id
                WHERE gm.user_id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId); // Set userId
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                groupNames.add(resultSet.getString("group_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groupNames;
    }

    public boolean addMemberToGroup(int userId, String groupName, String groupJoinKey) {
        String groupQuery = "SELECT id FROM groups WHERE group_name = ? AND join_key = ?";
        int groupId = -1;
        try (PreparedStatement groupStatement = connection.prepareStatement(groupQuery)) {
            groupStatement.setString(1, groupName);
            groupStatement.setString(2, groupJoinKey);
            ResultSet resultSet = groupStatement.executeQuery();
            if (resultSet.next()) {
                groupId = resultSet.getInt("id");
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String checkQuery = "SELECT COUNT(*) FROM group_members WHERE user_id = ? AND group_id = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setInt(1, userId);
            checkStatement.setInt(2, groupId);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String insertQuery = "INSERT INTO group_members (user_id, group_id) VALUES (?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setInt(1, userId);
            insertStatement.setInt(2, groupId);
            int rowsInserted = insertStatement.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String createGroup(String groupName, String description, int ownerId) {
        String checkQuery = "SELECT COUNT(*) FROM groups WHERE group_name = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setString(1, groupName);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return null;  // Group exists
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String joinKey = generateJoinKey();
        String insertQuery = """
                INSERT INTO groups (group_name, description, owner_id, join_key)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, groupName);
            insertStatement.setString(2, description);
            insertStatement.setInt(3, ownerId);
            insertStatement.setString(4, joinKey);
            int rowsInserted = insertStatement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int groupId = generatedKeys.getInt(1);
                    addMemberToGroup(ownerId, groupName, joinKey); // Automatically add the owner
                    return joinKey;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isValidGroup(String groupName, String groupJoinKey) {
        String query = "SELECT COUNT(*) FROM groups WHERE group_name = ? AND join_key = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setString(2, groupJoinKey);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String generateJoinKey() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            key.append((char) (random.nextInt(26) + 'A'));
        }
        return key.toString();
    }

    public int getGroupIdByName(String groupName) {
        String query = "SELECT id FROM groups WHERE group_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if the group name is not found
    }
}
