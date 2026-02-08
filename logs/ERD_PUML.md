## Module 1: User Management Module
Purpose: Handles user authentication, registration, profile management, and account operations.
```text
UserManagement/
├── UserService.java        // User authentication and profile logic
├── UserDAO.java           // CRUD operations for User entity
├── User.java              // User entity model
├── ProfileService.java    // Profile management logic
├── ProfileDAO.java        // CRUD operations for Profile entity
└── Profile.java           // Profile entity model
```

### Module 2: Content Management Module
Purpose: Manages posts, comments, likes, and content interactions.
```text
ContentManagement/
├── PostService.java       // Business logic for post operations
├── PostDAO.java          // DAO for CRUD operations on posts
├── Post.java             // Post entity model
├── CommentService.java   // Comment management logic
├── CommentDAO.java       // DAO for comment operations
├── Comment.java          // Comment entity model
├── LikeService.java      // Like/unlike logic
└── LikeDAO.java          // Like operations
```

### Module 3: Social Network Module
Purpose: Handles social connections, follows, and relationship management.
```text
SocialNetwork/
├── ConnectionService.java  // Connection request/accept logic
├── ConnectionDAO.java     // Connection CRUD operations
├── Connection.java        // Connection entity model
├── FollowService.java     // Follow/unfollow logic
├── FollowDAO.java        // Follow operations
└── Follow.java           // Follow entity model
```

### Module 4: Notification Module
Purpose: Manages system notifications and alerts.
```text
Notification/
├── NotificationService.java  // Notification generation logic
├── NotificationDAO.java     // Notification CRUD operations
└── Notification.java        // Notification entity model
```

### Module 5: Utility Module
Purpose: Provides helper functionalities and shared utilities across the application.
```text
Utils/
├── DBConnection.java      // Database connection management
├── InputValidator.java    // Validates input formats and constraints
├── LoggerUtil.java        // Logging wrapper (Log4J)
├── DateUtil.java          // Date/time utilities
└── HashUtil.java          // Hashing and security utilities
```

### 📐 CLASS DIAGRAM (Simplified)
The class structure follows a layered and dependency-driven design, where controllers delegate to services, services interact with DAOs, and DAOs communicate with the database.

#### Dependency Flow:
```bash
    MainApplication → MainMenu → Services → DAOs → DatabaseUtil → MySQL
```

### Interaction Flow:
```text
┌──────────────────────────────────────────────────────┐
│               RevConnect Application                 │
├──────────────────────────────────────────────────────┤
│  ┌──────────────┐    ┌──────────────┐                │
│  │ InputHandler │───▶│ OutputHandler│                │
│  └──────────────┘    └──────────────┘                │
│           │                    │                     │
│  ┌────────▼────────────────────▼──────────────────┐  │
│  │            Business Logic Layer                │  │
│  │  ┌──────────────┐  ┌──────────────┐            │  │
│  │  │ Authentication│  │ Content      │            │  │
│  │  │ Module        │  │ Management   │            │  │
│  │  └──────────────┘  └──────────────┘            │  │ 
│  │  ┌──────────────┐  ┌──────────────┐            │  │
│  │  │ Social       │  │ Notification │            │  │
│  │  │ Network      │  │ Module       │            │  │
│  │  └──────────────┘  └──────────────┘            │  │
│  └───────────────────────┬────────────────────────┘  │
│                          │                           │
│  ┌───────────────────────▼────────────────────────┐  │
│  │               Data Access Layer                │  │
│  │  ┌──────────────────────────────────────────┐  │  │
│  │  │ UserDAO | PostDAO | ConnectionDAO | ...  │  │  │
│  │  └──────────────────────────────────────────┘  │  │
│  └───────────────────────┬────────────────────────┘  │
│                          │                           │
└──────────────────────────│───────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────┐
│                 External Systems                    │
├─────────────────────────────────────────────────────┤
│  ┌──────────────┐        ┌──────────────────────┐   │
│  │  MySQL DB    │        │ File System (Logs)   │   │
│  └──────────────┘        └──────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

### Deployment Setup:

```text
┌──────────────────────────────────────────────────┐
│           Development Environment                │
├──────────────────────────────────────────────────┤
│  ┌─────────┐    ┌─────────┐    ┌─────────┐       │
│  │ IntelliJ│───▶│  Maven  │───▶│   JVM   │       │ 
│  │  IDEA   │    │  Build  │    │ Runtime │       │
│  └─────────┘    └─────────┘    └─────────┘       │
│                      │                           │
│  ┌───────────────────▼────────────────────────┐  │
│  │         RevConnect Application (JAR)       │  │
│  └───────────────────┬────────────────────────┘  │
│                      │                           │
│  ┌───────────────────▼────────────────────────┐  │
│  │           MySQL Database (Local)           │  │
│  │     ┌───────────────────────────────────┐  │  │
│  │     │       revconnect_db               │  │  │
│  │     │   • users                         │  │  │
│  │     │   • posts                         │  │  │
│  │     │   • connections                   │  │  │
│  │     │   • notifications                 │  │  │
│  │     │   • profiles                      │  │  │
│  │     │   • comments                      │  │  │
│  │     │   • follows                       │  │  │
│  │     │   • likes                         │  │  │
│  │     └───────────────────────────────────┘  │  │
│  └────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────┘
```

### Security Layers Implementation:
```text
┌─────────────────────────────────────────────────┐
│              Security Architecture              │
├─────────────────────────────────────────────────┤
│                                                 │
│ 🔒 Layer 1: Input Validation                   │
│ • Email format validation                       │
│ • Strong password rules (min 8 chars, mix)      │
│ • Length & character checks                     │
│ • SQL injection prevention                      │
│ • XSS prevention                                │
│                                                 │
│ 🔑 Layer 2: Authentication                     │
│ • Password hashing (SHA-256 + Salt)             │
│ • Secure login flow with session tracking       │
│ • Account lockout after failed attempts         │
│ • Secure password reset with tokens             │
│ • Two-factor authentication (future)            │
│                                                 │
│ 🛡️ Layer 3: Authorization                      │
│ • User-specific data access control             │
│ • Ownership validation before CRUD operations   │
│ • Role-based permissions (PERSONAL/CREATOR/     │
│   BUSINESS user types)                          │
│ • Sensitive action verification                 │
│ • Privacy settings enforcement                  │
│                                                 │
│ 🔐 Layer 4: Data Protection                    │
│ • Database-level encryption                     │
│ • Sensitive data masking in logs                │
│ • Secure data transmission                      │
│ • Regular encrypted backups                     │
│ • Data retention and deletion policies          │
│                                                 │
│ 🧾 Layer 5: Auditing & Logging                 │
│ • Login attempt logging with IP tracking        │
│ • Error & exception tracking (Log4J)            │
│ • Security event monitoring                     │
│ • User activity audit trails                    │
│ • Compliance and reporting                      │
│                                                 │
└─────────────────────────────────────────────────┘
```

### 📦 PROJECT STRUCTURE
```text

RevConnectApp/
├── src/main/java/
│   ├── com/revconnectapp/
│   │   ├── App.java                    # Main application entry point
│   │   ├── DBTest.java                 # Database testing
│   │   └── NotificationTest.java       # Notification testing
│   │
│   ├── com/revconnectapp/dao/          # Data Access Objects
│   │   ├── CommentDAO.java
│   │   ├── ConnectionDAO.java
│   │   ├── FollowDAO.java
│   │   ├── LikeDAO.java
│   │   ├── NotificationDAO.java
│   │   ├── PostDAO.java
│   │   ├── ProfileDAO.java
│   │   └── UserDAO.java
│   │
│   ├── com/revconnectapp/model/        # Entity Models
│   │   ├── Comment.java
│   │   ├── Connection.java
│   │   ├── Like.java
│   │   ├── Notification.java
│   │   ├── Post.java
│   │   ├── Profile.java
│   │   └── User.java
│   │
│   ├── com/revconnectapp/repository/   # Repository interfaces
│   │
│   ├── com/revconnectapp/service/      # Business Services
│   │
│   ├── com/revconnectapp/ui/           # User Interface
│   │   ├── MainMenu.java
│   │   ├── NotificationMenu.java
│   │   └── ProfileMenu.java
│   │
│   └── com/revconnectapp/util/         # Utilities
│       ├── DBConnection.java
│       ├── DateUtil.java
│       ├── HashUtil.java
│       ├── InputValidator.java
│       └── LoggerUtil.java
│
├── src/main/resources/
│   ├── application.properties          # Configuration
│   ├── log4j.properties               # Logging configuration
│   └── schema.sql                     # Database schema
│
├── target/                            # Compiled classes
├── pom.xml                           # Maven configuration
├── README.md                         # Project documentation
└── REVCONNECT_ARCHITECTURE.md        # This document
```
