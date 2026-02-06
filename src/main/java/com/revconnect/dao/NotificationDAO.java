package com.revconnect.dao;

import com.revconnect.models.Notification;
import com.revconnect.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    // Create notification
    public int createNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, sender_id, type, content, " +
                "reference_id, reference_type, is_read) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, notification.getUserId());

            if (notification.getSenderId() != null) {
                pstmt.setInt(2, notification.getSenderId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setString(3, notification.getType().name());
            pstmt.setString(4, notification.getContent());

            if (notification.getReferenceId() != null) {
                pstmt.setInt(5, notification.getReferenceId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.setString(6, notification.getReferenceType());
            pstmt.setBoolean(7, notification.isRead());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating notification failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int notificationId = generatedKeys.getInt(1);
                    System.out.println("Notification created with ID: " + notificationId);
                    return notificationId;
                } else {
                    throw new SQLException("Creating notification failed, no ID obtained.");
                }
            }
        }
    }

    // Get notification by ID
    public Notification getNotificationById(int notificationId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE notification_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToNotification(rs);
            }
            return null;
        }
    }

    // Get notifications for user
    public List<Notification> getNotificationsForUser(int userId, int limit, int offset) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? " +
                "ORDER BY created_at DESC, is_read ASC LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        }
        return notifications;
    }

    // Get unread notifications for user
    public List<Notification> getUnreadNotificationsForUser(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = FALSE " +
                "ORDER BY created_at DESC";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        }
        return notifications;
    }

    // Mark notification as read
    public boolean markAsRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Marked notification " + notificationId + " as read");
            return rowsAffected > 0;
        }
    }

    // Mark all notifications as read for user
    public boolean markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Marked all notifications as read for user " + userId);
            return rowsAffected > 0;
        }
    }

    // Delete notification
    public boolean deleteNotification(int notificationId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted notification " + notificationId);
            return rowsAffected > 0;
        }
    }

    // Delete all read notifications for user
    public boolean deleteAllReadNotifications(int userId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE user_id = ? AND is_read = TRUE";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted all read notifications for user " + userId);
            return rowsAffected > 0;
        }
    }

    // Get notification count for user
    public int getNotificationCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    // Get unread notification count for user
    public int getUnreadNotificationCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = FALSE";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    // Create connection request notification
    public int createConnectionRequestNotification(int receiverId, int senderId, int connectionId) throws SQLException {
        Notification notification = new Notification(
                receiverId,
                Notification.NotificationType.CONNECTION_REQUEST,
                "You have a new connection request"
        );
        notification.setSenderId(senderId);
        notification.setReferenceId(connectionId);
        notification.setReferenceType("CONNECTION");

        return createNotification(notification);
    }

    // Create connection accepted notification
    public int createConnectionAcceptedNotification(int receiverId, int senderId, int connectionId) throws SQLException {
        Notification notification = new Notification(
                receiverId,
                Notification.NotificationType.CONNECTION_ACCEPTED,
                "Your connection request was accepted"
        );
        notification.setSenderId(senderId);
        notification.setReferenceId(connectionId);
        notification.setReferenceType("CONNECTION");

        return createNotification(notification);
    }

    // Helper method to map ResultSet to Notification
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();

        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setUserId(rs.getInt("user_id"));

        int senderId = rs.getInt("sender_id");
        if (!rs.wasNull()) {
            notification.setSenderId(senderId);
        }

        notification.setType(Notification.NotificationType.valueOf(rs.getString("type")));
        notification.setContent(rs.getString("content"));

        int referenceId = rs.getInt("reference_id");
        if (!rs.wasNull()) {
            notification.setReferenceId(referenceId);
        }

        notification.setReferenceType(rs.getString("reference_type"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return notification;
    }
}