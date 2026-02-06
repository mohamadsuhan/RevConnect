package com.revconnect.services;

import com.revconnect.dao.FeedDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.models.Post;
import com.revconnect.models.User;

import java.sql.SQLException;
import java.util.List;

public class FeedService {
    private FeedDAO feedDAO;
    private UserDAO userDAO;

    public FeedService() {
        this.feedDAO = new FeedDAO();
        this.userDAO = new UserDAO();
    }

    // Get personalized feed
    public List<Post> getPersonalizedFeed(int userId, int limit, int offset) {
        try {
            return feedDAO.getPersonalizedFeed(userId, limit, offset);
        } catch (SQLException e) {
            System.out.println("Error retrieving personalized feed: " + e.getMessage());
            return null;
        }
    }

    // Get public feed
    public List<Post> getPublicFeed(int limit, int offset) {
        try {
            return feedDAO.getPublicFeed(limit, offset);
        } catch (SQLException e) {
            System.out.println("Error retrieving public feed: " + e.getMessage());
            return null;
        }
    }

    // Get feed by category
    public List<Post> getFeedByCategory(String category, int limit, int offset) {
        try {
            return feedDAO.getFeedByCategory(category, limit, offset);
        } catch (SQLException e) {
            System.out.println("Error retrieving category feed: " + e.getMessage());
            return null;
        }
    }

    // Get trending feed
    public List<Post> getTrendingFeed(int limit, int offset) {
        try {
            return feedDAO.getTrendingFeed(limit, offset);
        } catch (SQLException e) {
            System.out.println("Error retrieving trending feed: " + e.getMessage());
            return null;
        }
    }

    // Get network feed
    public List<Post> getNetworkFeed(int userId, int limit, int offset) {
        try {
            return feedDAO.getNetworkFeed(userId, limit, offset);
        } catch (SQLException e) {
            System.out.println("Error retrieving network feed: " + e.getMessage());
            return null;
        }
    }

    // Get media feed
    public List<Post> getMediaFeed(int limit, int offset) {
        try {
            return feedDAO.getMediaFeed(limit, offset);
        } catch (SQLException e) {
            System.out.println("Error retrieving media feed: " + e.getMessage());
            return null;
        }
    }

    // Get recommended posts
    public List<Post> getRecommendedPosts(int userId, int limit) {
        try {
            return feedDAO.getRecommendedPosts(userId, limit);
        } catch (SQLException e) {
            System.out.println("Error retrieving recommended posts: " + e.getMessage());
            return null;
        }
    }

    // Display personalized feed with user info
    public void displayPersonalizedFeed(int userId, int limit, int offset) {
        try {
            List<Post> feed = feedDAO.getPersonalizedFeed(userId, limit, offset);

            if (feed == null || feed.isEmpty()) {
                System.out.println("\nYour feed is empty. Start following people or make connections!");
                System.out.println("Try viewing trending posts or searching for content.");
                return;
            }

            System.out.println("\n══════════════════════════════════════");
            System.out.println("        YOUR FEED (" + feed.size() + ")        ");
            System.out.println("══════════════════════════════════════");

            for (int i = 0; i < feed.size(); i++) {
                Post post = feed.get(i);
                User author = userDAO.getUserById(post.getUserId());

                System.out.println("\n──────────────────────────────────────");
                System.out.println("POST #" + (i + 1));
                if (author != null) {
                    System.out.println("By: " + author.getFirstName() + " " +
                            author.getLastName() + " (@" + author.getUsername() + ")");
                }
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

            System.out.println("\nEnd of feed. Showing " + feed.size() + " posts.");

        } catch (SQLException e) {
            System.out.println("Error displaying feed: " + e.getMessage());
        }
    }

    // Display trending feed
    public void displayTrendingFeed(int limit, int offset) {
        try {
            List<Post> trendingPosts = feedDAO.getTrendingFeed(limit, offset);

            if (trendingPosts == null || trendingPosts.isEmpty()) {
                System.out.println("\nNo trending posts at the moment.");
                return;
            }

            System.out.println("\n══════════════════════════════════════");
            System.out.println("      TRENDING NOW (" + trendingPosts.size() + ")      ");
            System.out.println("══════════════════════════════════════");

            for (int i = 0; i < trendingPosts.size(); i++) {
                Post post = trendingPosts.get(i);
                User author = userDAO.getUserById(post.getUserId());

                System.out.println("\n──────────────────────────────────────");
                System.out.println("TRENDING #" + (i + 1));
                if (author != null) {
                    System.out.println("By: " + author.getFirstName() + " " +
                            author.getLastName() + " (@" + author.getUsername() + ")");
                }
                System.out.println("──────────────────────────────────────");

                System.out.println(post.getContent());

                if (post.getMediaUrl() != null) {
                    System.out.println("[Media: " + post.getMediaUrl() + "]");
                }

                System.out.println("\n❤️ " + post.getLikeCount() + "   💬 " + post.getCommentCount() +
                        "   🔄 " + post.getShareCount());
                System.out.println("Engagement Score: " +
                        (post.getLikeCount() + post.getCommentCount() * 2 + post.getShareCount() * 3));
                System.out.println("Posted: " + post.getCreatedAt().toLocalDate());
                System.out.println("──────────────────────────────────────");
            }

        } catch (SQLException e) {
            System.out.println("Error displaying trending feed: " + e.getMessage());
        }
    }

    // Display category feed
    public void displayCategoryFeed(String category, int limit, int offset) {
        try {
            List<Post> categoryPosts = feedDAO.getFeedByCategory(category, limit, offset);

            if (categoryPosts == null || categoryPosts.isEmpty()) {
                System.out.println("\nNo posts found in category: " + category);
                return;
            }

            System.out.println("\n══════════════════════════════════════");
            System.out.println("   " + category.toUpperCase() + " FEED (" + categoryPosts.size() + ")   ");
            System.out.println("══════════════════════════════════════");

            for (int i = 0; i < categoryPosts.size(); i++) {
                Post post = categoryPosts.get(i);
                User author = userDAO.getUserById(post.getUserId());

                System.out.println("\n──────────────────────────────────────");
                System.out.println("POST #" + (i + 1));
                if (author != null) {
                    System.out.println("By: " + author.getFirstName() + " " +
                            author.getLastName() + " (@" + author.getUsername() + ")");

                    // Show user type for context
                    System.out.println("User Type: " + author.getUserType());
                }
                System.out.println("──────────────────────────────────────");

                System.out.println(post.getContent());

                System.out.println("\n❤️ " + post.getLikeCount() + "   💬 " + post.getCommentCount());
                System.out.println("Posted: " + post.getCreatedAt().toLocalDate());
                System.out.println("──────────────────────────────────────");
            }

        } catch (SQLException e) {
            System.out.println("Error displaying category feed: " + e.getMessage());
        }
    }

    // Refresh feed (could be used for real-time updates)
    public List<Post> refreshFeed(int userId, int lastPostId) {
        try {
            // In a real app, you would get posts newer than lastPostId
            // For now, we'll just return the personalized feed
            return feedDAO.getPersonalizedFeed(userId, 20, 0);
        } catch (SQLException e) {
            System.out.println("Error refreshing feed: " + e.getMessage());
            return null;
        }
    }
}