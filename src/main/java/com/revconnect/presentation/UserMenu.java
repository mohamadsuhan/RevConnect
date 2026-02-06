package com.revconnect.presentation;

import com.revconnect.services.UserService;
import com.revconnect.services.ConnectionService;
import com.revconnect.models.User;
import com.revconnect.models.Connection;

import java.util.List;
import java.util.Scanner;

public class UserMenu {
    private Scanner scanner;
    private UserService userService;
    private ConnectionService connectionService;
    private int currentUserId;

    public UserMenu(Scanner scanner, UserService userService, int currentUserId) {
        this.scanner = scanner;
        this.userService = userService;
        this.connectionService = new ConnectionService();
        this.currentUserId = currentUserId;
    }

    public void showProfileMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n══════════════════════════════════════");
            System.out.println("               PROFILE                ");
            System.out.println("══════════════════════════════════════");

            try {
                User user = userService.getUserById(currentUserId);
                displayUserProfile(user);

                System.out.println("\n1. Edit Profile");
                System.out.println("2. View My Posts");
                System.out.println("3. View Followers");
                System.out.println("4. View Following");
                System.out.println("5. Back to Main Menu");
                System.out.print("Enter your choice: ");

                int choice = getIntInput(1, 5);

                switch (choice) {
                    case 1:
                        editProfile(user);
                        break;
                    case 2:
                        viewMyPosts();
                        break;
                    case 3:
                        viewFollowers();
                        break;
                    case 4:
                        viewFollowing();
                        break;
                    case 5:
                        back = true;
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error loading profile: " + e.getMessage());
                back = true;
            }
        }
    }

    public void showConnectionsMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n══════════════════════════════════════");
            System.out.println("             CONNECTIONS              ");
            System.out.println("══════════════════════════════════════");

            System.out.println("1. View My Connections");
            System.out.println("2. View Pending Requests");
            System.out.println("3. Search Users to Connect");
            System.out.println("4. Suggested Connections");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    viewConnections();
                    break;
                case 2:
                    viewPendingRequests();
                    break;
                case 3:
                    searchUsersToConnect();
                    break;
                case 4:
                    viewSuggestedConnections();
                    break;
                case 5:
                    back = true;
                    break;
            }
        }
    }

    public void showSettingsMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n══════════════════════════════════════");
            System.out.println("               SETTINGS               ");
            System.out.println("══════════════════════════════════════");

            System.out.println("1. Change Password");
            System.out.println("2. Update Email");
            System.out.println("3. Privacy Settings");
            System.out.println("4. Delete Account");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    changePassword();
                    break;
                case 2:
                    updateEmail();
                    break;
                case 3:
                    showPrivacySettings();
                    break;
                case 4:
                    deleteAccount();
                    back = true;
                    break;
                case 5:
                    back = true;
                    break;
            }
        }
    }

    private void displayUserProfile(User user) {
        System.out.println("\n──────────────────────────────────────");
        System.out.println("Name: " + user.getFirstName() + " " + user.getLastName());
        System.out.println("Username: @" + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Account Type: " + user.getUserType());

        if (user.getBio() != null && !user.getBio().isEmpty()) {
            System.out.println("Bio: " + user.getBio());
        }

        System.out.println("Followers: " + user.getFollowersCount());
        System.out.println("Following: " + user.getFollowingCount());

        // Display type-specific information
        switch (user.getUserType()) {
            case BUSINESS:
                com.revconnect.models.BusinessUser businessUser = (com.revconnect.models.BusinessUser) user;
                System.out.println("Business: " + businessUser.getBusinessName());
                if (businessUser.getIndustry() != null) {
                    System.out.println("Industry: " + businessUser.getIndustry());
                }
                break;
            case CREATOR:
                com.revconnect.models.CreatorUser creatorUser = (com.revconnect.models.CreatorUser) user;
                System.out.println("Category: " + creatorUser.getCreatorCategory());
                if (creatorUser.getPlatforms() != null) {
                    System.out.println("Platforms: " + creatorUser.getPlatforms());
                }
                break;
        }

        System.out.println("Member since: " + user.getCreatedAt().toLocalDate());
        System.out.println("──────────────────────────────────────");
    }

    private void editProfile(User user) {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("            EDIT PROFILE              ");
        System.out.println("══════════════════════════════════════");

        try {
            System.out.print("New Bio (press Enter to keep current): ");
            String newBio = scanner.nextLine();
            if (!newBio.isEmpty()) {
                user.setBio(newBio);
            }

            System.out.print("New Website URL (press Enter to keep current): ");
            String newWebsite = scanner.nextLine();
            if (!newWebsite.isEmpty()) {
                user.setWebsiteUrl(newWebsite);
            }

            // Type-specific fields
            switch (user.getUserType()) {
                case PERSONAL:
                    com.revconnect.models.PersonalUser personalUser = (com.revconnect.models.PersonalUser) user;
                    System.out.print("New Location (press Enter to keep current): ");
                    String newLocation = scanner.nextLine();
                    if (!newLocation.isEmpty()) {
                        personalUser.setLocation(newLocation);
                    }

                    System.out.print("New Occupation (press Enter to keep current): ");
                    String newOccupation = scanner.nextLine();
                    if (!newOccupation.isEmpty()) {
                        personalUser.setOccupation(newOccupation);
                    }
                    break;

                case BUSINESS:
                    com.revconnect.models.BusinessUser businessUser = (com.revconnect.models.BusinessUser) user;
                    System.out.print("New Industry (press Enter to keep current): ");
                    String newIndustry = scanner.nextLine();
                    if (!newIndustry.isEmpty()) {
                        businessUser.setIndustry(newIndustry);
                    }

                    System.out.print("New Company Size (press Enter to keep current): ");
                    String newCompanySize = scanner.nextLine();
                    if (!newCompanySize.isEmpty()) {
                        businessUser.setCompanySize(newCompanySize);
                    }
                    break;

                case CREATOR:
                    com.revconnect.models.CreatorUser creatorUser = (com.revconnect.models.CreatorUser) user;
                    System.out.print("New Niche (press Enter to keep current): ");
                    String newNiche = scanner.nextLine();
                    if (!newNiche.isEmpty()) {
                        creatorUser.setNiche(newNiche);
                    }

                    System.out.print("New Platforms (press Enter to keep current): ");
                    String newPlatforms = scanner.nextLine();
                    if (!newPlatforms.isEmpty()) {
                        creatorUser.setPlatforms(newPlatforms);
                    }
                    break;
            }

            boolean success = userService.updateUser(user);
            if (success) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Failed to update profile.");
            }

        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }

    private void viewMyPosts() {
        try {
            userService.displayUserPosts(currentUserId, 10, 0);
        } catch (Exception e) {
            System.out.println("Error loading posts: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewFollowers() {
        try {
            userService.displayFollowers(currentUserId);
        } catch (Exception e) {
            System.out.println("Error loading followers: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewFollowing() {
        try {
            userService.displayFollowing(currentUserId);
        } catch (Exception e) {
            System.out.println("Error loading following: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewConnections() {
        try {
            List<Connection> connections = connectionService.getAcceptedConnections(currentUserId);

            if (connections.isEmpty()) {
                System.out.println("\nYou don't have any connections yet.");
                System.out.println("Try searching for users or accepting pending requests.");
            } else {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("          YOUR CONNECTIONS           ");
                System.out.println("══════════════════════════════════════");

                for (int i = 0; i < connections.size(); i++) {
                    Connection conn = connections.get(i);
                    int otherUserId = (conn.getUserId1() == currentUserId) ? conn.getUserId2() : conn.getUserId1();
                    User otherUser = userService.getUserById(otherUserId);

                    System.out.println((i + 1) + ". " + otherUser.getFirstName() + " " +
                            otherUser.getLastName() + " (@" + otherUser.getUsername() + ")");
                }

                System.out.println("\nTotal connections: " + connections.size());
            }
        } catch (Exception e) {
            System.out.println("Error loading connections: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewPendingRequests() {
        try {
            List<Connection> pendingRequests = connectionService.getPendingRequests(currentUserId);

            if (pendingRequests.isEmpty()) {
                System.out.println("\nNo pending connection requests.");
            } else {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("       PENDING REQUESTS (" + pendingRequests.size() + ")      ");
                System.out.println("══════════════════════════════════════");

                for (int i = 0; i < pendingRequests.size(); i++) {
                    Connection conn = pendingRequests.get(i);
                    int senderId = (conn.getUserId1() == currentUserId) ? conn.getUserId2() : conn.getUserId1();
                    User sender = userService.getUserById(senderId);

                    System.out.println((i + 1) + ". Request from: " + sender.getFirstName() + " " +
                            sender.getLastName() + " (@" + sender.getUsername() + ")");
                    System.out.println("   Connection ID: " + conn.getConnectionId());
                    System.out.println("   Sent on: " + conn.getCreatedAt().toLocalDate());
                    System.out.println("──────────────────────────────────────");
                }

                System.out.println("\nEnter connection ID to respond (or 0 to go back): ");
                int connectionId = getIntInput(0, Integer.MAX_VALUE);

                if (connectionId > 0) {
                    System.out.println("1. Accept");
                    System.out.println("2. Reject");
                    System.out.print("Choose action: ");

                    int action = getIntInput(1, 2);

                    if (action == 1) {
                        boolean accepted = connectionService.acceptConnectionRequest(connectionId);
                        if (accepted) {
                            System.out.println("Connection request accepted!");
                        } else {
                            System.out.println("Failed to accept request.");
                        }
                    } else {
                        boolean rejected = connectionService.rejectConnectionRequest(connectionId);
                        if (rejected) {
                            System.out.println("Connection request rejected.");
                        } else {
                            System.out.println("Failed to reject request.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading pending requests: " + e.getMessage());
        }
    }

    private void searchUsersToConnect() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("         SEARCH USERS TO CONNECT      ");
        System.out.println("══════════════════════════════════════");

        System.out.print("Enter name or username to search: ");
        String query = scanner.nextLine();

        if (query.trim().isEmpty()) {
            System.out.println("Please enter a search term.");
            return;
        }

        try {
            List<User> users = userService.searchUsers(query);

            if (users.isEmpty()) {
                System.out.println("No users found matching your search.");
            } else {
                System.out.println("\nFound " + users.size() + " user(s):");
                System.out.println("──────────────────────────────────────");

                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    System.out.println((i + 1) + ". " + user.getFirstName() + " " +
                            user.getLastName() + " (@" + user.getUsername() + ")");
                    System.out.println("   Type: " + user.getUserType());
                    System.out.println("   Followers: " + user.getFollowersCount());

                    // Check if already connected
                    boolean isConnected = connectionService.areUsersConnected(currentUserId, user.getUserId());
                    if (isConnected) {
                        System.out.println("   Status: Already Connected");
                    } else {
                        System.out.println("   Status: Not Connected");
                    }
                    System.out.println("──────────────────────────────────────");
                }

                System.out.println("\nEnter user number to send connection request (or 0 to cancel): ");
                int userChoice = getIntInput(0, users.size());

                if (userChoice > 0) {
                    User selectedUser = users.get(userChoice - 1);

                    if (connectionService.areUsersConnected(currentUserId, selectedUser.getUserId())) {
                        System.out.println("You are already connected with this user.");
                    } else {
                        boolean sent = connectionService.sendConnectionRequest(currentUserId, selectedUser.getUserId());
                        if (sent) {
                            System.out.println("Connection request sent to " + selectedUser.getFirstName() + "!");
                        } else {
                            System.out.println("Failed to send connection request.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error searching users: " + e.getMessage());
        }
    }

    private void viewSuggestedConnections() {
        try {
            List<User> suggestions = connectionService.getSuggestedConnections(currentUserId, 10);

            if (suggestions.isEmpty()) {
                System.out.println("\nNo connection suggestions at this time.");
            } else {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("       SUGGESTED CONNECTIONS         ");
                System.out.println("══════════════════════════════════════");

                for (int i = 0; i < suggestions.size(); i++) {
                    User user = suggestions.get(i);
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

                System.out.println("\nEnter user number to send connection request (or 0 to cancel): ");
                int userChoice = getIntInput(0, suggestions.size());

                if (userChoice > 0) {
                    User selectedUser = suggestions.get(userChoice - 1);
                    boolean sent = connectionService.sendConnectionRequest(currentUserId, selectedUser.getUserId());
                    if (sent) {
                        System.out.println("Connection request sent to " + selectedUser.getFirstName() + "!");
                    } else {
                        System.out.println("Failed to send connection request.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading suggestions: " + e.getMessage());
        }
    }

    private void changePassword() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("          CHANGE PASSWORD             ");
        System.out.println("══════════════════════════════════════");

        try {
            System.out.print("Current Password: ");
            String currentPassword = scanner.nextLine();

            System.out.print("New Password: ");
            String newPassword = scanner.nextLine();

            System.out.print("Confirm New Password: ");
            String confirmPassword = scanner.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("New passwords do not match!");
                return;
            }

            boolean changed = userService.changePassword(currentUserId, currentPassword, newPassword);
            if (changed) {
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("Failed to change password. Please check your current password.");
            }
        } catch (Exception e) {
            System.out.println("Error changing password: " + e.getMessage());
        }
    }

    private void updateEmail() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("            UPDATE EMAIL              ");
        System.out.println("══════════════════════════════════════");

        try {
            System.out.print("New Email: ");
            String newEmail = scanner.nextLine();

            System.out.print("Confirm Password: ");
            String password = scanner.nextLine();

            boolean updated = userService.updateEmail(currentUserId, newEmail, password);
            if (updated) {
                System.out.println("Email updated successfully!");
            } else {
                System.out.println("Failed to update email. Please check your password.");
            }
        } catch (Exception e) {
            System.out.println("Error updating email: " + e.getMessage());
        }
    }

    private void showPrivacySettings() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("          PRIVACY SETTINGS            ");
        System.out.println("══════════════════════════════════════");

        System.out.println("Privacy settings feature coming soon!");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void deleteAccount() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("           DELETE ACCOUNT             ");
        System.out.println("══════════════════════════════════════");

        System.out.println("WARNING: This action cannot be undone!");
        System.out.println("All your data will be permanently deleted.");

        System.out.print("\nType 'DELETE' to confirm: ");
        String confirmation = scanner.nextLine();

        if (!confirmation.equals("DELETE")) {
            System.out.println("Account deletion cancelled.");
            return;
        }

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try {
            boolean deleted = userService.deleteAccount(currentUserId, password);
            if (deleted) {
                System.out.println("Account deleted successfully.");
                System.out.println("Thank you for using RevConnect!");
                System.exit(0);
            } else {
                System.out.println("Failed to delete account. Please check your password.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting account: " + e.getMessage());
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