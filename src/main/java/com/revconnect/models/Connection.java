package com.revconnect.models;

import java.time.LocalDateTime;

public class Connection {
    private int connectionId;
    private int userId1;
    private int userId2;
    private ConnectionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ConnectionStatus {
        PENDING, ACCEPTED, REJECTED, BLOCKED
    }

    // Constructors
    public Connection() {}

    public Connection(int userId1, int userId2, ConnectionStatus status) {
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.status = status;
    }

    // Getters and Setters
    public int getConnectionId() { return connectionId; }
    public void setConnectionId(int connectionId) { this.connectionId = connectionId; }

    public int getUserId1() { return userId1; }
    public void setUserId1(int userId1) { this.userId1 = userId1; }

    public int getUserId2() { return userId2; }
    public void setUserId2(int userId2) { this.userId2 = userId2; }

    public ConnectionStatus getStatus() { return status; }
    public void setStatus(ConnectionStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isAccepted() {
        return status == ConnectionStatus.ACCEPTED;
    }

    public boolean isPending() {
        return status == ConnectionStatus.PENDING;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "connectionId=" + connectionId +
                ", userId1=" + userId1 +
                ", userId2=" + userId2 +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}