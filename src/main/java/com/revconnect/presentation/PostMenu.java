package com.revconnect.presentation;

import com.revconnect.services.PostService;
import com.revconnect.models.Post;
import com.revconnect.models.Comment;

import java.util.List;
import java.util.Scanner;

public class PostMenu {
    private Scanner scanner;
    private PostService postService;
    private int currentUserId;

    public PostMenu(Scanner scanner, PostService postService, int currentUserId) {
        this.scanner = scanner;
        this.postService = postService;
        this.currentUserId = currentUserId;
    }

    public void showFeedMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n══════════════════════════════════════");
            System.out.println("                 FEED                 ");
            System.out.println("══════════════════════════════════════");

            System.out.println("1. View Personalized Feed");
            System.out.println("2. View Trending Posts");
            System.out.println("3. View Media Feed");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(1, 4);

            switch (choice) {
                case 1:
                    viewPersonalizedFeed();
                    break;
                case 2:
                    viewTrendingPosts();
                    break;
                case 3:
                    viewMediaFeed();
                    break;
                case 4:
                    back = true;
                    break;
            }
        }
    }

    public void showCreatePostMenu() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("            CREATE POST               ");
        System.out.println("══════════════════════════════════════");

        try {
            System.out.print("Enter your post content: ");
            String content = scanner.nextLine();

            if (content.trim().isEmpty()) {
                System.out.println("Post content cannot be empty!");
                return;
            }

            System.out.println("\nSelect post type:");
            System.out.println("1. Text");
            System.out.println("2. Image (URL)");
            System.out.println("3. Video (URL)");
            System.out.println("4. Link");
            System.out.print("Enter your choice (1-4): ");

            int postTypeChoice = getIntInput(1, 4);
            Post.PostType postType = Post.PostType.TEXT;
            String mediaUrl = null;

            if (postTypeChoice == 2 || postTypeChoice == 3 || postTypeChoice == 4) {
                System.out.print("Enter media URL: ");
                mediaUrl = scanner.nextLine();

                switch (postTypeChoice) {
                    case 2:
                        postType = Post.PostType.IMAGE;
                        break;
                    case 3:
                        postType = Post.PostType.VIDEO;
                        break;
                    case 4:
                        postType = Post.PostType.LINK;
                        break;
                }
            }

            System.out.println("\nSelect visibility:");
            System.out.println("1. Public (Everyone can see)");
            System.out.println("2. Connections Only");
            System.out.println("3. Private (Only you)");
            System.out.print("Enter your choice (1-3): ");

            int visibilityChoice = getIntInput(1, 3);
            Post.Visibility visibility = Post.Visibility.PUBLIC;

            switch (visibilityChoice) {
                case 2:
                    visibility = Post.Visibility.CONNECTIONS;
                    break;
                case 3:
                    visibility = Post.Visibility.PRIVATE;
                    break;
            }

            Post post = new Post(currentUserId, content, postType, visibility);
            post.setMediaUrl(mediaUrl);

            int postId = postService.createPost(post);

            if (postId > 0) {
                System.out.println("\n✓ Post created successfully!");
                System.out.println("Post ID: " + postId);
            } else {
                System.out.println("Failed to create post.");
            }

        } catch (Exception e) {
            System.out.println("Error creating post: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewPersonalizedFeed() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("        PERSONALIZED FEED             ");
        System.out.println("══════════════════════════════════════");

        try {
            List<Post> feed = postService.getPersonalizedFeed(currentUserId, 10, 0);

            if (feed.isEmpty()) {
                System.out.println("Your feed is empty. Start following people or make connections!");
                System.out.println("Try viewing trending posts or searching for content.");
            } else {
                displayPosts(feed, true);
            }
        } catch (Exception e) {
            System.out.println("Error loading feed: " + e.getMessage());
        }
    }

    private void viewTrendingPosts() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("          TRENDING POSTS              ");
        System.out.println("══════════════════════════════════════");

        try {
            List<Post> trendingPosts = postService.getTrendingPosts(10);

            if (trendingPosts.isEmpty()) {
                System.out.println("No trending posts at the moment.");
            } else {
                displayPosts(trendingPosts, false);
            }
        } catch (Exception e) {
            System.out.println("Error loading trending posts: " + e.getMessage());
        }
    }

    private void viewMediaFeed() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("            MEDIA FEED                ");
        System.out.println("══════════════════════════════════════");

        try {
            List<Post> mediaPosts = postService.getMediaFeed(10, 0);

            if (mediaPosts.isEmpty()) {
                System.out.println("No media posts available.");
            } else {
                displayPosts(mediaPosts, false);
            }
        } catch (Exception e) {
            System.out.println("Error loading media feed: " + e.getMessage());
        }
    }

    private void displayPosts(List<Post> posts, boolean interactive) {
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);

            System.out.println("\n──────────────────────────────────────");
            System.out.println("POST #" + (i + 1));
            System.out.println("──────────────────────────────────────");

            // Display post author (you would need UserService here)
            System.out.println("Post ID: " + post.getPostId());

            // Display content
            System.out.println("\n" + post.getContent());

            if (post.getMediaUrl() != null) {
                System.out.println("[Media: " + post.getMediaUrl() + "]");
            }

            // Display stats
            System.out.println("\n❤️ " + post.getLikeCount() + "   💬 " + post.getCommentCount() +
                    "   🔄 " + post.getShareCount());
            System.out.println("Type: " + post.getPostType() + " | Visibility: " + post.getVisibility());
            System.out.println("Posted: " + post.getCreatedAt().toLocalDate());

            if (interactive) {
                System.out.println("\n1. Like  2. Comment  3. View Comments  4. Next Post  5. Back");
                System.out.print("Enter your choice: ");

                int choice = getIntInput(1, 5);

                switch (choice) {
                    case 1:
                        likePost(post.getPostId());
                        break;
                    case 2:
                        addComment(post.getPostId());
                        break;
                    case 3:
                        viewComments(post.getPostId());
                        break;
                    case 4:
                        continue;
                    case 5:
                        return;
                }
            }

            System.out.println("──────────────────────────────────────");
        }

        if (interactive) {
            System.out.println("\nEnd of feed.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
    }

    private void likePost(int postId) {
        try {
            boolean liked = postService.likePost(currentUserId, postId);
            if (liked) {
                System.out.println("Post liked!");
            } else {
                System.out.println("Post unliked!");
            }
        } catch (Exception e) {
            System.out.println("Error liking post: " + e.getMessage());
        }
    }

    private void addComment(int postId) {
        System.out.print("Enter your comment: ");
        String content = scanner.nextLine();

        if (content.trim().isEmpty()) {
            System.out.println("Comment cannot be empty!");
            return;
        }

        try {
            Comment comment = new Comment(postId, currentUserId, content);
            int commentId = postService.addComment(comment);

            if (commentId > 0) {
                System.out.println("Comment added successfully!");
            } else {
                System.out.println("Failed to add comment.");
            }
        } catch (Exception e) {
            System.out.println("Error adding comment: " + e.getMessage());
        }
    }

    private void viewComments(int postId) {
        try {
            List<Comment> comments = postService.getCommentsByPost(postId, false);

            if (comments.isEmpty()) {
                System.out.println("No comments yet. Be the first to comment!");
            } else {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("           COMMENTS (" + comments.size() + ")           ");
                System.out.println("══════════════════════════════════════");

                for (Comment comment : comments) {
                    System.out.println("\n──────────────────────────────────────");
                    System.out.println("Comment ID: " + comment.getCommentId());
                    System.out.println(comment.getContent());
                    System.out.println("❤️ " + comment.getLikeCount() + " likes");
                    System.out.println("Posted: " + comment.getCreatedAt().toLocalDate());
                    System.out.println("──────────────────────────────────────");
                }

                System.out.println("\n1. Add Comment  2. Like a Comment  3. Back");
                System.out.print("Enter your choice: ");

                int choice = getIntInput(1, 3);

                switch (choice) {
                    case 1:
                        addComment(postId);
                        break;
                    case 2:
                        System.out.print("Enter Comment ID to like: ");
                        int commentId = getIntInput(1, Integer.MAX_VALUE);
                        likeComment(commentId);
                        break;
                    case 3:
                        return;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading comments: " + e.getMessage());
        }
    }

    private void likeComment(int commentId) {
        try {
            boolean liked = postService.likeComment(currentUserId, commentId);
            if (liked) {
                System.out.println("Comment liked!");
            } else {
                System.out.println("Comment unliked!");
            }
        } catch (Exception e) {
            System.out.println("Error liking comment: " + e.getMessage());
        }
    }

    private int getIntInput(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input);

                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}