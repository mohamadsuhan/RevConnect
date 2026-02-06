package com.revconnect.presentation;

import com.revconnect.services.NotificationService;
import com.revconnect.models.Notification;

import java.util.List;
import java.util.Scanner;

public class NotificationMenu {
    private Scanner scanner;
    private NotificationService notificationService;
    private int currentUserId;

    public NotificationMenu(Scanner scanner, NotificationService notificationService, int currentUserId) {
        this.scanner = scanner;
        this.notificationService = notificationService;
        this.currentUserId = currentUserId;
    }

    public void showNotificationsMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n══════════════════════════════════════");
            System.out.println("           NOTIFICATIONS              ");
            System.out.println("══════════════════════════════════════");

            try {
                int unreadCount = notificationService.getUnreadNotificationCount(currentUserId);
                System.out.println("You have " + unreadCount + " unread notification(s)");

                System.out.println("\n1. View All Notifications");
                System.out.println("2. View Unread Notifications");
                System.out.println("3. Mark All as Read");
                System.out.println("4. Clear Read Notifications");
                System.out.println("5. Back to Main Menu");
                System.out.print("Enter your choice: ");

                int choice = getIntInput(1, 5);

                switch (choice) {
                    case 1:
                        viewAllNotifications();
                        break;
                    case 2:
                        viewUnreadNotifications();
                        break;
                    case 3:
                        markAllAsRead();
                        break;
                    case 4:
                        clearReadNotifications();
                        break;
                    case 5:
                        back = true;
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error loading notifications: " + e.getMessage());
                back = true;
            }
        }
    }

    private void viewAllNotifications() {
        try {
            List<Notification> notifications = notificationService.getNotificationsForUser(currentUserId, 20, 0);

            if (notifications.isEmpty()) {
                System.out.println("\nNo notifications yet.");
            } else {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("       ALL NOTIFICATIONS (" + notifications.size() + ")      ");
                System.out.println("══════════════════════════════════════");

                displayNotifications(notifications);
            }
        } catch (Exception e) {
            System.out.println("Error loading notifications: " + e.getMessage());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewUnreadNotifications() {
        try {
            List<Notification> unreadNotifications = notificationService.getUnreadNotificationsForUser(currentUserId);

            if (unreadNotifications.isEmpty()) {
                System.out.println("\nNo unread notifications.");
            } else {
                System.out.println("\n══════════════════════════════════════");
                System.out.println("   UNREAD NOTIFICATIONS (" + unreadNotifications.size() + ")   ");
                System.out.println("══════════════════════════════════════");

                displayNotifications(unreadNotifications);

                System.out.println("\n1. Mark All as Read");
                System.out.println("2. Mark Individual as Read");
                System.out.println("3. Back");
                System.out.print("Enter your choice: ");

                int choice = getIntInput(1, 3);

                switch (choice) {
                    case 1:
                        markAllAsRead();
                        break;
                    case 2:
                        markIndividualAsRead();
                        break;
                    case 3:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading unread notifications: " + e.getMessage());
        }
    }

    private void markAllAsRead() {
        try {
            boolean marked = notificationService.markAllAsRead(currentUserId);
            if (marked) {
                System.out.println("All notifications marked as read.");
            } else {
                System.out.println("No notifications to mark as read.");
            }
        } catch (Exception e) {
            System.out.println("Error marking notifications as read: " + e.getMessage());
        }
    }

    private void clearReadNotifications() {
        System.out.println("\nAre you sure you want to clear all read notifications?");
        System.out.print("Type 'CLEAR' to confirm: ");
        String confirmation = scanner.nextLine();

        if (!confirmation.equals("CLEAR")) {
            System.out.println("Operation cancelled.");
            return;
        }

        try {
            boolean cleared = notificationService.deleteAllReadNotifications(currentUserId);
            if (cleared) {
                System.out.println("Read notifications cleared.");
            } else {
                System.out.println("No read notifications to clear.");
            }
        } catch (Exception e) {
            System.out.println("Error clearing notifications: " + e.getMessage());
        }
    }

    private void displayNotifications(List<Notification> notifications) {
        for (int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);

            String readStatus = notification.isRead() ? "[READ]" : "[NEW]";
            String typeIcon = getNotificationIcon(notification.getType());

            System.out.println("\n" + readStatus + " " + typeIcon + " " + (i + 1) + ". " + notification.getContent());
            System.out.println("   Type: " + notification.getType());
            System.out.println("   Time: " + notification.getCreatedAt().toLocalDate() +
                    " " + notification.getCreatedAt().toLocalTime());

            if (notification.getReferenceId() != null) {
                System.out.println("   Reference ID: " + notification.getReferenceId());
            }

            if (i < notifications.size() - 1) {
                System.out.println("   ──────────────────────────────────────");
            }
        }
    }

    private void markIndividualAsRead() {
        System.out.print("\nEnter notification number to mark as read (or 0 to cancel): ");
        int notificationNumber = getIntInput(0, Integer.MAX_VALUE);

        if (notificationNumber == 0) {
            return;
        }

        try {
            List<Notification> notifications = notificationService.getUnreadNotificationsForUser(currentUserId);

            if (notificationNumber > 0 && notificationNumber <= notifications.size()) {
                Notification notification = notifications.get(notificationNumber - 1);
                boolean marked = notificationService.markAsRead(notification.getNotificationId());

                if (marked) {
                    System.out.println("Notification marked as read.");
                } else {
                    System.out.println("Failed to mark notification as read.");
                }
            } else {
                System.out.println("Invalid notification number.");
            }
        } catch (Exception e) {
            System.out.println("Error marking notification as read: " + e.getMessage());
        }
    }

    private String getNotificationIcon(Notification.NotificationType type) {
        switch (type) {
            case CONNECTION_REQUEST:
                return "🔗";
            case CONNECTION_ACCEPTED:
                return "✅";
            case NEW_POST:
                return "📝";
            case NEW_COMMENT:
                return "💬";
            case NEW_LIKE:
                return "❤️";
            case NEW_FOLLOW:
                return "👤";
            case MENTION:
                return "📢";
            default:
                return "🔔";
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