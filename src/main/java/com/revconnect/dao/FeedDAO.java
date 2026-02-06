package com.revconnect.dao;

import com.revconnect.models.Post;
import com.revconnect.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedDAO {

    // Get personalized feed for user
    public List<Post> getPersonalizedFeed(int userId, int limit, int offset) throws SQLException {
        List<Post> feed = new ArrayList<>();

        // Complex query to get personalized feed:
        // 1. Posts from connections
        // 2. Posts from followed users
        // 3. Trending posts from users in same industry/category
        // 4. Popular posts (with high engagement)
        String sql = "(SELECT p.*, 10 as priority FROM posts p " +
                "INNER JOIN connections c ON (p.user_id = c.user_id1 OR p.user_id = c.user_id2) " +
                "WHERE c.status = 'ACCEPTED' AND (c.user_id1 = ? OR c.user_id2 = ?) " +
                "AND p.visibility IN ('PUBLIC', 'CONNECTIONS')) " +

                "UNION " +

                "(SELECT p.*, 8 as priority FROM posts p " +
                "INNER JOIN follows f ON p.user_id = f.followed_id " +
                "WHERE f.follower_id = ? " +
                "AND p.visibility IN ('PUBLIC', 'CONNECTIONS')) " +

                "UNION " +

                "(SELECT p.*, 6 as priority FROM posts p " +
                "WHERE p.user_id = ? " +  // User's own posts
                "AND p.visibility IN ('PUBLIC', 'CONNECTIONS', 'PRIVATE')) " +

                "UNION " +

                "(SELECT p.*, 4 as priority FROM posts p " +
                "WHERE p.visibility = 'PUBLIC' " +
                "AND p.like_count + p.comment_count * 2 >= 10 " +  // Popular posts
                "AND p.created_at >= DATE_SUB(NOW(), INTERVAL 3 DAY)) " +

                "ORDER BY priority DESC, created_at DESC " +
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
                feed.add(mapResultSetToPost(rs));
            }
        }

        System.out.println("Retrieved personalized feed for user " + userId + " with " + feed.size() + " posts");
        return feed;
    }

    // Get feed for guest/non-logged in users
    public List<Post> getPublicFeed(int limit, int offset) throws SQLException {
        List<Post> feed = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE visibility = 'PUBLIC' " +
                "ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                feed.add(mapResultSetToPost(rs));
            }
        }
        return feed;
    }

    // Get feed by category/interest
    public List<Post> getFeedByCategory(String category, int limit, int offset) throws SQLException {
        List<Post> feed = new ArrayList<>();

        // This assumes posts have tags or we can infer category from user's profile
        String sql = "SELECT p.* FROM posts p " +
                "INNER JOIN users u ON p.user_id = u.user_id " +
                "WHERE p.visibility = 'PUBLIC' " +
                "AND (u.user_type = 'CREATOR' AND u.creator_category LIKE ? " +
                "     OR u.user_type = 'BUSINESS' AND u.business_type LIKE ?) " +
                "ORDER BY p.like_count DESC, p.created_at DESC " +
                "LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String categoryPattern = "%" + category + "%";
            pstmt.setString(1, categoryPattern);
            pstmt.setString(2, categoryPattern);
            pstmt.setInt(3, limit);
            pstmt.setInt(4, offset);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                feed.add(mapResultSetToPost(rs));
            }
        }
        return feed;
    }

    // Get trending feed (most engaged posts in last 24 hours)
    public List<Post> getTrendingFeed(int limit, int offset) throws SQLException {
        List<Post> feed = new ArrayList<>();
        String sql = "SELECT * FROM posts " +
                "WHERE visibility = 'PUBLIC' " +
                "AND created_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
                "ORDER BY (like_count * 1 + comment_count * 2 + share_count * 3) DESC, " +
                "created_at DESC " +
                "LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                feed.add(mapResultSetToPost(rs));
            }
        }
        return feed;
    }

    // Get feed from specific user's network
    public List<Post> getNetworkFeed(int userId, int limit, int offset) throws SQLException {
        List<Post> feed = new ArrayList<>();

        // Posts from user's direct connections only
        String sql = "SELECT p.* FROM posts p " +
                "INNER JOIN connections c ON (p.user_id = c.user_id1 OR p.user_id = c.user_id2) " +
                "WHERE c.status = 'ACCEPTED' " +
                "AND (c.user_id1 = ? OR c.user_id2 = ?) " +
                "AND p.user_id != ? " +  // Exclude user's own posts
                "AND p.visibility IN ('PUBLIC', 'CONNECTIONS') " +
                "ORDER BY p.created_at DESC " +
                "LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, limit);
            pstmt.setInt(5, offset);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                feed.add(mapResultSetToPost(rs));
            }
        }
        return feed;
    }

    // Get feed with media (images/videos)
    public List<Post> getMediaFeed(int limit, int offset) throws SQLException {
        List<Post> feed = new ArrayList<>();
        String sql = "SELECT * FROM posts " +
                "WHERE visibility = 'PUBLIC' " +
                "AND media_url IS NOT NULL " +
                "AND post_type IN ('IMAGE', 'VIDEO') " +
                "ORDER BY created_at DESC " +
                "LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                feed.add(mapResultSetToPost(rs));
            }
        }
        return feed;
    }

    // Get saved/bookmarked posts for user
    public List<Post> getSavedPosts(int userId, int limit, int offset) throws SQLException {
        List<Post> savedPosts = new ArrayList<>();

        // Note: This requires a bookmarks/saved_posts table
        // For now, we'll return an empty list
        System.out.println("getSavedPosts called for user " + userId + " - feature not implemented yet");

        return savedPosts;
    }

    // Get recommended posts based on user's interaction history
    public List<Post> getRecommendedPosts(int userId, int limit) throws SQLException {
        List<Post> recommendations = new ArrayList<>();

        // Simple recommendation: posts from categories user has interacted with
        String sql = "SELECT DISTINCT p.* FROM posts p " +
                "WHERE p.visibility = 'PUBLIC' " +
                "AND p.post_id NOT IN ( " +
                "    SELECT post_id FROM likes WHERE user_id = ? " +
                "    UNION " +
                "    SELECT post_id FROM comments WHERE user_id = ? " +
                ") " +
                "ORDER BY RAND() " +  // Simple random for now
                "LIMIT ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, limit);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                recommendations.add(mapResultSetToPost(rs));
            }
        }
        return recommendations;
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