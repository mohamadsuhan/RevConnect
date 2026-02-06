package com.revconnect.services;

import com.revconnect.dao.PostDAO;
import com.revconnect.dao.CommentDAO;
import com.revconnect.dao.LikeDAO;
import com.revconnect.dao.UserDAO;
import com.revconnect.dao.FeedDAO;
import com.revconnect.models.Post;
import com.revconnect.models.Comment;
import com.revconnect.models.Like;
import com.revconnect.models.User;

import java.sql.SQLException;
import java.util.List;

public class PostService {
    private PostDAO postDAO;
    private CommentDAO commentDAO;
    private LikeDAO likeDAO;
    private UserDAO userDAO;
    private FeedDAO feedDAO;

    public PostService() {
        this.postDAO = new PostDAO();
        this.commentDAO = new CommentDAO();
        this.likeDAO = new LikeDAO();
        this.userDAO = new UserDAO();
        this.feedDAO = new FeedDAO();
    }

    // Create a new post
    public int createPost(Post post) {
        try {
            return postDAO.createPost(post);
        } catch (SQLException e) {
            System.out.println("Error creating post: " + e.getMessage());
            return -1;
        }
    }

    // Get post by ID
    public Post getPostById(int postId) {
        try {
            return postDAO.getPostById(postId);
        } catch (SQLException e) {
            System.out.println("Error retrieving post: " + e.getMessage());
            return null;
        }
    }

    // Update post
    public boolean updatePost(Post post) {
        try {
            return postDAO.updatePost(post);
        } catch (SQLException e) {
            System.out.println("Error updating post: " + e.getMessage());
            return false;
        }
    }

    // Delete post
    public boolean deletePost(int postId, int userId) {
        try {
            // Verify ownership
            Post post = postDAO.getPostById(postId);
            if (post == null) {
                System.out.println("Post not found.");
                return false;
            }

            if (post.getUserId() != userId) {
                System.out.println("You can only delete your own posts.");
                return false;
            }

            return postDAO.deletePost(postId);

        } catch (SQLException e) {
            System.out.println("Error deleting post: " + e.getMessage());
            return false;
        }
    }

    // Like a post
    public boolean likePost(int userId, int postId) {
        try {
            // Check if already liked
            if (likeDAO.hasUserLikedPost(userId, postId)) {
                // Unlike the post
                boolean unliked = likeDAO.removePostLike(userId, postId);
                if (unliked) {
                    postDAO.updateLikeCount(postId, -1);
                }
                return false; // Return false for unliked
            } else {
                // Like the post
                boolean liked = likeDAO.addPostLike(userId, postId);
                if (liked) {
                    postDAO.updateLikeCount(postId, 1);
                }
                return true; // Return true for liked
            }
        } catch (SQLException e) {
            System.out.println("Error liking post: " + e.getMessage());
            return false;
        }
    }

    // Like a comment
    public boolean likeComment(int userId, int commentId) {
        try {
            // Check if already liked
            if (likeDAO.hasUserLikedComment(userId, commentId)) {
                // Unlike the comment
                boolean unliked = likeDAO.removeCommentLike(userId, commentId);
                if (unliked) {
                    commentDAO.updateLikeCount(commentId, -1);
                }
                return false; // Return false for unliked
            } else {
                // Like the comment
                boolean liked = likeDAO.addCommentLike(userId, commentId);
                if (liked) {
                    commentDAO.updateLikeCount(commentId, 1);
                }
                return true; // Return true for liked
            }
        } catch (SQLException e) {
            System.out.println("Error liking comment: " + e.getMessage());
            return false;
        }
    }

    // Add comment to post
    public int addComment(Comment comment) {
        try {
            int commentId = commentDAO.createComment(comment);
            if (commentId > 0) {
                // Update comment count on post
                postDAO.updateCommentCount(comment.getPostId(), 1);
            }
            return commentId;
        } catch (SQLException e) {
            System.out.println("Error adding comment: " + e.getMessage());
            return -1;
        }
    }

    // Get comments for a post
    public List<Comment> getCommentsByPost(int postId, boolean includeReplies) {
        try {
            return commentDAO.getCommentsByPost(postId, includeReplies);
        } catch (SQLException e) {
            System.out.println("Error retrieving comments: " + e.getMessage());
            return null;
        }
    }

    // Get replies to a comment
    public List<Comment> getCommentReplies(int parentCommentId) {
        try {
            return commentDAO.getCommentReplies(parentCommentId);
        } catch (SQLException e) {
            System.out.println("Error retrieving comment replies: " + e.getMessage());
            return null;
        }
    }

    // Update comment
    public boolean updateComment(Comment comment, int userId) {
        try {
            // Verify ownership
            Comment existingComment = commentDAO.getCommentById(comment.getCommentId());
            if (existingComment == null) {
                System.out.println("Comment not found.");
                return false;
            }

            if (existingComment.getUserId() != userId) {
                System.out.println("You can only edit your own comments.");
                return false;
            }

            return commentDAO.updateComment(comment);

        } catch (SQLException e) {
            System.out.println("Error updating comment: " + e.getMessage());
            return false;
        }
    }

    // Delete comment
    public boolean deleteComment(int commentId, int userId) {
        try {
            // Verify ownership
            Comment comment = commentDAO.getCommentById(commentId);
            if (comment == null) {
                System.out.println("Comment not found.");
                return false;
            }

            if (comment.getUserId() != userId) {
                System.out.println("You can only delete your own comments.");
                return false;
            }

            boolean deleted = commentDAO.deleteComment(commentId);
            if (deleted) {
                // Update comment count on post
                postDAO.updateCommentCount(comment.getPostId(), -1);
            }
            return deleted;

        } catch (SQLException e) {
            System.out.println("Error deleting comment: " + e.getMessage());
            return false;
        }
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

    // Display public feed
    public void displayPublicFeed(int limit, int offset) {
        try {
            List<Post> feed = feedDAO.getPublicFeed(limit, offset);

            if (feed.isEmpty()) {
                System.out.println("No posts available.");
            } else {
                System.out.println("══════════════════════════════════════");
                System.out.println("          PUBLIC FEED (" + feed.size() + ")        ");
                System.out.println("══════════════════════════════════════");

                for (int i = 0; i < feed.size(); i++) {
                    Post post = feed.get(i);
                    User user = userDAO.getUserById(post.getUserId());

                    System.out.println("\n──────────────────────────────────────");
                    System.out.println("POST #" + (i + 1));
                    if (user != null) {
                        System.out.println("By: " + user.getFirstName() + " " +
                                user.getLastName() + " (@" + user.getUsername() + ")");
                    }
                    System.out.println("──────────────────────────────────────");

                    System.out.println(post.getContent());

                    if (post.getMediaUrl() != null) {
                        System.out.println("[Media: " + post.getMediaUrl() + "]");
                    }

                    System.out.println("\n❤️ " + post.getLikeCount() + "   💬 " + post.getCommentCount() +
                            "   🔄 " + post.getShareCount());
                    System.out.println("Type: " + post.getPostType());
                    System.out.println("Posted: " + post.getCreatedAt().toLocalDate());
                    System.out.println("──────────────────────────────────────");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error displaying public feed: " + e.getMessage());
        }
    }

    // Get trending posts
    public List<Post> getTrendingPosts(int limit) {
        try {
            return postDAO.getTrendingPosts(limit);
        } catch (SQLException e) {
            System.out.println("Error retrieving trending posts: " + e.getMessage());
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

    // Search posts
    public List<Post> searchPosts(String query) {
        try {
            List<Post> posts = postDAO.searchPosts(query);

            if (posts.isEmpty()) {
                System.out.println("No posts found matching: " + query);
            } else {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("     SEARCH RESULTS (" + posts.size() + ")      ");
                System.out.println("══════════════════════════════════════");

                for (int i = 0; i < posts.size(); i++) {
                    Post post = posts.get(i);
                    User user = userDAO.getUserById(post.getUserId());

                    System.out.println("\n──────────────────────────────────────");
                    System.out.println("RESULT #" + (i + 1));
                    if (user != null) {
                        System.out.println("By: " + user.getFirstName() + " " +
                                user.getLastName() + " (@" + user.getUsername() + ")");
                    }
                    System.out.println("──────────────────────────────────────");

                    String content = post.getContent();
                    // Highlight search term
                    if (content.toLowerCase().contains(query.toLowerCase())) {
                        int start = content.toLowerCase().indexOf(query.toLowerCase());
                        int end = start + query.length();
                        System.out.println(content.substring(0, start) +
                                ">>>" + content.substring(start, end) + "<<<" +
                                content.substring(end));
                    } else {
                        System.out.println(content);
                    }

                    System.out.println("\n❤️ " + post.getLikeCount() + "   💬 " + post.getCommentCount());
                    System.out.println("Posted: " + post.getCreatedAt().toLocalDate());
                    System.out.println("──────────────────────────────────────");
                }
            }

            return posts;

        } catch (SQLException e) {
            System.out.println("Error searching posts: " + e.getMessage());
            return null;
        }
    }

    // Share post (increment share count)
    public boolean sharePost(int postId) {
        try {
            postDAO.updateShareCount(postId, 1);
            System.out.println("Post shared successfully!");
            return true;
        } catch (SQLException e) {
            System.out.println("Error sharing post: " + e.getMessage());
            return false;
        }
    }

    // Get posts by user
    public List<Post> getPostsByUser(int userId, int limit, int offset) {
        try {
            return postDAO.getPostsByUser(userId, limit, offset);
        } catch (SQLException e) {
            System.out.println("Error retrieving user posts: " + e.getMessage());
            return null;
        }
    }

    // Get like count for post
    public int getLikeCountForPost(int postId) {
        try {
            return likeDAO.getLikeCountForPost(postId);
        } catch (SQLException e) {
            System.out.println("Error getting like count: " + e.getMessage());
            return 0;
        }
    }

    // Get comment count for post
    public int getCommentCountForPost(int postId) {
        try {
            return commentDAO.getCommentCountForPost(postId);
        } catch (SQLException e) {
            System.out.println("Error getting comment count: " + e.getMessage());
            return 0;
        }
    }

    // Check if user liked a post
    public boolean hasUserLikedPost(int userId, int postId) {
        try {
            return likeDAO.hasUserLikedPost(userId, postId);
        } catch (SQLException e) {
            System.out.println("Error checking like status: " + e.getMessage());
            return false;
        }
    }
}