package com.revconnect.services;

import com.revconnect.dao.UserDAO;
import com.revconnect.models.User;
import com.revconnect.utils.PasswordHasher;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class AuthenticationService {
    private UserDAO userDAO;
    private PasswordHasher passwordHasher;

    // Email validation regex pattern
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.passwordHasher = new PasswordHasher();
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

    // Register user with email validation
    public User register(User user) {
        try {
            // Validate email first
            if (!isValidEmail(user.getEmail())) {
                System.out.println("❌ Registration failed: Email must be a valid @gmail.com address");
                System.out.println("   Example: john.doe@gmail.com");
                return null;
            }

            // Check if email already exists
            if (userDAO.getUserByEmail(user.getEmail()) != null) {
                System.out.println("❌ Registration failed: Email already registered");
                return null;
            }

            // Check if username already exists
            if (userDAO.getUserByUsername(user.getUsername()) != null) {
                System.out.println("❌ Registration failed: Username already taken");
                return null;
            }

            // Note: The User object should already have password hashed before reaching here
            // If not, you need to get the plain password from somewhere
            // For now, assuming user already has hashed password in passwordHash field

            // Create user in database
            int userId = userDAO.createUser(user);

            if (userId > 0) {
                user.setUserId(userId);
                System.out.println("✅ User registered successfully with ID: " + userId);
                return user;
            } else {
                System.out.println("❌ Registration failed: Could not create user");
                return null;
            }

        } catch (SQLException e) {
            System.out.println("❌ Registration error: " + e.getMessage());
            return null;
        }
    }

    // Updated register method that takes plain password
    public User registerWithPlainPassword(User user, String plainPassword) {
        try {
            // Validate email first
            if (!isValidEmail(user.getEmail())) {
                System.out.println("❌ Registration failed: Email must be a valid @gmail.com address");
                System.out.println("   Example: john.doe@gmail.com");
                return null;
            }

            // Check if email already exists
            if (userDAO.getUserByEmail(user.getEmail()) != null) {
                System.out.println("❌ Registration failed: Email already registered");
                return null;
            }

            // Check if username already exists
            if (userDAO.getUserByUsername(user.getUsername()) != null) {
                System.out.println("❌ Registration failed: Username already taken");
                return null;
            }

            // Hash the plain password
            String hashedPassword = passwordHasher.hashPassword(plainPassword);
            user.setPasswordHash(hashedPassword);

            // Create user in database
            int userId = userDAO.createUser(user);

            if (userId > 0) {
                user.setUserId(userId);
                System.out.println("✅ User registered successfully with ID: " + userId);
                return user;
            } else {
                System.out.println("❌ Registration failed: Could not create user");
                return null;
            }

        } catch (SQLException e) {
            System.out.println("❌ Registration error: " + e.getMessage());
            return null;
        }
    }

    // Login method
    public User login(String usernameOrEmail, String plainPassword) {
        try {
            User user = null;

            // Check if input is email or username
            if (usernameOrEmail.contains("@")) {
                // It's an email
                if (!isValidEmail(usernameOrEmail)) {
                    System.out.println("❌ Login failed: Invalid email format");
                    return null;
                }
                user = userDAO.getUserByEmail(usernameOrEmail);
            } else {
                // It's a username
                user = userDAO.getUserByUsername(usernameOrEmail);
            }

            if (user == null) {
                System.out.println("❌ Login failed: User not found");
                return null;
            }

            // Verify password - compare plain password with hashed password
            boolean passwordMatches = passwordHasher.verifyPassword(plainPassword, user.getPasswordHash());

            if (passwordMatches) {
                System.out.println("✅ Login successful! Welcome back, " + user.getFirstName() + "!");
                return user;
            } else {
                System.out.println("❌ Login failed: Incorrect password");
                return null;
            }

        } catch (SQLException e) {
            System.out.println("❌ Login error: " + e.getMessage());
            return null;
        }
    }

    // Change password method
    public boolean changePassword(int userId, String currentPlainPassword, String newPlainPassword) {
        try {
            User user = userDAO.getUserById(userId);

            if (user == null) {
                System.out.println("❌ User not found");
                return false;
            }

            // Verify current password
            boolean passwordMatches = passwordHasher.verifyPassword(currentPlainPassword, user.getPasswordHash());

            if (!passwordMatches) {
                System.out.println("❌ Incorrect current password");
                return false;
            }

            // Hash new password
            String newHashedPassword = passwordHasher.hashPassword(newPlainPassword);

            // Update password in database
            boolean success = userDAO.updatePassword(userId, newHashedPassword);

            if (success) {
                System.out.println("✅ Password changed successfully");
                return true;
            } else {
                System.out.println("❌ Failed to change password");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error changing password: " + e.getMessage());
            return false;
        }
    }

    // Check if email exists
    public boolean emailExists(String email) {
        try {
            if (!isValidEmail(email)) {
                return false;
            }
            return userDAO.getUserByEmail(email) != null;
        } catch (SQLException e) {
            System.out.println("❌ Error checking email: " + e.getMessage());
            return false;
        }
    }

    // Check if username exists
    public boolean usernameExists(String username) {
        try {
            return userDAO.getUserByUsername(username) != null;
        } catch (SQLException e) {
            System.out.println("❌ Error checking username: " + e.getMessage());
            return false;
        }
    }

    // Get user by ID
    public User getUserById(int userId) {
        try {
            return userDAO.getUserById(userId);
        } catch (SQLException e) {
            System.out.println("❌ Error getting user: " + e.getMessage());
            return null;
        }
    }
}