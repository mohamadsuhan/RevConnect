package com.revconnect.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseConfig {
    private static final Logger logger = LogManager.getLogger(DatabaseConfig.class);
    private static Connection connection;
    private static Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                logger.warn("database.properties not found, using default values");
                properties.setProperty("db.url", "jdbc:mysql://localhost:3306/revconnect");
                properties.setProperty("db.username", "root");
                properties.setProperty("db.password", "password");
            } else {
                properties.load(input);
            }
        } catch (Exception e) {
            logger.error("Error loading database properties: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                        properties.getProperty("db.url"),
                        properties.getProperty("db.username"),
                        properties.getProperty("db.password")
                );
                logger.info("Database connection established");
            } catch (ClassNotFoundException e) {
                logger.error("MySQL Driver not found: " + e.getMessage());
                throw new SQLException("Database driver not found");
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void initializeDatabase() {
        String[] createTables = {
                // Users table
                "CREATE TABLE IF NOT EXISTS users (" +
                        "user_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "username VARCHAR(50) UNIQUE NOT NULL, " +
                        "email VARCHAR(100) UNIQUE NOT NULL, " +
                        "password_hash VARCHAR(255) NOT NULL, " +
                        "first_name VARCHAR(50) NOT NULL, " +
                        "last_name VARCHAR(50) NOT NULL, " +
                        "user_type ENUM('PERSONAL', 'BUSINESS', 'CREATOR') NOT NULL, " +
                        "bio TEXT, " +
                        "profile_picture_url VARCHAR(255), " +
                        "website_url VARCHAR(255), " +
                        "business_name VARCHAR(100), " +
                        "business_type VARCHAR(50), " +
                        "creator_category VARCHAR(50), " +
                        "followers_count INT DEFAULT 0, " +
                        "following_count INT DEFAULT 0, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                        "is_active BOOLEAN DEFAULT TRUE" +
                        ")",

                // Posts table
                "CREATE TABLE IF NOT EXISTS posts (" +
                        "post_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "user_id INT NOT NULL, " +
                        "content TEXT NOT NULL, " +
                        "media_url VARCHAR(255), " +
                        "post_type ENUM('TEXT', 'IMAGE', 'VIDEO', 'LINK') DEFAULT 'TEXT', " +
                        "like_count INT DEFAULT 0, " +
                        "comment_count INT DEFAULT 0, " +
                        "share_count INT DEFAULT 0, " +
                        "visibility ENUM('PUBLIC', 'CONNECTIONS', 'PRIVATE') DEFAULT 'PUBLIC', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                        ")",

                // Comments table
                "CREATE TABLE IF NOT EXISTS comments (" +
                        "comment_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "post_id INT NOT NULL, " +
                        "user_id INT NOT NULL, " +
                        "content TEXT NOT NULL, " +
                        "parent_comment_id INT, " +
                        "like_count INT DEFAULT 0, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (parent_comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE" +
                        ")",

                // Likes table
                "CREATE TABLE IF NOT EXISTS likes (" +
                        "like_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "user_id INT NOT NULL, " +
                        "post_id INT, " +
                        "comment_id INT, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE, " +
                        "CONSTRAINT chk_like_target CHECK (post_id IS NOT NULL OR comment_id IS NOT NULL)" +
                        ")",

                // Connections table
                "CREATE TABLE IF NOT EXISTS connections (" +
                        "connection_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "user_id1 INT NOT NULL, " +
                        "user_id2 INT NOT NULL, " +
                        "status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'BLOCKED') DEFAULT 'PENDING', " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id1) REFERENCES users(user_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (user_id2) REFERENCES users(user_id) ON DELETE CASCADE, " +
                        "UNIQUE KEY unique_connection (user_id1, user_id2)" +
                        ")",

                // Follows table
                "CREATE TABLE IF NOT EXISTS follows (" +
                        "follow_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "follower_id INT NOT NULL, " +
                        "followed_id INT NOT NULL, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (followed_id) REFERENCES users(user_id) ON DELETE CASCADE, " +
                        "UNIQUE KEY unique_follow (follower_id, followed_id)" +
                        ")",

                // Notifications table
                "CREATE TABLE IF NOT EXISTS notifications (" +
                        "notification_id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "user_id INT NOT NULL, " +
                        "sender_id INT, " +
                        "type ENUM('CONNECTION_REQUEST', 'CONNECTION_ACCEPTED', 'NEW_POST', 'NEW_COMMENT', 'NEW_LIKE', 'NEW_FOLLOW', 'MENTION') NOT NULL, " +
                        "content TEXT NOT NULL, " +
                        "reference_id INT, " +
                        "reference_type VARCHAR(50), " +
                        "is_read BOOLEAN DEFAULT FALSE, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE SET NULL" +
                        ")"
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            for (String query : createTables) {
                stmt.executeUpdate(query);
            }
            logger.info("Database tables created/verified successfully");

        } catch (SQLException e) {
            logger.error("Error initializing database: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}