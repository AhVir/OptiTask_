package group;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final Map<Integer, ClientHandler> clients = new HashMap<>();
    private static final Map<Integer, List<Integer>> groupUserMap = new HashMap<>();  // groupId -> list of userIds
    private static final Map<Integer, PrintWriter> groupMembers = new HashMap<>();  // userId -> PrintWriter

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private int groupId;
        private int userId;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String input = in.readLine();
                System.out.println("Received connection from user with input: " + input);  // Debugging line
                String[] parts = input.split(",");
                groupId = Integer.parseInt(parts[0]);
                userId = Integer.parseInt(parts[1]);

                // Register the user for this group
                synchronized (clients) {
                    clients.put(userId, this);
                }
                System.out.println("User " + userId + " connected to group " + groupId);  // Debugging line

                // Add user to the group
                synchronized (groupUserMap) {
                    groupUserMap.computeIfAbsent(groupId, k -> new ArrayList<>()).add(userId);
                }

                // Listen for messages from the client
                String message;
                while ((message = in.readLine()) != null) {
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Clean up
                try {
                    clientSocket.close();
                    synchronized (clients) {
                        clients.remove(userId);
                    }
                    synchronized (groupUserMap) {
                        List<Integer> groupMembersList = groupUserMap.get(groupId);
                        if (groupMembersList != null) {
                            groupMembersList.remove(Integer.valueOf(userId));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (ClientHandler client : clients.values()) {
                    if (client.groupId == this.groupId) {
                        client.out.println(message);  // Send the message to clients in the same group
                    }
                }
            }
        }
    }
}
