package com.revconnect.models;

import java.time.LocalDateTime;

public class Post {
    private int postId;
    private int userId;
    private String content;
    private String mediaUrl;
    private PostType postType;
    private int likeCount;
    private int commentCount;
    private int shareCount;
    private Visibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum PostType {
        TEXT, IMAGE, VIDEO, LINK
    }

    public enum Visibility {
        PUBLIC, CONNECTIONS, PRIVATE
    }

    // Constructors
    public Post() {}

    public Post(int userId, String content, PostType postType, Visibility visibility) {
        this.userId = userId;
        this.content = content;
        this.postType = postType;
        this.visibility = visibility;
    }

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public PostType getPostType() { return postType; }
    public void setPostType(PostType postType) { this.postType = postType; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public int getShareCount() { return shareCount; }
    public void setShareCount(int shareCount) { this.shareCount = shareCount; }

    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", userId=" + userId +
                ", content='" + (content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                ", postType=" + postType +
                ", likes=" + likeCount +
                ", comments=" + commentCount +
                ", visibility=" + visibility +
                ", createdAt=" + createdAt +
                '}';
    }
}