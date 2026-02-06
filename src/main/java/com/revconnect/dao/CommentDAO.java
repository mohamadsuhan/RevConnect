package com.revconnect.dao;

import com.revconnect.models.Comment;
import com.revconnect.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    // Create comment
    public int createComment(Comment comment) throws SQLException {
        String sql = "INSERT INTO comments (post_id, user_id, content, parent_comment_id) VALUES (?, ?, ?, ?)";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, comment.getPostId());
            pstmt.setInt(2, comment.getUserId());
            pstmt.setString(3, comment.getContent());

            if (comment.getParentCommentId() != null) {
                pstmt.setInt(4, comment.getParentCommentId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating comment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int commentId = generatedKeys.getInt(1);
                    System.out.println("Comment created successfully with ID: " + commentId);
                    return commentId;
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }
        }
    }

    // Get comment by ID
    public Comment getCommentById(int commentId) throws SQLException {
        String sql = "SELECT * FROM comments WHERE comment_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, commentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToComment(rs);
            }
            return null;
        }
    }

    // Get comments by post
    public List<Comment> getCommentsByPost(int postId, boolean includeReplies) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql;

        if (includeReplies) {
            sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at ASC";
        } else {
            sql = "SELECT * FROM comments WHERE post_id = ? AND parent_comment_id IS NULL ORDER BY created_at ASC";
        }

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        }
        return comments;
    }

    // Get replies to a comment
    public List<Comment> getCommentReplies(int parentCommentId) throws SQLException {
        List<Comment> replies = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE parent_comment_id = ? ORDER BY created_at ASC";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, parentCommentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                replies.add(mapResultSetToComment(rs));
            }
        }
        return replies;
    }

    // Get comments by user
    public List<Comment> getCommentsByUser(int userId, int limit, int offset) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                comments.add(mapResultSetToComment(rs));
            }
        }
        return comments;
    }

    // Update comment
    public boolean updateComment(Comment comment) throws SQLException {
        String sql = "UPDATE comments SET content = ? WHERE comment_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, comment.getContent());
            pstmt.setInt(2, comment.getCommentId());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Updated comment with ID: " + comment.getCommentId() + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        }
    }

    // Delete comment
    public boolean deleteComment(int commentId) throws SQLException {
        String sql = "DELETE FROM comments WHERE comment_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, commentId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted comment with ID: " + commentId + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        }
    }

    // Update like count
    public void updateLikeCount(int commentId, int change) throws SQLException {
        String sql = "UPDATE comments SET like_count = like_count + ? WHERE comment_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, change);
            pstmt.setInt(2, commentId);
            pstmt.executeUpdate();
        }
    }

    // Get comment count for post
    public int getCommentCountForPost(int postId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM comments WHERE post_id = ?";

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

    // Helper method to map ResultSet to Comment
    private Comment mapResultSetToComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();

        comment.setCommentId(rs.getInt("comment_id"));
        comment.setPostId(rs.getInt("post_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setContent(rs.getString("content"));

        int parentCommentId = rs.getInt("parent_comment_id");
        if (!rs.wasNull()) {
            comment.setParentCommentId(parentCommentId);
        }

        comment.setLikeCount(rs.getInt("like_count"));
        comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return comment;
    }
}