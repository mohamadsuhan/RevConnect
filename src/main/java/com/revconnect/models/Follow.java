package com.revconnect.models;

import java.time.LocalDateTime;

public class Follow {
    private int followId;
    private int followerId;
    private int followedId;
    private LocalDateTime createdAt;

    // Constructors
    public Follow() {}

    public Follow(int followerId, int followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
    }

    // Getters and Setters
    public int getFollowId() { return followId; }
    public void setFollowId(int followId) { this.followId = followId; }

    public int getFollowerId() { return followerId; }
    public void setFollowerId(int followerId) { this.followerId = followerId; }

    public int getFollowedId() { return followedId; }
    public void setFollowedId(int followedId) { this.followedId = followedId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Follow{" +
                "followId=" + followId +
                ", followerId=" + followerId +
                ", followedId=" + followedId +
                ", createdAt=" + createdAt +
                '}';
    }
}