package com.revconnect.services;

import com.revconnect.dao.NotificationDAO;
import com.revconnect.models.Notification;
import com.revconnect.models.User;

import java.sql.SQLException;
import java.util.List;

public class NotificationService {
    private NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    // Create notification
    public int createNotification(Notification notification) {
        try {
            return notificationDAO.createNotification(notification);
        } catch (SQLException e) {
            System.out.println("Error creating notification: " + e.getMessage());
            return -1;
        }
    }

    // Get notification by ID
    public Notification getNotificationById(int notificationId) {
        try {
            return notificationDAO.getNotificationById(notificationId);
        } catch (SQLException e) {
            System.out.println("Error retrieving notification: " + e.getMessage());
            return null;
        }
    }

    // Get notifications for user
    public List<Notification> getNotificationsForUser(int userId, int limit, int offset) {
        try {
            return notificationDAO.getNotificationsForUser(userId, limit, offset);
        } catch (SQLException e) {
            System.out.println("Error retrieving notifications: " + e.getMessage());
            return null;
        }
    }

    // Get unread notifications for user
    public List<Notification> getUnreadNotificationsForUser(int userId) {
        try {
            return notificationDAO.getUnreadNotificationsForUser(userId);
        } catch (SQLException e) {
            System.out.println("Error retrieving unread notifications: " + e.getMessage());
            return null;
        }
    }

    // Mark notification as read
    public boolean markAsRead(int notificationId) {
        try {
            return notificationDAO.markAsRead(notificationId);
        } catch (SQLException e) {
            System.out.println("Error marking notification as read: " + e.getMessage());
            return false;
        }
    }

    // Mark all notifications as read for user
    public boolean markAllAsRead(int userId) {
        try {
            return notificationDAO.markAllAsRead(userId);
        } catch (SQLException e) {
            System.out.println("Error marking all notifications as read: " + e.getMessage());
            return false;
        }
    }

    // Delete notification
    public boolean deleteNotification(int notificationId, int userId) {
        try {
            Notification notification = notificationDAO.getNotificationById(notificationId);
            if (notification == null) {
                System.out.println("Notification not found.");
                return false;
            }

            // Check ownership
            if (notification.getUserId() != userId) {
                System.out.println("You can only delete your own notifications.");
                return false;
            }

            return notificationDAO.deleteNotification(notificationId);

        } catch (SQLException e) {
            System.out.println("Error deleting notification: " + e.getMessage());
            return false;
        }
    }

    // Delete all read notifications for user
    public boolean deleteAllReadNotifications(int userId) {
        try {
            return notificationDAO.deleteAllReadNotifications(userId);
        } catch (SQLException e) {
            System.out.println("Error deleting read notifications: " + e.getMessage());
            return false;
        }
    }

    // Get notification count for user
    public int getNotificationCount(int userId) {
        try {
            return notificationDAO.getNotificationCount(userId);
        } catch (SQLException e) {
            System.out.println("Error getting notification count: " + e.getMessage());
            return 0;
        }
    }

    // Get unread notification count for user
    public int getUnreadNotificationCount(int userId) {
        try {
            return notificationDAO.getUnreadNotificationCount(userId);
        } catch (SQLException e) {
            System.out.println("Error getting unread notification count: " + e.getMessage());
            return 0;
        }
    }

    // Create new post notification (for followers)
    public void createNewPostNotification(int postId, int authorId, String postContent) {
        try {
            // In a real app, you would notify all followers
            // For now, we'll just log it
            System.out.println("New post notification would be sent to followers of user " + authorId);
            System.out.println("Post content: " + (postContent.length() > 50 ?
                    postContent.substring(0, 50) + "..." : postContent));

        } catch (Exception e) {
            System.out.println("Error creating post notification: " + e.getMessage());
        }
    }

    // Create new comment notification
    public void createNewCommentNotification(int postId, int commenterId, int postAuthorId, String commentContent) {
        try {
            if (commenterId != postAuthorId) {
                Notification notification = new Notification(
                        postAuthorId,
                        Notification.NotificationType.NEW_COMMENT,
                        "Someone commented on your post: " +
                                (commentContent.length() > 30 ? commentContent.substring(0, 30) + "..." : commentContent)
                );
                notification.setSenderId(commenterId);
                notification.setReferenceId(postId);
                notification.setReferenceType("POST");

                notificationDAO.createNotification(notification);
            }
        } catch (SQLException e) {
            System.out.println("Error creating comment notification: " + e.getMessage());
        }
    }

    // Create new like notification
    public void createNewLikeNotification(int postId, int likerId, int postAuthorId) {
        try {
            if (likerId != postAuthorId) {
                Notification notification = new Notification(
                        postAuthorId,
                        Notification.NotificationType.NEW_LIKE,
                        "Someone liked your post"
                );
                notification.setSenderId(likerId);
                notification.setReferenceId(postId);
                notification.setReferenceType("POST");

                notificationDAO.createNotification(notification);
            }
        } catch (SQLException e) {
            System.out.println("Error creating like notification: " + e.getMessage());
        }
    }

    // Create new follow notification
    public void createNewFollowNotification(int followerId, int followedId) {
        try {
            Notification notification = new Notification(
                    followedId,
                    Notification.NotificationType.NEW_FOLLOW,
                    "Someone started following you"
            );
            notification.setSenderId(followerId);
            notification.setReferenceType("USER");

            notificationDAO.createNotification(notification);

        } catch (SQLException e) {
            System.out.println("Error creating follow notification: " + e.getMessage());
        }
    }

    // Display notifications
    public void displayNotifications(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            System.out.println("No notifications to display.");
            return;
        }

        System.out.println("\n══════════════════════════════════════");
        System.out.println("        NOTIFICATIONS (" + notifications.size() + ")       ");
        System.out.println("══════════════════════════════════════");

        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);

            String readStatus = notification.isRead() ? "[READ]" : "[NEW]";
            String typeIcon = getNotificationIcon(notification.getType());

            System.out.println("\n" + readStatus + " " + typeIcon + " " + (i + 1) + ". " + notification.getContent());
            System.out.println("   Type: " + notification.getType());
            System.out.println("   Time: " + notification.getCreatedAt().toLocalDate() +
                    " " + notification.getCreatedAt().toLocalTime());

            if (notification.getReferenceId() != null) {
                System.out.println("   Reference ID: " + notification.getReferenceId());
            }

            if (i < notifications.size() - 1) {
                System.out.println("   ──────────────────────────────────────");
            }
        }
    }

    // Helper method to get notification icon
    private String getNotificationIcon(Notification.NotificationType type) {
        switch (type) {
            case CONNECTION_REQUEST:
                return "🔗";
            case CONNECTION_ACCEPTED:
                return "✅";
            case NEW_POST:
                return "📝";
            case NEW_COMMENT:
                return "💬";
            case NEW_LIKE:
                return "❤️";
            case NEW_FOLLOW:
                return "👤";
            case MENTION:
                return "📢";
            default:
                return "🔔";
        }
    }
}