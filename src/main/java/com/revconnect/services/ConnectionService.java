package com.revconnect.services;

import com.revconnect.dao.ConnectionDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.dao.NotificationDAO;
import com.revconnect.models.Connection;
import com.revconnect.models.User;
import com.revconnect.models.Notification;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionService {
    private ConnectionDAO connectionDAO;
    private UserDAO userDAO;
    private NotificationDAO notificationDAO;

    public ConnectionService() {
        this.connectionDAO = new ConnectionDAO();
        this.userDAO = new UserDAO();
        this.notificationDAO = new NotificationDAO();
    }

    // Send connection request
    public boolean sendConnectionRequest(int senderId, int receiverId) {
        try {
            // Check if users exist
            User sender = userDAO.getUserById(senderId);
            User receiver = userDAO.getUserById(receiverId);

            if (sender == null || receiver == null) {
                System.out.println("User not found.");
                return false;
            }

            // Check if trying to connect with yourself
            if (senderId == receiverId) {
                System.out.println("You cannot send a connection request to yourself.");
                return false;
            }

            // Check if already connected or pending
            Connection existingConnection = connectionDAO.getConnectionBetweenUsers(senderId, receiverId);
            if (existingConnection != null) {
                switch (existingConnection.getStatus()) {
                    case PENDING:
                        System.out.println("Connection request already pending.");
                        break;
                    case ACCEPTED:
                        System.out.println("You are already connected with this user.");
                        break;
                    case REJECTED:
                        System.out.println("Previous connection request was rejected.");
                        break;
                    case BLOCKED:
                        System.out.println("Connection is blocked.");
                        break;
                }
                return false;
            }

            // Send connection request
            int connectionId = connectionDAO.sendConnectionRequest(senderId, receiverId);

            if (connectionId > 0) {
                // Create notification for receiver
                notificationDAO.createConnectionRequestNotification(receiverId, senderId, connectionId);

                System.out.println("Connection request sent to " + receiver.getFirstName() + " " +
                        receiver.getLastName() + "!");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error sending connection request: " + e.getMessage());
        }

        return false;
    }

    // Accept connection request
    public boolean acceptConnectionRequest(int connectionId) {
        try {
            Connection connection = connectionDAO.getConnectionById(connectionId);
            if (connection == null) {
                System.out.println("Connection request not found.");
                return false;
            }

            if (connection.getStatus() != Connection.ConnectionStatus.PENDING) {
                System.out.println("This connection request is no longer pending.");
                return false;
            }

            // Accept the request
            boolean accepted = connectionDAO.acceptConnectionRequest(connectionId);

            if (accepted) {
                // Create notification for sender
                int receiverId = connection.getUserId1();
                int senderId = connection.getUserId2();
                notificationDAO.createConnectionAcceptedNotification(senderId, receiverId, connectionId);

                System.out.println("Connection request accepted!");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error accepting connection request: " + e.getMessage());
        }

        return false;
    }

    // Reject connection request
    public boolean rejectConnectionRequest(int connectionId) {
        try {
            Connection connection = connectionDAO.getConnectionById(connectionId);
            if (connection == null) {
                System.out.println("Connection request not found.");
                return false;
            }

            if (connection.getStatus() != Connection.ConnectionStatus.PENDING) {
                System.out.println("This connection request is no longer pending.");
                return false;
            }

            return connectionDAO.rejectConnectionRequest(connectionId);

        } catch (SQLException e) {
            System.out.println("Error rejecting connection request: " + e.getMessage());
            return false;
        }
    }

    // Remove/delete connection
    public boolean removeConnection(int connectionId, int userId) {
        try {
            Connection connection = connectionDAO.getConnectionById(connectionId);
            if (connection == null) {
                System.out.println("Connection not found.");
                return false;
            }

            // Check if user is part of this connection
            if (connection.getUserId1() != userId && connection.getUserId2() != userId) {
                System.out.println("You are not part of this connection.");
                return false;
            }

            return connectionDAO.removeConnection(connectionId);

        } catch (SQLException e) {
            System.out.println("Error removing connection: " + e.getMessage());
            return false;
        }
    }

    // Block connection
    public boolean blockConnection(int connectionId, int userId) {
        try {
            Connection connection = connectionDAO.getConnectionById(connectionId);
            if (connection == null) {
                System.out.println("Connection not found.");
                return false;
            }

            // Check if user is part of this connection
            if (connection.getUserId1() != userId && connection.getUserId2() != userId) {
                System.out.println("You are not part of this connection.");
                return false;
            }

            return connectionDAO.blockConnection(connectionId);

        } catch (SQLException e) {
            System.out.println("Error blocking connection: " + e.getMessage());
            return false;
        }
    }

    // Get connection by ID
    public Connection getConnectionById(int connectionId) {
        try {
            return connectionDAO.getConnectionById(connectionId);
        } catch (SQLException e) {
            System.out.println("Error retrieving connection: " + e.getMessage());
            return null;
        }
    }

    // Get connection between users
    public Connection getConnectionBetweenUsers(int userId1, int userId2) {
        try {
            return connectionDAO.getConnectionBetweenUsers(userId1, userId2);
        } catch (SQLException e) {
            System.out.println("Error retrieving connection: " + e.getMessage());
            return null;
        }
    }

    // Get pending requests for user
    public List<Connection> getPendingRequests(int userId) {
        try {
            return connectionDAO.getPendingRequestsForUser(userId);
        } catch (SQLException e) {
            System.out.println("Error retrieving pending requests: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get accepted connections for user
    public List<Connection> getAcceptedConnections(int userId) {
        try {
            return connectionDAO.getAcceptedConnectionsForUser(userId);
        } catch (SQLException e) {
            System.out.println("Error retrieving connections: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get connection count
    public int getConnectionCount(int userId) {
        try {
            return connectionDAO.getConnectionCount(userId);
        } catch (SQLException e) {
            System.out.println("Error getting connection count: " + e.getMessage());
            return 0;
        }
    }

    // Check if users are connected
    public boolean areUsersConnected(int userId1, int userId2) {
        try {
            return connectionDAO.areUsersConnected(userId1, userId2);
        } catch (SQLException e) {
            System.out.println("Error checking connection status: " + e.getMessage());
            return false;
        }
    }

    // Get suggested connections
    public List<User> getSuggestedConnections(int userId, int limit) {
        try {
            List<Integer> suggestedUserIds = connectionDAO.getSuggestedConnections(userId, limit);
            List<User> suggestedUsers = new ArrayList<>();

            for (Integer suggestedUserId : suggestedUserIds) {
                User user = userDAO.getUserById(suggestedUserId);
                if (user != null) {
                    suggestedUsers.add(user);
                }
            }

            return suggestedUsers;

        } catch (SQLException e) {
            System.out.println("Error getting suggested connections: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get mutual connections
    public List<User> getMutualConnections(int userId1, int userId2) {
        try {
            List<Connection> user1Connections = connectionDAO.getAcceptedConnectionsForUser(userId1);
            List<Connection> user2Connections = connectionDAO.getAcceptedConnectionsForUser(userId2);

            List<User> mutualConnections = new ArrayList<>();

            for (Connection conn1 : user1Connections) {
                int otherUserId1 = (conn1.getUserId1() == userId1) ? conn1.getUserId2() : conn1.getUserId1();

                for (Connection conn2 : user2Connections) {
                    int otherUserId2 = (conn2.getUserId1() == userId2) ? conn2.getUserId2() : conn2.getUserId1();

                    if (otherUserId1 == otherUserId2) {
                        User mutualUser = userDAO.getUserById(otherUserId1);
                        if (mutualUser != null && !mutualConnections.contains(mutualUser)) {
                            mutualConnections.add(mutualUser);
                        }
                    }
                }
            }

            return mutualConnections;

        } catch (SQLException e) {
            System.out.println("Error getting mutual connections: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Display connection statistics
    public void displayConnectionStats(int userId) {
        try {
            int connectionCount = connectionDAO.getConnectionCount(userId);
            List<Connection> pendingRequests = connectionDAO.getPendingRequestsForUser(userId);

            User user = userDAO.getUserById(userId);

            System.out.println("\n══════════════════════════════════════");
            System.out.println("     CONNECTION STATISTICS     ");
            System.out.println("══════════════════════════════════════");
            System.out.println("User: " + user.getFirstName() + " " + user.getLastName());
            System.out.println("Total Connections: " + connectionCount);
            System.out.println("Pending Requests: " + pendingRequests.size());
            System.out.println("══════════════════════════════════════");

        } catch (SQLException e) {
            System.out.println("Error displaying connection stats: " + e.getMessage());
        }
    }
}