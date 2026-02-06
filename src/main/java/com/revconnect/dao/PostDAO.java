package com.revconnect.dao;

import com.revconnect.models.Post;
import com.revconnect.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    // Create post
    public int createPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (user_id, content, media_url, post_type, visibility) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, post.getUserId());
            pstmt.setString(2, post.getContent());
            pstmt.setString(3, post.getMediaUrl());
            pstmt.setString(4, post.getPostType().name());
            pstmt.setString(5, post.getVisibility().name());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating post failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int postId = generatedKeys.getInt(1);
                    System.out.println("Post created successfully with ID: " + postId);
                    return postId;
                } else {
                    throw new SQLException("Creating post failed, no ID obtained.");
                }
            }
        }
    }

    // Get post by ID
    public Post getPostById(int postId) throws SQLException {
        String sql = "SELECT * FROM posts WHERE post_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPost(rs);
            }
            return null;
        }
    }

    // Get posts by user
    public List<Post> getPostsByUser(int userId, int limit, int offset) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        }
        return posts;
    }

    // Get feed posts for user (posts from connections and followed users)
    public List<Post> getFeedPosts(int userId, int limit, int offset) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.* FROM posts p " +
                "LEFT JOIN connections c ON (p.user_id = c.user_id1 OR p.user_id = c.user_id2) " +
                "LEFT JOIN follows f ON p.user_id = f.followed_id " +
                "WHERE (c.status = 'ACCEPTED' AND (c.user_id1 = ? OR c.user_id2 = ?)) " +
                "OR f.follower_id = ? " +
                "OR p.user_id = ? " +  // User's own posts
                "OR p.visibility = 'PUBLIC' " +
                "GROUP BY p.post_id " +
                "ORDER BY p.created_at DESC " +
                "LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, userId);
            pstmt.setInt(5, limit);
            pstmt.setInt(6, offset);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        }
        return posts;
    }

    // Update post
    public boolean updatePost(Post post) throws SQLException {
        String sql = "UPDATE posts SET content = ?, media_url = ?, post_type = ?, " +
                "visibility = ?, updated_at = CURRENT_TIMESTAMP WHERE post_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, post.getContent());
            pstmt.setString(2, post.getMediaUrl());
            pstmt.setString(3, post.getPostType().name());
            pstmt.setString(4, post.getVisibility().name());
            pstmt.setInt(5, post.getPostId());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Updated post with ID: " + post.getPostId() + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        }
    }

    // Delete post
    public boolean deletePost(int postId) throws SQLException {
        String sql = "DELETE FROM posts WHERE post_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted post with ID: " + postId + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        }
    }

    // Update like count
    public void updateLikeCount(int postId, int change) throws SQLException {
        String sql = "UPDATE posts SET like_count = like_count + ? WHERE post_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, change);
            pstmt.setInt(2, postId);
            pstmt.executeUpdate();
        }
    }

    // Update comment count
    public void updateCommentCount(int postId, int change) throws SQLException {
        String sql = "UPDATE posts SET comment_count = comment_count + ? WHERE post_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, change);
            pstmt.setInt(2, postId);
            pstmt.executeUpdate();
        }
    }

    // Update share count
    public void updateShareCount(int postId, int change) throws SQLException {
        String sql = "UPDATE posts SET share_count = share_count + ? WHERE post_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, change);
            pstmt.setInt(2, postId);
            pstmt.executeUpdate();
        }
    }

    // Search posts by content
    public List<Post> searchPosts(String query) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE content LIKE ? AND visibility = 'PUBLIC' " +
                "ORDER BY created_at DESC LIMIT 50";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        }
        return posts;
    }

    // Get trending posts (most liked in last 7 days)
    public List<Post> getTrendingPosts(int limit) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts " +
                "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                "AND visibility = 'PUBLIC' " +
                "ORDER BY like_count DESC, comment_count DESC " +
                "LIMIT ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(mapResultSetToPost(rs));
            }
        }
        return posts;
    }

    // Helper method to map ResultSet to Post
    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();

        post.setPostId(rs.getInt("post_id"));
        post.setUserId(rs.getInt("user_id"));
        post.setContent(rs.getString("content"));
        post.setMediaUrl(rs.getString("media_url"));
        post.setPostType(Post.PostType.valueOf(rs.getString("post_type")));
        post.setLikeCount(rs.getInt("like_count"));
        post.setCommentCount(rs.getInt("comment_count"));
        post.setShareCount(rs.getInt("share_count"));
        post.setVisibility(Post.Visibility.valueOf(rs.getString("visibility")));
        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return post;
    }
}