package com.revconnect.dao;

import com.revconnect.models.Connection;
import com.revconnect.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDAO {

    // Send connection request
    public int sendConnectionRequest(int userId1, int userId2) throws SQLException {
        // Ensure userId1 is always smaller than userId2 for consistency
        int smallerId = Math.min(userId1, userId2);
        int largerId = Math.max(userId1, userId2);

        String sql = "INSERT INTO connections (user_id1, user_id2, status) VALUES (?, ?, 'PENDING')";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, smallerId);
            pstmt.setInt(2, largerId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Sending connection request failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int connectionId = generatedKeys.getInt(1);
                    System.out.println("Connection request sent from user " + userId1 + " to user " + userId2);
                    return connectionId;
                } else {
                    throw new SQLException("Sending connection request failed, no ID obtained.");
                }
            }
        }
    }

    // Accept connection request
    public boolean acceptConnectionRequest(int connectionId) throws SQLException {
        String sql = "UPDATE connections SET status = 'ACCEPTED', updated_at = CURRENT_TIMESTAMP " +
                "WHERE connection_id = ? AND status = 'PENDING'";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, connectionId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Connection request " + connectionId + " accepted");
            return rowsAffected > 0;
        }
    }

    // Reject connection request
    public boolean rejectConnectionRequest(int connectionId) throws SQLException {
        String sql = "UPDATE connections SET status = 'REJECTED', updated_at = CURRENT_TIMESTAMP " +
                "WHERE connection_id = ? AND status = 'PENDING'";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, connectionId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Connection request " + connectionId + " rejected");
            return rowsAffected > 0;
        }
    }

    // Block connection
    public boolean blockConnection(int connectionId) throws SQLException {
        String sql = "UPDATE connections SET status = 'BLOCKED', updated_at = CURRENT_TIMESTAMP " +
                "WHERE connection_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, connectionId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Connection " + connectionId + " blocked");
            return rowsAffected > 0;
        }
    }

    // Remove connection
    public boolean removeConnection(int connectionId) throws SQLException {
        String sql = "DELETE FROM connections WHERE connection_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, connectionId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Connection " + connectionId + " removed");
            return rowsAffected > 0;
        }
    }

    // Get connection by ID
    public Connection getConnectionById(int connectionId) throws SQLException {
        String sql = "SELECT * FROM connections WHERE connection_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, connectionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToConnection(rs);
            }
            return null;
        }
    }

    // Get connection between two users
    public Connection getConnectionBetweenUsers(int userId1, int userId2) throws SQLException {
        int smallerId = Math.min(userId1, userId2);
        int largerId = Math.max(userId1, userId2);

        String sql = "SELECT * FROM connections WHERE user_id1 = ? AND user_id2 = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, smallerId);
            pstmt.setInt(2, largerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToConnection(rs);
            }
            return null;
        }
    }

    // Get pending connection requests for a user
    public List<Connection> getPendingRequestsForUser(int userId) throws SQLException {
        List<Connection> connections = new ArrayList<>();
        String sql = "SELECT * FROM connections WHERE status = 'PENDING' AND " +
                "(user_id1 = ? OR user_id2 = ?)";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                connections.add(mapResultSetToConnection(rs));
            }
        }
        return connections;
    }

    // Get accepted connections for a user
    public List<Connection> getAcceptedConnectionsForUser(int userId) throws SQLException {
        List<Connection> connections = new ArrayList<>();
        String sql = "SELECT * FROM connections WHERE status = 'ACCEPTED' AND " +
                "(user_id1 = ? OR user_id2 = ?)";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                connections.add(mapResultSetToConnection(rs));
            }
        }
        return connections;
    }

    // Get connection count for user
    public int getConnectionCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM connections WHERE status = 'ACCEPTED' AND " +
                "(user_id1 = ? OR user_id2 = ?)";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    // Check if users are connected
    public boolean areUsersConnected(int userId1, int userId2) throws SQLException {
        Connection connection = getConnectionBetweenUsers(userId1, userId2);
        return connection != null && connection.isAccepted();
    }

    // Get suggested connections (users not yet connected)
    public List<Integer> getSuggestedConnections(int userId, int limit) throws SQLException {
        List<Integer> suggestions = new ArrayList<>();

        // Find users who are connected to user's connections but not yet connected to the user
        String sql = "SELECT DISTINCT u.user_id FROM users u " +
                "WHERE u.user_id != ? " +
                "AND u.is_active = TRUE " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM connections c " +
                "    WHERE ((c.user_id1 = ? AND c.user_id2 = u.user_id) " +
                "           OR (c.user_id1 = u.user_id AND c.user_id2 = ?)) " +
                "    AND c.status IN ('PENDING', 'ACCEPTED') " +
                ") " +
                "ORDER BY u.followers_count DESC " +
                "LIMIT ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                suggestions.add(rs.getInt("user_id"));
            }
        }
        return suggestions;
    }

    // Helper method to map ResultSet to Connection
    private Connection mapResultSetToConnection(ResultSet rs) throws SQLException {
        Connection connection = new Connection();

        connection.setConnectionId(rs.getInt("connection_id"));
        connection.setUserId1(rs.getInt("user_id1"));
        connection.setUserId2(rs.getInt("user_id2"));
        connection.setStatus(Connection.ConnectionStatus.valueOf(rs.getString("status")));
        connection.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        connection.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return connection;
    }
}