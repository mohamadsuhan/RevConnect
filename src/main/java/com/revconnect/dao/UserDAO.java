package com.revconnect.dao;

import com.revconnect.models.User;
import com.revconnect.models.PersonalUser;
import com.revconnect.models.BusinessUser;
import com.revconnect.models.CreatorUser;
import com.revconnect.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Create user
    public int createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, first_name, last_name, " +
                "user_type, bio, profile_picture_url, website_url, business_name, " +
                "business_type, creator_category) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.setString(6, user.getUserType().name());
            pstmt.setString(7, user.getBio());
            pstmt.setString(8, user.getProfilePictureUrl());
            pstmt.setString(9, user.getWebsiteUrl());

            // Set user type specific fields
            if (user instanceof BusinessUser) {
                BusinessUser businessUser = (BusinessUser) user;
                pstmt.setString(10, businessUser.getBusinessName());
                pstmt.setString(11, businessUser.getBusinessType());
                pstmt.setString(12, null);
            } else if (user instanceof CreatorUser) {
                CreatorUser creatorUser = (CreatorUser) user;
                pstmt.setString(10, null);
                pstmt.setString(11, null);
                pstmt.setString(12, creatorUser.getCreatorCategory());
            } else {
                pstmt.setString(10, null);
                pstmt.setString(11, null);
                pstmt.setString(12, null);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    System.out.println("User created successfully with ID: " + userId);
                    return userId;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    // Get user by ID
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        }
    }

    // Get user by username
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        }
    }

    // Get user by email
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        }
    }

    // Update user
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ?, first_name = ?, last_name = ?, " +
                "bio = ?, profile_picture_url = ?, website_url = ?, business_name = ?, " +
                "business_type = ?, creator_category = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getBio());
            pstmt.setString(6, user.getProfilePictureUrl());
            pstmt.setString(7, user.getWebsiteUrl());

            // Set user type specific fields
            if (user instanceof BusinessUser) {
                BusinessUser businessUser = (BusinessUser) user;
                pstmt.setString(8, businessUser.getBusinessName());
                pstmt.setString(9, businessUser.getBusinessType());
                pstmt.setString(10, null);
            } else if (user instanceof CreatorUser) {
                CreatorUser creatorUser = (CreatorUser) user;
                pstmt.setString(8, null);
                pstmt.setString(9, null);
                pstmt.setString(10, creatorUser.getCreatorCategory());
            } else {
                pstmt.setString(8, null);
                pstmt.setString(9, null);
                pstmt.setString(10, null);
            }

            pstmt.setInt(11, user.getUserId());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Updated user with ID: " + user.getUserId() + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        }
    }

    // Delete user (soft delete)
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Soft deleted user with ID: " + userId + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        }
    }

    // Get all users (with pagination)
    public List<User> getAllUsers(int limit, int offset) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    // Search users by name or username
    public List<User> searchUsers(String query) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = TRUE AND " +
                "(username LIKE ? OR first_name LIKE ? OR last_name LIKE ? OR business_name LIKE ?) " +
                "ORDER BY username";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    // Update password
    public boolean updatePassword(int userId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Updated password for user ID: " + userId);
            return rowsAffected > 0;
        }
    }

    // Update follower/following counts
    public void updateFollowerCount(int userId, int change) throws SQLException {
        String sql = "UPDATE users SET followers_count = followers_count + ? WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, change);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    public void updateFollowingCount(int userId, int change) throws SQLException {
        String sql = "UPDATE users SET following_count = following_count + ? WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, change);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    // Helper method to map ResultSet to appropriate User subclass
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User.UserType userType = User.UserType.valueOf(rs.getString("user_type"));
        User user;

        switch (userType) {
            case BUSINESS:
                BusinessUser businessUser = new BusinessUser();
                businessUser.setBusinessName(rs.getString("business_name"));
                businessUser.setBusinessType(rs.getString("business_type"));
                user = businessUser;
                break;
            case CREATOR:
                CreatorUser creatorUser = new CreatorUser();
                creatorUser.setCreatorCategory(rs.getString("creator_category"));
                user = creatorUser;
                break;
            default:
                user = new PersonalUser();
                break;
        }

        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setUserType(userType);
        user.setBio(rs.getString("bio"));
        user.setProfilePictureUrl(rs.getString("profile_picture_url"));
        user.setWebsiteUrl(rs.getString("website_url"));
        user.setFollowersCount(rs.getInt("followers_count"));
        user.setFollowingCount(rs.getInt("following_count"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        user.setActive(rs.getBoolean("is_active"));

        return user;
    }
}