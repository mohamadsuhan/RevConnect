package com.revconnect.dao;

import com.revconnect.models.Like;
import com.revconnect.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikeDAO {

    // Add like to post
    public boolean addPostLike(int userId, int postId) throws SQLException {
        String sql = "INSERT INTO likes (user_id, post_id) VALUES (?, ?)";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Added post like - User: " + userId + ", Post: " + postId);
            return rowsAffected > 0;
        }
    }

    // Add like to comment
    public boolean addCommentLike(int userId, int commentId) throws SQLException {
        String sql = "INSERT INTO likes (user_id, comment_id) VALUES (?, ?)";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, commentId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Added comment like - User: " + userId + ", Comment: " + commentId);
            return rowsAffected > 0;
        }
    }

    // Remove like from post
    public boolean removePostLike(int userId, int postId) throws SQLException {
        String sql = "DELETE FROM likes WHERE user_id = ? AND post_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Removed post like - User: " + userId + ", Post: " + postId);
            return rowsAffected > 0;
        }
    }

    // Remove like from comment
    public boolean removeCommentLike(int userId, int commentId) throws SQLException {
        String sql = "DELETE FROM likes WHERE user_id = ? AND comment_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, commentId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Removed comment like - User: " + userId + ", Comment: " + commentId);
            return rowsAffected > 0;
        }
    }

    // Check if user liked a post
    public boolean hasUserLikedPost(int userId, int postId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM likes WHERE user_id = ? AND post_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            return false;
        }
    }

    // Check if user liked a comment
    public boolean hasUserLikedComment(int userId, int commentId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM likes WHERE user_id = ? AND comment_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, commentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            return false;
        }
    }

    // Get likes for a post
    public List<Like> getLikesForPost(int postId) throws SQLException {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM likes WHERE post_id = ? ORDER BY created_at DESC";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                likes.add(mapResultSetToLike(rs));
            }
        }
        return likes;
    }

    // Get likes for a comment
    public List<Like> getLikesForComment(int commentId) throws SQLException {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM likes WHERE comment_id = ? ORDER BY created_at DESC";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, commentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                likes.add(mapResultSetToLike(rs));
            }
        }
        return likes;
    }

    // Get likes by user
    public List<Like> getLikesByUser(int userId, int limit, int offset) throws SQLException {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM likes WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                likes.add(mapResultSetToLike(rs));
            }
        }
        return likes;
    }

    // Get like count for post
    public int getLikeCountForPost(int postId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM likes WHERE post_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    // Get like count for comment
    public int getLikeCountForComment(int commentId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM likes WHERE comment_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, commentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    // Helper method to map ResultSet to Like
    private Like mapResultSetToLike(ResultSet rs) throws SQLException {
        Like like = new Like();

        like.setLikeId(rs.getInt("like_id"));
        like.setUserId(rs.getInt("user_id"));

        int postId = rs.getInt("post_id");
        if (!rs.wasNull()) {
            like.setPostId(postId);
        }

        int commentId = rs.getInt("comment_id");
        if (!rs.wasNull()) {
            like.setCommentId(commentId);
        }

        like.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return like;
    }
}