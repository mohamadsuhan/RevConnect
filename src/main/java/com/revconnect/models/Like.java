package com.revconnect.models;

import java.time.LocalDateTime;

public class Like {
    private int likeId;
    private int userId;
    private Integer postId;
    private Integer commentId;
    private LocalDateTime createdAt;

    // Constructors
    public Like() {}

    public Like(int userId, int postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public Like(int userId, Integer postId, Integer commentId) {
        this.userId = userId;
        this.postId = postId;
        this.commentId = commentId;
    }

    // Getters and Setters
    public int getLikeId() { return likeId; }
    public void setLikeId(int likeId) { this.likeId = likeId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }

    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isPostLike() {
        return postId != null;
    }

    public boolean isCommentLike() {
        return commentId != null;
    }

    @Override
    public String toString() {
        if (isPostLike()) {
            return "Like{" +
                    "likeId=" + likeId +
                    ", userId=" + userId +
                    ", postId=" + postId +
                    ", createdAt=" + createdAt +
                    '}';
        } else {
            return "Like{" +
                    "likeId=" + likeId +
                    ", userId=" + userId +
                    ", commentId=" + commentId +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }
}