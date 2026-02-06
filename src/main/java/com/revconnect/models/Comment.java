package com.revconnect.models;

import java.time.LocalDateTime;

public class Comment {
    private int commentId;
    private int postId;
    private int userId;
    private String content;
    private Integer parentCommentId;
    private int likeCount;
    private LocalDateTime createdAt;

    // Constructors
    public Comment() {}

    public Comment(int postId, int userId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    public Comment(int postId, int userId, String content, Integer parentCommentId) {
        this(postId, userId, content);
        this.parentCommentId = parentCommentId;
    }

    // Getters and Setters
    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Integer parentCommentId) { this.parentCommentId = parentCommentId; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isReply() {
        return parentCommentId != null;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", postId=" + postId +
                ", userId=" + userId +
                ", content='" + (content.length() > 30 ? content.substring(0, 30) + "..." : content) + '\'' +
                ", parentCommentId=" + parentCommentId +
                ", likes=" + likeCount +
                ", createdAt=" + createdAt +
                '}';
    }
}