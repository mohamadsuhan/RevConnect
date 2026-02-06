package com.revconnect.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 10000;
    private static final String ALGORITHM = "SHA-256";

    // Hash password with salt
    public String hashPassword(String password) {
        try {
            // Generate random salt
            byte[] salt = generateSalt();

            // Hash password with salt
            byte[] hashedPassword = hashWithSalt(password, salt);

            // Combine salt and hash
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);

            // Format: algorithm:iterations:salt:hash
            return String.format("%s:%d:%s:%s", ALGORITHM, ITERATIONS, saltBase64, hashBase64);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            // Fallback to simple hash if algorithm not available
            return simpleHash(password);
        }
    }

    // Verify password
    public boolean verifyPassword(String password, String storedHash) {
        try {
            // Parse stored hash
            String[] parts = storedHash.split(":");

            if (parts.length != 4) {
                // Try to verify with old simple hash format
                return verifySimpleHash(password, storedHash);
            }

            String algorithm = parts[0];
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] storedHashBytes = Base64.getDecoder().decode(parts[3]);

            // Hash input password with same salt and iterations
            byte[] testHash = hashWithSalt(password, salt, iterations, algorithm);

            // Compare hashes
            return MessageDigest.isEqual(testHash, storedHashBytes);

        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    // Generate random salt
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    // Hash password with salt
    private byte[] hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        return hashWithSalt(password, salt, ITERATIONS, ALGORITHM);
    }

    // Hash password with salt, iterations, and algorithm
    private byte[] hashWithSalt(String password, byte[] salt, int iterations, String algorithm)
            throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance(algorithm);

        // Add salt to password
        digest.update(salt);
        digest.update(password.getBytes());

        byte[] hash = digest.digest();

        // Apply iterations
        for (int i = 1; i < iterations; i++) {
            digest.reset();
            hash = digest.digest(hash);
        }

        return hash;
    }

    // Simple hash for fallback (not secure, only for compatibility)
    private String simpleHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Last resort - very insecure, only for development
            return Integer.toHexString(password.hashCode());
        }
    }

    // Verify simple hash
    private boolean verifySimpleHash(String password, String storedHash) {
        String testHash = simpleHash(password);
        return testHash.equals(storedHash);
    }

    // Check password strength
    public String checkPasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return "WEAK: Password must be at least 8 characters long";
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        int score = 0;
        if (hasUpperCase) score++;
        if (hasLowerCase) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;
        if (password.length() >= 12) score++;

        switch (score) {
            case 5:
                return "STRONG: Excellent password!";
            case 4:
                return "GOOD: Strong password";
            case 3:
                return "MODERATE: Consider adding more complexity";
            default:
                return "WEAK: Please use a stronger password";
        }
    }

    // Generate random password
    public String generateRandomPassword(int length) {
        if (length < 8) length = 8;

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()-_=+";

        String allChars = upper + lower + digits + special;
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder(length);

        // Ensure at least one of each type
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Fill remaining characters
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password
        String shuffled = shuffleString(password.toString(), random);

        return shuffled;
    }

    // Shuffle string
    private String shuffleString(String input, SecureRandom random) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = characters[index];
            characters[index] = characters[i];
            characters[i] = temp;
        }
        return new String(characters);
    }

    // Test password hasher
    public static void main(String[] args) {
        PasswordHasher hasher = new PasswordHasher();

        // Test password hashing
        String password = "SecurePass123!";
        String hashed = hasher.hashPassword(password);

        System.out.println("Password: " + password);
        System.out.println("Hashed: " + hashed);
        System.out.println("Strength: " + hasher.checkPasswordStrength(password));

        // Test verification
        boolean verified = hasher.verifyPassword(password, hashed);
        System.out.println("Verification: " + (verified ? "✓ SUCCESS" : "✗ FAILED"));

        // Test wrong password
        boolean wrongVerified = hasher.verifyPassword("WrongPass", hashed);
        System.out.println("Wrong password test: " + (!wrongVerified ? "✓ CORRECT" : "✗ INCORRECT"));

        // Generate random password
        String randomPass = hasher.generateRandomPassword(12);
        System.out.println("\nGenerated password: " + randomPass);
        System.out.println("Strength: " + hasher.checkPasswordStrength(randomPass));
    }
}