## 📁 Project Structure
```text
RevConnect/
│
├── src/main/java/com/revconnect/
│ │
│ ├── Main.java # Application entry point
│ │
│ ├── models/ # Data Models (POJOs)
│ │ ├── User.java # Base user class
│ │ ├── PersonalUser.java # Personal user subclass
│ │ ├── BusinessUser.java # Business user subclass
│ │ ├── CreatorUser.java # Creator user subclass
│ │ ├── Post.java # Post model
│ │ ├── Comment.java # Comment model
│ │ ├── Like.java # Like model
│ │ ├── Follow.java # Follow model
│ │ └── Notification.java # Notification model
│ │
│ ├── dao/ # Data Access Objects
│ │ ├── UserDAO.java # User database operations
│ │ ├── PostDAO.java # Post database operations
│ │ ├── CommentDAO.java # Comment database operations
│ │ ├── LikeDAO.java # Like database operations
│ │ ├── ConnectionDAO.java # Connection database operations
│ │ ├── FollowDAO.java # Follow database operations
│ │ ├── NotificationDAO.java # Notification database operations
│ │ └── FeedDAO.java # Feed database operations
│ │
│ ├── services/ # Business Logic Layer
│ │ ├── UserService.java # User business logic
│ │ ├── AuthenticationService.java # Authentication logic
│ │ ├── PostService.java # Post business logic
│ │ ├── FeedService.java # Feed business logic
│ │ ├── ConnectionService.java # Connection business logic
│ │ ├── NotificationService.java # Notification business logic
│ │ └── PasswordHasher.java # Password hashing utility
│ │
│ ├── presentation/ # Console UI Layer
│ │ ├── ConsoleUI.java # Main controller
│ │ ├── AuthMenu.java # Authentication menus
│ │ ├── UserMenu.java # User profile menus
│ │ ├── PostMenu.java # Post-related menus
│ │ └── NotificationMenu.java # Notification menus
│ │
│ ├── utils/ # Utility Classes
│ │ └── ConsoleUtils.java # Console formatting utilities
│ │
│ └── config/ # Configuration
│ ├── DatabaseConfig.java # Database configuration
│ └── Log4jConfig.java # Logging configuration
│
├── src/main/resources/ # Configuration Files
│ ├── database.properties # Database connection properties
│ └── log4j2.xml # Log4j2 configuration
│
├── src/test/java/ # Test Files
│ └── com/revconnect/ # Test packages
│
├── pom.xml # Maven configuration
└── README.md # Project documentation
```
