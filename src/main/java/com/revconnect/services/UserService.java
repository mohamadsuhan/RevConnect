package com.revconnect.services;

import com.revconnect.dao.UserDAO;
import com.revconnect.dao.FollowDAO;
import com.revconnect.dao.PostDAO;
import com.revconnect.models.User;
import com.revconnect.models.Follow;
import com.revconnect.models.Post;
import com.revconnect.utils.PasswordHasher;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;

public class UserService {
    private UserDAO userDAO;
    private FollowDAO followDAO;
    private PostDAO postDAO;
    private PasswordHasher passwordHasher;

    // Default constructor (your existing one)
    public UserService() {
        this.userDAO = new UserDAO();
        this.followDAO = new FollowDAO();
        this.postDAO = new PostDAO();
        this.passwordHasher = new PasswordHasher();
    }

    // NEW: Constructor for testing (optional)
    public UserService(Connection connection) {
        this(); // Call default constructor
        // You can use the connection if needed
        // For now, just ignore it since your DAOs create their own connections
    }

    // Get user by ID
    public User getUserById(int userId) {
        try {
            return userDAO.getUserById(userId);
        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
            return null;
        }
    }

    // Get user by username
    public User getUserByUsername(String username) {
        try {
            return userDAO.getUserByUsername(username);
        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
            return null;
        }
    }

    // Update user profile
    public boolean updateUser(User user) {
        try {
            return userDAO.updateUser(user);
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // Delete user account (soft delete)
    public boolean deleteAccount(int userId, String password) {
        try {
            User user = userDAO.getUserById(userId);
            if (user == null) {
                System.out.println("User not found.");
                return false;
            }

            // Verify password
            boolean passwordMatches = passwordHasher.verifyPassword(password, user.getPasswordHash());
            if (!passwordMatches) {
                System.out.println("Incorrect password.");
                return false;
            }

            // Soft delete the user
            return userDAO.deleteUser(userId);

        } catch (SQLException e) {
            System.out.println("Error deleting account: " + e.getMessage());
            return false;
        }
    }

    // Search users
    public List<User> searchUsers(String query) {
        try {
            List<User> users = userDAO.searchUsers(query);

            if (users.isEmpty()) {
                System.out.println("No users found matching: " + query);
            } else {
                System.out.println("\nFound " + users.size() + " user(s):");
                System.out.println("──────────────────────────────────────");

                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    System.out.println((i + 1) + ". " + user.getFirstName() + " " +
                            user.getLastName() + " (@" + user.getUsername() + ")");
                    System.out.println("   Type: " + user.getUserType());
                    System.out.println("   Followers: " + user.getFollowersCount());

                    if (user.getBio() != null && !user.getBio().isEmpty()) {
                        String shortBio = user.getBio().length() > 50 ?
                                user.getBio().substring(0, 50) + "..." : user.getBio();
                        System.out.println("   Bio: " + shortBio);
                    }
                    System.out.println("──────────────────────────────────────");
                }
            }

            return users;

        } catch (SQLException e) {
            System.out.println("Error searching users: " + e.getMessage());
            return null;
        }
    }

    // Follow a user
    public boolean followUser(int followerId, int followedId) {
        try {
            // Check if already following
            if (followDAO.isFollowing(followerId, followedId)) {
                System.out.println("You are already following this user.");
                return false;
            }

            // Check if trying to follow yourself
            if (followerId == followedId) {
                System.out.println("You cannot follow yourself.");
                return false;
            }

            // Follow the user
            boolean success = followDAO.followUser(followerId, followedId);

            if (success) {
                // Update follower/following counts
                userDAO.updateFollowerCount(followedId, 1);
                userDAO.updateFollowingCount(followerId, 1);
                System.out.println("You are now following this user.");
            }

            return success;

        } catch (SQLException e) {
            System.out.println("Error following user: " + e.getMessage());
            return false;
        }
    }

    // Unfollow a user
    public boolean unfollowUser(int followerId, int followedId) {
        try {
            // Check if actually following
            if (!followDAO.isFollowing(followerId, followedId)) {
                System.out.println("You are not following this user.");
                return false;
            }

            // Unfollow the user
            boolean success = followDAO.unfollowUser(followerId, followedId);

            if (success) {
                // Update follower/following counts
                userDAO.updateFollowerCount(followedId, -1);
                userDAO.updateFollowingCount(followerId, -1);
                System.out.println("You have unfollowed this user.");
            }

            return success;

        } catch (SQLException e) {
            System.out.println("Error unfollowing user: " + e.getMessage());
            return false;
        }
    }

    // Get followers list
    public List<Follow> getFollowers(int userId) {
        try {
            return followDAO.getFollowers(userId);
        } catch (SQLException e) {
            System.out.println("Error getting followers: " + e.getMessage());
            return null;
        }
    }

    // Get following list
    public List<Follow> getFollowing(int userId) {
        try {
            return followDAO.getFollowing(userId);
        } catch (SQLException e) {
            System.out.println("Error getting following: " + e.getMessage());
            return null;
        }
    }

    // Display followers
    public void displayFollowers(int userId) {
        try {
            List<Follow> followers = followDAO.getFollowers(userId);
            User user = userDAO.getUserById(userId);

            if (followers.isEmpty()) {
                System.out.println("\n" + user.getFirstName() + " has no followers yet.");
            } else {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("      FOLLOWERS (" + followers.size() + ")       ");
                System.out.println("══════════════════════════════════════");

                for (int i = 0; i < followers.size(); i++) {
                    Follow follow = followers.get(i);
                    User follower = userDAO.getUserById(follow.getFollowerId());

                    System.out.println((i + 1) + ". " + follower.getFirstName() + " " +
                            follower.getLastName() + " (@" + follower.getUsername() + ")");
                    System.out.println("   Since: " + follow.getCreatedAt().toLocalDate());
                    System.out.println("──────────────────────────────────────");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error displaying followers: " + e.getMessage());
        }
    }

    // Display following
    public void displayFollowing(int userId) {
        try {
            List<Follow> following = followDAO.getFollowing(userId);
            User user = userDAO.getUserById(userId);

            if (following.isEmpty()) {
                System.out.println("\n" + user.getFirstName() + " is not following anyone yet.");
            } else {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("      FOLLOWING (" + following.size() + ")       ");
                System.out.println("══════════════════════════════════════");

                for (int i = 0; i < following.size(); i++) {
                    Follow follow = following.get(i);
                    User followedUser = userDAO.getUserById(follow.getFollowedId());

                    System.out.println((i + 1) + ". " + followedUser.getFirstName() + " " +
                            followedUser.getLastName() + " (@" + followedUser.getUsername() + ")");
                    System.out.println("   Since: " + follow.getCreatedAt().toLocalDate());
                    System.out.println("──────────────────────────────────────");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error displaying following: " + e.getMessage());
        }
    }

    // Display user posts
    public void displayUserPosts(int userId, int limit, int offset) {
        try {
            List<Post> posts = postDAO.getPostsByUser(userId, limit, offset);
            User user = userDAO.getUserById(userId);

            if (posts.isEmpty()) {
                System.out.println("\n" + user.getFirstName() + " hasn't posted anything yet.");
            } else {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("     " + user.getFirstName() + "'S POSTS (" + posts.size() + ")     ");
                System.out.println("══════════════════════════════════════");

                for (int i = 0; i < posts.size(); i++) {
                    Post post = posts.get(i);

                    System.out.println("\n──────────────────────────────────────");
                    System.out.println("POST #" + (i + 1));
                    System.out.println("──────────────────────────────────────");

                    System.out.println(post.getContent());

                    if (post.getMediaUrl() != null) {
                        System.out.println("[Media: " + post.getMediaUrl() + "]");
                    }

                    System.out.println("\n❤️ " + post.getLikeCount() + "   💬 " + post.getCommentCount() +
                            "   🔄 " + post.getShareCount());
                    System.out.println("Type: " + post.getPostType() + " | Visibility: " + post.getVisibility());
                    System.out.println("Posted: " + post.getCreatedAt().toLocalDate());
                    System.out.println("──────────────────────────────────────");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error displaying user posts: " + e.getMessage());
        }
    }

    // Get follower count
    public int getFollowerCount(int userId) {
        try {
            return followDAO.getFollowerCount(userId);
        } catch (SQLException e) {
            System.out.println("Error getting follower count: " + e.getMessage());
            return 0;
        }
    }

    // Get following count
    public int getFollowingCount(int userId) {
        try {
            return followDAO.getFollowingCount(userId);
        } catch (SQLException e) {
            System.out.println("Error getting following count: " + e.getMessage());
            return 0;
        }
    }

    // Check if user is following another user
    public boolean isFollowing(int followerId, int followedId) {
        try {
            return followDAO.isFollowing(followerId, followedId);
        } catch (SQLException e) {
            System.out.println("Error checking follow status: " + e.getMessage());
            return false;
        }
    }

    // Update email
    public boolean updateEmail(int userId, String newEmail, String password) {
        try {
            User user = userDAO.getUserById(userId);
            if (user == null) {
                System.out.println("User not found.");
                return false;
            }

            // Verify password
            boolean passwordMatches = passwordHasher.verifyPassword(password, user.getPasswordHash());
            if (!passwordMatches) {
                System.out.println("Incorrect password.");
                return false;
            }

            // Check if email already exists
            User existingUser = userDAO.getUserByEmail(newEmail);
            if (existingUser != null && existingUser.getUserId() != userId) {
                System.out.println("Email already in use by another account.");
                return false;
            }

            // Update email
            user.setEmail(newEmail);
            return userDAO.updateUser(user);

        } catch (SQLException e) {
            System.out.println("Error updating email: " + e.getMessage());
            return false;
        }
    }

    // Change password (alternative method)
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        // You'll need to import AuthenticationService
        // AuthenticationService authService = new AuthenticationService();
        // return authService.changePassword(userId, currentPassword, newPassword);
        return false; // Placeholder
    }

    // Get suggested users to follow
    public List<Integer> getSuggestedUsersToFollow(int userId, int limit) {
        try {
            return followDAO.getSuggestedUsersToFollow(userId, limit);
        } catch (SQLException e) {
            System.out.println("Error getting suggested users: " + e.getMessage());
            return null;
        }
    }

    // NEW: Simple method for testing
    public String testMethod() {
        return "Test method works!";
    }
}