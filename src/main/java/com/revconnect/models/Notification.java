package com.revconnect.models;

import java.time.LocalDateTime;

public class Notification {
    private int notificationId;
    private int userId;
    private Integer senderId;
    private NotificationType type;
    private String content;
    private Integer referenceId;
    private String referenceType;
    private boolean isRead;
    private LocalDateTime createdAt;

    public enum NotificationType {
        CONNECTION_REQUEST,
        CONNECTION_ACCEPTED,
        NEW_POST,
        NEW_COMMENT,
        NEW_LIKE,
        NEW_FOLLOW,
        MENTION
    }

    // Constructors
    public Notification() {}

    public Notification(int userId, NotificationType type, String content) {
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.isRead = false;
    }

    // Getters and Setters
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getReferenceId() { return referenceId; }
    public void setReferenceId(Integer referenceId) { this.referenceId = referenceId; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getShortContent() {
        if (content.length() > 50) {
            return content.substring(0, 50) + "...";
        }
        return content;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", type=" + type +
                ", content='" + getShortContent() + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}