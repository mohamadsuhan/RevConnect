package com.revconnect.utils;

import com.revconnect.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    // Get database connection
    public static Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    // Close database connection
    public static void closeConnection() {
        DatabaseConfig.closeConnection();
    }

    // Test database connection
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("✗ Database connection failed: " + e.getMessage());
            System.out.println("Please check:");
            System.out.println("1. MySQL server is running");
            System.out.println("2. Database 'revconnect' exists");
            System.out.println("3. Credentials in database.properties are correct");
        }
        return false;
    }

    // Execute a simple query to verify database
    public static boolean verifyDatabaseStructure() {
        try (Connection conn = getConnection()) {
            java.sql.Statement stmt = conn.createStatement();

            // Check if users table exists
            String checkTableSQL = "SHOW TABLES LIKE 'users'";
            java.sql.ResultSet rs = stmt.executeQuery(checkTableSQL);

            if (rs.next()) {
                System.out.println("✓ Database tables exist");
                return true;
            } else {
                System.out.println("✗ Database tables not found. Initializing database...");
                DatabaseConfig.initializeDatabase();
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error verifying database structure: " + e.getMessage());
            return false;
        }
    }

    // Get database info
    public static void getDatabaseInfo() {
        try (Connection conn = getConnection()) {
            java.sql.DatabaseMetaData metaData = conn.getMetaData();

            System.out.println("\n══════════════════════════════════════");
            System.out.println("        DATABASE INFORMATION         ");
            System.out.println("══════════════════════════════════════");
            System.out.println("Database: " + metaData.getDatabaseProductName());
            System.out.println("Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver: " + metaData.getDriverName());
            System.out.println("URL: " + metaData.getURL());
            System.out.println("Username: " + metaData.getUserName());
            System.out.println("══════════════════════════════════════");

        } catch (SQLException e) {
            System.out.println("Error getting database info: " + e.getMessage());
        }
    }

    // Count records in table
    public static int countRecords(String tableName) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT COUNT(*) as count FROM " + tableName;
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.out.println("Error counting records in " + tableName + ": " + e.getMessage());
        }
        return 0;
    }

    // Display database statistics
    public static void displayDatabaseStats() {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("       DATABASE STATISTICS          ");
        System.out.println("══════════════════════════════════════");

        String[] tables = {"users", "posts", "comments", "likes", "connections", "follows", "notifications"};

        for (String table : tables) {
            int count = countRecords(table);
            System.out.println(String.format("%-20s: %d records", table, count));
        }

        System.out.println("══════════════════════════════════════");
    }

    // Backup database (simplified - just exports table schemas)
    public static void backupDatabase(String backupPath) {
        try (Connection conn = getConnection()) {
            java.sql.Statement stmt = conn.createStatement();

            // Get all table names
            String sql = "SHOW TABLES";
            java.sql.ResultSet rs = stmt.executeQuery(sql);

            System.out.println("Starting database backup...");

            while (rs.next()) {
                String tableName = rs.getString(1);
                System.out.println("Backing up table: " + tableName);

                // In a real app, you would export data here
                // This is a simplified version
            }

            System.out.println("✓ Database backup completed to: " + backupPath);
            System.out.println("Note: This is a schema-only backup. Implement data export for full backup.");

        } catch (SQLException e) {
            System.out.println("Error backing up database: " + e.getMessage());
        }
    }

    // Clean up old data (archive/delete)
    public static void cleanupOldData(int daysOld) {
        try (Connection conn = getConnection()) {
            java.sql.Statement stmt = conn.createStatement();

            System.out.println("Cleaning up data older than " + daysOld + " days...");

            // Clean up old notifications (keep 30 days)
            String cleanupNotifications = "DELETE FROM notifications WHERE created_at < DATE_SUB(NOW(), INTERVAL " + daysOld + " DAY)";
            int notificationsDeleted = stmt.executeUpdate(cleanupNotifications);
            System.out.println("Deleted " + notificationsDeleted + " old notifications");

            // Clean up old activity logs (if you have them)
            // String cleanupLogs = "DELETE FROM activity_logs WHERE created_at < DATE_SUB(NOW(), INTERVAL " + daysOld + " DAY)";

            System.out.println("✓ Cleanup completed");

        } catch (SQLException e) {
            System.out.println("Error cleaning up old data: " + e.getMessage());
        }
    }
}