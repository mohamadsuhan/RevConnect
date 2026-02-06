package com.revconnect.dao;

import com.revconnect.models.Follow;
import com.revconnect.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowDAO {

    // Follow user
    public boolean followUser(int followerId, int followedId) throws SQLException {
        String sql = "INSERT INTO follows (follower_id, followed_id) VALUES (?, ?)";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followedId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("User " + followerId + " followed user " + followedId);
            return rowsAffected > 0;
        }
    }

    // Unfollow user
    public boolean unfollowUser(int followerId, int followedId) throws SQLException {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND followed_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followedId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("User " + followerId + " unfollowed user " + followedId);
            return rowsAffected > 0;
        }
    }

    // Check if user is following another user
    public boolean isFollowing(int followerId, int followedId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM follows WHERE follower_id = ? AND followed_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, followerId);
            pstmt.setInt(2, followedId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            return false;
        }
    }

    // Get followers for a user
    public List<Follow> getFollowers(int userId) throws SQLException {
        List<Follow> followers = new ArrayList<>();
        String sql = "SELECT * FROM follows WHERE followed_id = ? ORDER BY created_at DESC";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                followers.add(mapResultSetToFollow(rs));
            }
        }
        return followers;
    }

    // Get users followed by a user
    public List<Follow> getFollowing(int userId) throws SQLException {
        List<Follow> following = new ArrayList<>();
        String sql = "SELECT * FROM follows WHERE follower_id = ? ORDER BY created_at DESC";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                following.add(mapResultSetToFollow(rs));
            }
        }
        return following;
    }

    // Get follower IDs for a user
    public List<Integer> getFollowerIds(int userId) throws SQLException {
        List<Integer> followerIds = new ArrayList<>();
        String sql = "SELECT follower_id FROM follows WHERE followed_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                followerIds.add(rs.getInt("follower_id"));
            }
        }
        return followerIds;
    }

    // Get following IDs for a user
    public List<Integer> getFollowingIds(int userId) throws SQLException {
        List<Integer> followingIds = new ArrayList<>();
        String sql = "SELECT followed_id FROM follows WHERE follower_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                followingIds.add(rs.getInt("followed_id"));
            }
        }
        return followingIds;
    }

    // Get follower count
    public int getFollowerCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM follows WHERE followed_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    // Get following count
    public int getFollowingCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM follows WHERE follower_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
            return 0;
        }
    }

    // Get mutual followers
    public List<Integer> getMutualFollowers(int userId1, int userId2) throws SQLException {
        List<Integer> mutuals = new ArrayList<>();
        String sql = "SELECT f1.follower_id FROM follows f1 " +
                "INNER JOIN follows f2 ON f1.follower_id = f2.follower_id " +
                "WHERE f1.followed_id = ? AND f2.followed_id = ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId1);
            pstmt.setInt(2, userId2);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                mutuals.add(rs.getInt("follower_id"));
            }
        }
        return mutuals;
    }

    // Get suggested users to follow
    public List<Integer> getSuggestedUsersToFollow(int userId, int limit) throws SQLException {
        List<Integer> suggestions = new ArrayList<>();

        // Find users followed by people you follow but you don't follow yet
        String sql = "SELECT DISTINCT f2.followed_id FROM follows f1 " +
                "INNER JOIN follows f2 ON f1.followed_id = f2.follower_id " +
                "WHERE f1.follower_id = ? " +
                "AND f2.followed_id != ? " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM follows f3 " +
                "    WHERE f3.follower_id = ? AND f3.followed_id = f2.followed_id " +
                ") " +
                "ORDER BY RAND() " +
                "LIMIT ?";

        try (java.sql.Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                suggestions.add(rs.getInt("followed_id"));
            }
        }
        return suggestions;
    }

    // Helper method to map ResultSet to Follow
    private Follow mapResultSetToFollow(ResultSet rs) throws SQLException {
        Follow follow = new Follow();

        follow.setFollowId(rs.getInt("follow_id"));
        follow.setFollowerId(rs.getInt("follower_id"));
        follow.setFollowedId(rs.getInt("followed_id"));
        follow.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return follow;
    }
}