-- REVCONNECT ENTITY-RELATIONSHIP DIAGRAM
-- ======================================

┌─────────────────────────────────────────────────────────────────────────┐
│                           DATABASE: revconnect                          │
├─────────────────────────────────────────────────────────────────────────┤

─── ENTITIES ──────────────────────────────────────────────────────────────

╔═════════════════════════════════════════════════════════════════════════╗
║                             USERS (Core Entity)                         ║
╠═════════════════════════════════════════════════════════════════════════╣
║ PK  user_id           INT AUTO_INCREMENT                                ║
║     username          VARCHAR(50) UNIQUE NOT NULL                       ║
║     email             VARCHAR(100) UNIQUE NOT NULL                      ║
║     password_hash     VARCHAR(255) NOT NULL                             ║
║     first_name        VARCHAR(100)                                      ║
║     last_name         VARCHAR(100)                                      ║
║     user_type         ENUM('PERSONAL','CREATOR','BUSINESS') DEFAULT 'PERSONAL'
║     created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP              ║
║     updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE    ║
║     is_active         BOOLEAN DEFAULT TRUE                             ║
║     privacy_setting   ENUM('PUBLIC','PRIVATE') DEFAULT 'PUBLIC'        ║
╚═════════════════════════════════════════════════════════════════════════╝
│
│ 1:1
▼
╔═════════════════════════════════════════════════════════════════════════╗
║                          USER_PROFILES (Weak Entity)                    ║
╠═════════════════════════════════════════════════════════════════════════╣
║ PK  profile_id        INT AUTO_INCREMENT                                ║
║ FK  user_id           INT UNIQUE NOT NULL → users.user_id               ║
║     full_name         VARCHAR(100)                                      ║
║     bio               TEXT                                              ║
║     location          VARCHAR(100)                                      ║
║     occupation        VARCHAR(100)                                      ║
║     profile_picture_path VARCHAR(255)                                   ║
║     website_url       VARCHAR(255)                                      ║
╚═════════════════════════════════════════════════════════════════════════╝
│
│ Specialization (1:1)
├─────────────────────────────────────┐
│                                     │
▼                                     ▼
╔═══════════════════════════╗   ╔═══════════════════════════╗
║    BUSINESS_PROFILES      ║   ║    CREATOR_PROFILES      ║
╠═══════════════════════════╣   ╠═══════════════════════════╣
║ PK  business_id      INT  ║   ║ PK  creator_id      INT  ║
║ FK  user_id          INT  ║   ║ FK  user_id          INT  ║
║     business_name    VARCHAR(100) NOT NULL ║   ║     creator_name     VARCHAR(100) NOT NULL ║
║     category         VARCHAR(50)  ║   ║     category         VARCHAR(50)  ║
║     detailed_bio     TEXT         ║   ║     detailed_bio     TEXT         ║
║     business_address TEXT         ║   ║     portfolio_url    VARCHAR(255) ║
║     contact_info     VARCHAR(100) ║   ║     social_media_links JSON       ║
║     business_hours   TEXT         ║   ╚═══════════════════════════╝
╚═══════════════════════════╝

─── CONTENT ENTITIES ──────────────────────────────────────────────────────

         │
         │ 1:M (User creates Posts)
         ▼
╔═════════════════════════════════════════════════════════════════════════╗
║                                POSTS                                    ║
╠═════════════════════════════════════════════════════════════════════════╣
║ PK  post_id           INT AUTO_INCREMENT                                ║
║ FK  user_id           INT NOT NULL → users.user_id                      ║
║     content           TEXT NOT NULL                                     ║
║     post_type         ENUM('TEXT','IMAGE','VIDEO','LINK') DEFAULT 'TEXT'
║     visibility        ENUM('PUBLIC','FRIENDS','PRIVATE') DEFAULT 'PUBLIC'
║     media_url         VARCHAR(255)                                      ║
║     hashtags          JSON                                              ║
║     like_count        INT DEFAULT 0                                    ║
║     comment_count     INT DEFAULT 0                                    ║
║     share_count       INT DEFAULT 0                                    ║
║     created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP              ║
║     updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE    ║
║     is_pinned         BOOLEAN DEFAULT FALSE                            ║
║ FK  original_post_id  INT NULL → posts.post_id (for shares/reposts)    ║
╚═════════════════════════════════════════════════════════════════════════╝
│
│ 1:M (Post has Comments)
▼
╔═════════════════════════════════════════════════════════════════════════╗
║                               COMMENTS                                  ║
╠═════════════════════════════════════════════════════════════════════════╣
║ PK  comment_id        INT AUTO_INCREMENT                                ║
║ FK  post_id           INT NOT NULL → posts.post_id                      ║
║ FK  user_id           INT NOT NULL → users.user_id                      ║
║     content           TEXT NOT NULL                                     ║
║ FK  parent_comment_id INT NULL → comments.comment_id (for replies)      ║
║     created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP              ║
║     updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE    ║
╚═════════════════════════════════════════════════════════════════════════╝

─── INTERACTION ENTITIES ──────────────────────────────────────────────────

         │
         │ M:N (Users like Posts)
         ▼
╔═════════════════════════════════════════════════════════════════════════╗
║                                LIKES                                    ║
╠═════════════════════════════════════════════════════════════════════════╣
║ PK  like_id           INT AUTO_INCREMENT                                ║
║ FK  post_id           INT NOT NULL → posts.post_id                      ║
║ FK  user_id           INT NOT NULL → users.user_id                      ║
║     created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP              ║
║     UNIQUE (post_id, user_id)                                          ║
╚═════════════════════════════════════════════════════════════════════════╝

         │
         │ M:N (Users follow Users)
         ▼
╔═════════════════════════════════════════════════════════════════════════╗
║                                FOLLOWS                                  ║
╠═════════════════════════════════════════════════════════════════════════╣
║ PK  follow_id         INT AUTO_INCREMENT                                ║
║ FK  follower_id       INT NOT NULL → users.user_id                      ║
║ FK  following_id      INT NOT NULL → users.user_id                      ║
║     created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP              ║
║     UNIQUE (follower_id, following_id)                                 ║
╚═════════════════════════════════════════════════════════════════════════╝

         │
         │ M:N (Users connect with Users)
         ▼
╔═════════════════════════════════════════════════════════════════════════╗
║                              CONNECTIONS                                ║
╠═════════════════════════════════════════════════════════════════════════╣
║ PK  connection_id     INT AUTO_INCREMENT                                ║
║ FK  user_id1          INT NOT NULL → users.user_id                      ║
║ FK  user_id2          INT NOT NULL → users.user_id                      ║
║     status            ENUM('PENDING','ACCEPTED','REJECTED') DEFAULT 'PENDING'
║ FK  requested_by      INT NOT NULL → users.user_id                      ║
║     created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP              ║
║     updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE    ║
║     UNIQUE (user_id1, user_id2)                                        ║
╚═════════════════════════════════════════════════════════════════════════╝

─── NOTIFICATION ENTITY ───────────────────────────────────────────────────

         │
         │ 1:M (Users receive Notifications)
         ▼
╔═════════════════════════════════════════════════════════════════════════╗
║                            NOTIFICATIONS                                ║
╠═════════════════════════════════════════════════════════════════════════╣
║ PK  notification_id   INT AUTO_INCREMENT                                ║
║ FK  user_id           INT NOT NULL → users.user_id (recipient)          ║
║     notification_type ENUM('CONNECTION_REQUEST','CONNECTION_ACCEPTED',  ║
║                          'NEW_FOLLOWER','POST_LIKE','POST_COMMENT',     ║
║                          'POST_SHARE','NEW_POST') NOT NULL              ║
║ FK  source_id         INT NOT NULL → users.user_id (triggering user)    ║
║ FK  post_id           INT NULL → posts.post_id                          ║
║ FK  comment_id        INT NULL → comments.comment_id                    ║
║     message           TEXT NOT NULL                                     ║
║     is_read           BOOLEAN DEFAULT FALSE                            ║
║     created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP              ║
╚═════════════════════════════════════════════════════════════════════════╝

─── ANALYTICS ENTITY ──────────────────────────────────────────────────────

         │
         │ 1:1 (Post has Analytics)
         ▼
╔═════════════════════════════════════════════════════════════════════════╗
║                          POST_ANALYTICS                                 ║
╠═════════════════════════════════════════════════════════════════════════╣
║ PK  analytic_id       INT AUTO_INCREMENT                                ║
║ FK  post_id           INT UNIQUE NOT NULL → posts.post_id               ║
║     total_likes       INT DEFAULT 0                                    ║
║     total_comments    INT DEFAULT 0                                    ║
║     total_shares      INT DEFAULT 0                                    ║
║     reach_count       INT DEFAULT 0                                    ║
║     last_updated      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE    ║
╚════════════════════════

════════════

┌─────────────────────────────────────────────────────────────────────────┐
│                              REVCONNECT ER DIAGRAM                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────┐          ┌─────────────┐          ┌─────────────┐     │
│  │    USERS    │1────────1│USER_PROFILES│          │BUSINESS_PROF│     │
│  ├─────────────┤          ├─────────────┤          ├─────────────┤     │
│  │  • user_id  │          │• profile_id │          │• business_id│     │
│  │  • username │          │• user_id(FK)│          │• user_id(FK)│     │
│  │  • email    │          │• full_name  │          │• business_na│     │
│  │  • user_type│          │• bio        │          │• category   │     │
│  └──────┬──────┘          └─────────────┘          └─────────────┘     │
│         │1                                   1│                         │
│         │                                    │                         │
│         │                                   │                         │
│         │         ┌─────────────┐          │                         │
│         │         │CREATOR_PROF │          │                         │
│         │         ├─────────────┤          │                         │
│         │         │• creator_id │          │                         │
│         │         │• user_id(FK)│          │                         │
│         │         │• creator_na │          │                         │
│         │         │• category   │          │                         │
│         │         └─────────────┘          │                         │
│         │                                   │                         │
│         │1:M                                │                         │
│         │                                   │                         │
│         ▼                                   │                         │
│  ┌─────────────┐          ┌─────────────┐  │                         │
│  │    POSTS    │1────────*│  COMMENTS   │  │                         │
│  ├─────────────┤          ├─────────────┤  │                         │
│  │  • post_id  │          │• comment_id │  │                         │
│  │  • user_id(F│          │• post_id(FK)│  │                         │
│  │  • content  │          │• user_id(FK)│  │                         │
│  │  • post_type│          │• content    │  │                         │
│  │  • visibility│         │• parent_id(F│  │                         │
│  └──────┬──────┘          └─────────────┘  │                         │
│         │1:M                              │                         │
│         │                                 │                         │
│         │                                 │                         │
│         │        M:N                      │                         │
│         ├─────────────────┐               │                         │
│         │                 ▼               │                         │
│  ┌─────────────┐  ┌─────────────┐        │                         │
│  │    LIKES    │  │  FOLLOWS    │        │                         │
│  ├─────────────┤  ├─────────────┤        │                         │
│  │  • like_id  │  │  • follow_id│        │                         │
│  │  • post_id(F│  │  • follower_│        │                         │
│  │  • user_id(F│  │  • following│        │                         │
│  └─────────────┘  └─────────────┘        │                         │
│         ▲                 ▲               │                         │
│         │                 │               │                         │
│         │M:N             M:N              │                         │
│         │                 │               │                         │
│  ┌─────────────┐  ┌─────────────┐        │                         │
│  │  USERS(2)   │  │  USERS(2)   │        │                         │
│  └─────────────┘  └─────────────┘        │                         │
│         ▲                 ▲               │                         │
│         │                 │               │                         │
│         │M:N             M:N              │                         │
│         │                 │               │                         │
│  ┌─────────────┐  ┌─────────────┐        │                         │
│  │ CONNECTIONS │  │NOTIFICATIONS│        │                         │
│  ├─────────────┤  ├─────────────┤        │                         │
│  │• connection_│  │• notificatio│        │                         │
│  │• user_id1(FK│  │• user_id(FK)│        │                         │
│  │• user_id2(FK│  │• notif_type │        │                         │
│  │• status     │  │• source_id(F│        │                         │
│  │• requested_b│  │• post_id(FK)│        │                         │
│  └─────────────┘  └─────────────┘        │                         │
│                                           │                         │
│         ┌─────────────┐                   │                         │
│         │POST_ANALYTIC│                   │                         │
│         ├─────────────┤                   │                         │
│         │• analytic_id│                   │                         │
│         │• post_id(FK)│                   │                         │
│         │• total_likes│                   │                         │
│         │• total_comme│                   │                         │
│         └─────────────┘                   │                         │
│                                           │                         │
└───────────────────────────────────────────┴─────────────────────────┘═════════════════════════════════════╝