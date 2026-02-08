package com.revconnect.utils;

import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ConsoleUtils {
    private static final Scanner scanner = new Scanner(System.in);

    // Clear console (platform independent)
    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // For Windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // For Linux/Unix/Mac
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            // If clearing fails, just print some newlines
            // FIXED: Using loop instead of .repeat() for Java 8 compatibility
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    // Print header with title
    public static void printHeader(String title) {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║" + centerText(title, 36) + "║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    // Print section header
    public static void printSection(String title) {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("        " + title.toUpperCase());
        System.out.println("══════════════════════════════════════");
    }

    // Print sub-section
    public static void printSubSection(String title) {
        System.out.println("\n──────────────────────────────────────");
        System.out.println("  " + title);
        System.out.println("──────────────────────────────────────");
    }

    // Center text in given width
    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }

        int padding = (width - text.length()) / 2;
        StringBuilder centered = new StringBuilder();

        for (int i = 0; i < padding; i++) {
            centered.append(" ");
        }

        centered.append(text);

        while (centered.length() < width) {
            centered.append(" ");
        }

        return centered.toString();
    }

    // Get integer input with validation
    public static int getIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    // Get string input with validation
    public static String getStringInput(String prompt, boolean required) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!required || !input.isEmpty()) {
                return input;
            } else {
                System.out.println("This field is required. Please enter a value.");
            }
        }
    }

    // Get yes/no input
    public static boolean getYesNoInput(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if ("y".equals(input) || "yes".equals(input)) {
                return true;
            } else if ("n".equals(input) || "no".equals(input)) {
                return false;
            } else {
                System.out.println("Please enter 'y' for yes or 'n' for no.");
            }
        }
    }

    // Get password input (masked)
    public static String getPasswordInput(String prompt) {
        // For console apps, we can't mask input easily without external libraries
        // In a real app, you'd use a proper password input method
        return getStringInput(prompt, true);
    }

    // Print success message
    public static void printSuccess(String message) {
        System.out.println("✓ " + message);
    }

    // Print error message
    public static void printError(String message) {
        System.out.println("✗ " + message);
    }

    // Print info message
    public static void printInfo(String message) {
        System.out.println("ℹ " + message);
    }

    // Print warning message
    public static void printWarning(String message) {
        System.out.println("⚠ " + message);
    }

    // Print loading animation
    public static void showLoading(String message) {
        System.out.print(message);

        String[] dots = {".  ", ".. ", "..."};
        for (int i = 0; i < 3; i++) {
            System.out.print("\r" + message + dots[i]);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }

    // Print progress bar
    public static void printProgressBar(int percentage, int width) {
        if (percentage < 0) percentage = 0;
        if (percentage > 100) percentage = 100;

        int filledWidth = (percentage * width) / 100;
        int emptyWidth = width - filledWidth;

        StringBuilder bar = new StringBuilder();
        bar.append("[");

        for (int i = 0; i < filledWidth; i++) {
            bar.append("█");
        }

        for (int i = 0; i < emptyWidth; i++) {
            bar.append(" ");
        }

        bar.append("] ");
        bar.append(percentage).append("%");

        System.out.print("\r" + bar);

        if (percentage == 100) {
            System.out.println();
        }
    }

    // Format date and time
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        return dateTime.format(formatter);
    }

    // Format date only
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return dateTime.format(formatter);
    }

    // Format time only
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    // Truncate text with ellipsis
    public static String truncateText(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength - 3) + "...";
    }

    // Display paginated list
    public static <T> void displayPaginatedList(List<T> items, int page, int pageSize, String itemName) {
        if (items == null || items.isEmpty()) {
            System.out.println("No " + itemName + " found.");
            return;
        }

        int totalItems = items.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);

        System.out.println("\n══════════════════════════════════════");
        System.out.println(itemName.toUpperCase() + " (Page " + page + " of " + totalPages + ")");
        System.out.println("Showing " + (startIndex + 1) + "-" + endIndex + " of " + totalItems + " items");
        System.out.println("══════════════════════════════════════");

        for (int i = startIndex; i < endIndex; i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }

        System.out.println("══════════════════════════════════════");

        if (totalPages > 1) {
            System.out.println("\nNavigation:");
            if (page > 1) {
                System.out.println("P - Previous Page");
            }
            if (page < totalPages) {
                System.out.println("N - Next Page");
            }
            System.out.println("B - Back to Menu");
        }
    }

    // Wait for user to press Enter
    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Display menu options
    public static void displayMenu(String title, String[] options) {
        printSection(title);

        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }

        System.out.println("══════════════════════════════════════");
    }

    // Get menu choice
    public static int getMenuChoice(int maxOption) {
        return getIntInput("Enter your choice (1-" + maxOption + "): ", 1, maxOption);
    }

    // Confirm action
    public static boolean confirmAction(String action) {
        System.out.println("\n⚠  CONFIRM ACTION");
        System.out.println("You are about to: " + action);
        System.out.println("This action cannot be undone.");

        return getYesNoInput("Are you sure you want to proceed?");
    }

    // Print ASCII art banner
    public static void printBanner() {
        String banner =
                "                ╔══════════════════════════════════════════════════════════╗\n" +
                        "                ║                                                          ║\n" +
                        "                ║    ██████╗ ███████╗██╗   ██╗ ██████╗ ██████╗ ███╗   ██╗ ║\n" +
                        "                ║    ██╔══██╗██╔════╝██║   ██║██╔════╝██╔═══██╗████╗  ██║ ║\n" +
                        "                ║    ██████╔╝█████╗  ██║   ██║██║     ██║   ██║██╔██╗ ██║ ║\n" +
                        "                ║    ██╔══██╗██╔══╝  ╚██╗ ██╔╝██║     ██║   ██║██║╚██╗██║ ║\n" +
                        "                ║    ██║  ██║███████╗ ╚████╔╝ ╚██████╗╚██████╔╝██║ ╚████║ ║\n" +
                        "                ║    ╚═╝  ╚═╝╚══════╝  ╚═══╝   ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝ ║\n" +
                        "                ║                                                          ║\n" +
                        "                ║               Professional Networking Platform           ║\n" +
                        "                ║                                                          ║\n" +
                        "                ╚══════════════════════════════════════════════════════════╝\n" +
                        "                ";

        System.out.println(banner);
    }

    // Print current date and time
    public static void printDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy - hh:mm a");
        System.out.println("Date: " + now.format(formatter));
    }

    // Close scanner (call at end of application)
    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    // Test utility methods
    public static void main(String[] args) {
        printBanner();
        printDateTime();

        printHeader("ConsoleUtils Test");

        // Test progress bar
        System.out.println("\nLoading...");
        for (int i = 0; i <= 100; i += 10) {
            printProgressBar(i, 50);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Test loading animation
        showLoading("Processing");
        printSuccess("Operation completed successfully!");

        // Test input methods
        String name = getStringInput("Enter your name: ", true);
        int age = getIntInput("Enter your age: ", 0, 150);

        System.out.println("\nHello " + name + ", you are " + age + " years old.");

        pressEnterToContinue();

        // Close scanner after test
        closeScanner();
    }
}