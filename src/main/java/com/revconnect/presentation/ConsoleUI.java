package com.revconnect.presentation;

import com.revconnect.services.AuthenticationService;
import com.revconnect.services.UserService;
import com.revconnect.services.PostService;
import com.revconnect.services.ConnectionService;
import com.revconnect.services.NotificationService;
import com.revconnect.models.User;
import com.revconnect.utils.ConsoleUtils;

import java.util.Scanner;

public class ConsoleUI {
    private Scanner scanner;
    private AuthenticationService authService;
    private UserService userService;
    private PostService postService;
    private ConnectionService connectionService;
    private NotificationService notificationService;
    private User currentUser;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthenticationService();
        this.userService = new UserService();
        this.postService = new PostService();
        this.connectionService = new ConnectionService();
        this.notificationService = new NotificationService();
    }

    public void start() {
        // Show banner ONLY HERE - using ConsoleUtils for consistent formatting
        ConsoleUtils.printBanner();

        boolean exit = false;

        while (!exit) {
            if (currentUser == null) {
                showGuestMenu();
            } else {
                showMainMenu();
            }
        }

        scanner.close();
        System.out.println("Thank you for using RevConnect!");
    }

    private void showGuestMenu() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("              MAIN MENU               ");
        System.out.println("══════════════════════════════════════");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");

        int choice = getIntInput(1, 3);

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.exit(0);
                break;
        }
    }

    private void showMainMenu() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("    Welcome, " + currentUser.getFirstName() + "!");
        System.out.println("══════════════════════════════════════");
        System.out.println("1. View Feed");
        System.out.println("2. Create Post");
        System.out.println("3. View Profile");
        System.out.println("4. Connections");
        System.out.println("5. Notifications");
        System.out.println("6. Search");
        System.out.println("7. Settings");
        System.out.println("8. Logout");
        System.out.print("Enter your choice: ");

        int choice = getIntInput(1, 8);

        switch (choice) {
            case 1:
                viewFeed();
                break;
            case 2:
                createPost();
                break;
            case 3:
                viewProfile();
                break;
            case 4:
                showConnectionsMenu();
                break;
            case 5:
                showNotifications();
                break;
            case 6:
                search();
                break;
            case 7:
                showSettings();
                break;
            case 8:
                logout();
                break;
        }
    }

    private void login() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("               LOGIN                  ");
        System.out.println("══════════════════════════════════════");

        System.out.print("Username or Email: ");
        String usernameOrEmail = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            User user = authService.login(usernameOrEmail, password);
            if (user != null) {
                currentUser = user;
                System.out.println("Login successful! Welcome back, " + user.getFirstName() + "!");

                // Check for unread notifications
                int unreadCount = notificationService.getUnreadNotificationCount(user.getUserId());
                if (unreadCount > 0) {
                    System.out.println("You have " + unreadCount + " unread notification(s).");
                }
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private void register() {
        AuthMenu authMenu = new AuthMenu(scanner, authService);
        User newUser = authMenu.showRegistrationMenu();

        if (newUser != null) {
            currentUser = newUser;
            System.out.println("Registration successful! Welcome to RevConnect, " + newUser.getFirstName() + "!");
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("You have been logged out successfully.");
    }

    private void viewFeed() {
        PostMenu postMenu = new PostMenu(scanner, postService, currentUser.getUserId());
        postMenu.showFeedMenu();
    }

    private void createPost() {
        PostMenu postMenu = new PostMenu(scanner, postService, currentUser.getUserId());
        postMenu.showCreatePostMenu();
    }

    private void viewProfile() {
        UserMenu userMenu = new UserMenu(scanner, userService, currentUser.getUserId());
        userMenu.showProfileMenu();
    }

    private void showConnectionsMenu() {
        UserMenu userMenu = new UserMenu(scanner, userService, currentUser.getUserId());
        userMenu.showConnectionsMenu();
    }

    private void showNotifications() {
        NotificationMenu notificationMenu = new NotificationMenu(scanner, notificationService, currentUser.getUserId());
        notificationMenu.showNotificationsMenu();
    }

    private void search() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("               SEARCH                 ");
        System.out.println("══════════════════════════════════════");

        System.out.print("Search for users or posts: ");
        String query = scanner.nextLine();

        if (query.trim().isEmpty()) {
            System.out.println("Please enter a search term.");
            return;
        }

        System.out.println("\n1. Search Users");
        System.out.println("2. Search Posts");
        System.out.print("Choose search type: ");

        int choice = getIntInput(1, 2);

        try {
            if (choice == 1) {
                userService.searchUsers(query);
            } else {
                postService.searchPosts(query);
            }
        } catch (Exception e) {
            System.out.println("Error during search: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void showSettings() {
        UserMenu userMenu = new UserMenu(scanner, userService, currentUser.getUserId());
        userMenu.showSettingsMenu();
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