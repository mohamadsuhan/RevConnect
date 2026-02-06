package com.revconnect.presentation;

import com.revconnect.services.AuthenticationService;
import com.revconnect.models.User;
import com.revconnect.models.PersonalUser;
import com.revconnect.models.BusinessUser;
import com.revconnect.models.CreatorUser;

import java.util.Scanner;
import java.util.regex.Pattern;

public class AuthMenu {
    private Scanner scanner;
    private AuthenticationService authService;

    // Email validation regex pattern
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public AuthMenu(Scanner scanner, AuthenticationService authService) {
        this.scanner = scanner;
        this.authService = authService;
    }

    public User showRegistrationMenu() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("             REGISTRATION             ");
        System.out.println("══════════════════════════════════════");

        System.out.println("Select account type:");
        System.out.println("1. Personal Account");
        System.out.println("2. Business Account");
        System.out.println("3. Creator Account");
        System.out.print("Enter your choice (1-3): ");

        int accountType = getIntInput(1, 3);

        switch (accountType) {
            case 1:
                return registerPersonalUser();
            case 2:
                return registerBusinessUser();
            case 3:
                return registerCreatorUser();
            default:
                return null;
        }
    }

    private User registerPersonalUser() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("       PERSONAL ACCOUNT REGISTRATION  ");
        System.out.println("══════════════════════════════════════");

        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();

            // Get valid email
            String email;
            while (true) {
                System.out.print("Email (must end with @gmail.com): ");
                email = scanner.nextLine();

                if (isValidEmail(email)) {
                    break;
                } else {
                    System.out.println("❌ Invalid email! Email must be a valid @gmail.com address.");
                    System.out.println("Example: john.doe@gmail.com");
                }
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.print("First Name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine();

            System.out.print("Bio (optional): ");
            String bio = scanner.nextLine();

            System.out.print("Location (optional): ");
            String location = scanner.nextLine();

            System.out.print("Occupation (optional): ");
            String occupation = scanner.nextLine();

            PersonalUser user = new PersonalUser(username, email, password, firstName, lastName);
            user.setBio(bio.isEmpty() ? null : bio);
            user.setLocation(location.isEmpty() ? null : location);
            user.setOccupation(occupation.isEmpty() ? null : occupation);

            User registeredUser = authService.register(user);

            if (registeredUser != null) {
                System.out.println("Personal account created successfully!");
                return registeredUser;
            }

        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }

        return null;
    }

    private User registerBusinessUser() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("       BUSINESS ACCOUNT REGISTRATION  ");
        System.out.println("══════════════════════════════════════");

        try {
            // Get valid email
            String email;
            while (true) {
                System.out.print("Business Email (must end with @gmail.com): ");
                email = scanner.nextLine();

                if (isValidEmail(email)) {
                    break;
                } else {
                    System.out.println("❌ Invalid email! Email must be a valid @gmail.com address.");
                }
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.print("Business Name: ");
            String businessName = scanner.nextLine();

            System.out.print("Business Type (e.g., Technology, Retail): ");
            String businessType = scanner.nextLine();

            System.out.print("Username (for login): ");
            String username = scanner.nextLine();

            System.out.print("Industry (optional): ");
            String industry = scanner.nextLine();

            System.out.print("Company Size (optional): ");
            String companySize = scanner.nextLine();

            BusinessUser user = new BusinessUser(username, email, password, businessName, businessType);
            user.setIndustry(industry.isEmpty() ? null : industry);
            user.setCompanySize(companySize.isEmpty() ? null : companySize);

            User registeredUser = authService.register(user);

            if (registeredUser != null) {
                System.out.println("Business account created successfully!");
                return registeredUser;
            }

        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }

        return null;
    }

    private User registerCreatorUser() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("       CREATOR ACCOUNT REGISTRATION   ");
        System.out.println("══════════════════════════════════════");

        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();

            // Get valid email
            String email;
            while (true) {
                System.out.print("Email (must end with @gmail.com): ");
                email = scanner.nextLine();

                if (isValidEmail(email)) {
                    break;
                } else {
                    System.out.println("❌ Invalid email! Email must be a valid @gmail.com address.");
                }
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.print("First Name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine();

            System.out.print("Creator Category (e.g., YouTuber, Blogger, Artist): ");
            String category = scanner.nextLine();

            System.out.print("Niche (optional): ");
            String niche = scanner.nextLine();

            System.out.print("Platforms (e.g., YouTube, Instagram): ");
            String platforms = scanner.nextLine();

            CreatorUser user = new CreatorUser(username, email, password, firstName, lastName, category);
            user.setNiche(niche.isEmpty() ? null : niche);
            user.setPlatforms(platforms.isEmpty() ? null : platforms);

            User registeredUser = authService.register(user);

            if (registeredUser != null) {
                System.out.println("Creator account created successfully!");
                return registeredUser;
            }

        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }

        return null;
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Check if email ends with @gmail.com
        if (!email.toLowerCase().endsWith("@gmail.com")) {
            return false;
        }

        // Validate with regex pattern
        return EMAIL_PATTERN.matcher(email).matches();
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

    // Helper method to get valid email input
    private String getValidEmailInput() {
        String email;
        while (true) {
            System.out.print("Email (must be @gmail.com): ");
            email = scanner.nextLine().trim();

            if (isValidEmail(email)) {
                return email;
            } else {
                System.out.println("❌ Invalid email format! Please enter a valid @gmail.com address.");
                System.out.println("   Examples: john.doe@gmail.com, jane.smith123@gmail.com");
            }
        }
    }
}