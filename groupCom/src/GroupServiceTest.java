package group;

import java.sql.Connection;
import java.util.List;

public class GroupServiceTest {
    public static void main(String[] args) {
        try {
            // Establish database connection
            Connection connection = DatabaseConnection.getConnection();
            GroupService groupService = new GroupService(connection);

            // Test 1: List existing groups
            System.out.println("Existing groups:");
            List<String> existingGroups = groupService.listExistingGroups();
            existingGroups.forEach(System.out::println);



//            // Test 3: Add a member to a group (userId = 2, groupId = 2)
//            boolean isMemberAdded = groupService.addMemberToGroup(2, 2);
//            if (isMemberAdded) {
//                System.out.println("Member added successfully!");
//            } else {
//                System.out.println("Failed to add member.");
//            }
//
            // Test 2: Check members of a group (groupId = 1)
//            System.out.println("Members of group 2:");
//            List<String> members = groupService.checkMembersOfGroup(2);
//            members.forEach(System.out::println);


            // Test 4: Create a new group (attempting to create a group with an existing name)
//            boolean isGroupCreated = groupService.createGroup("NewGroup", "Description of New Group", 2); // Owner ID: 1
//            if (isGroupCreated) {
//                System.out.println("Group created successfully!");
//            } else {
//                System.out.println("Failed to create group.");
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
