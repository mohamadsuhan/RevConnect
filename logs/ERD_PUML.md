@startuml RevConnect_ER_Diagram

' =============================================
' REVCONNECT ENTITY RELATIONSHIP DIAGRAM
' PlantUML ER Diagram
' =============================================

skinparam linetype ortho
skinparam packageStyle rectangle
skinparam roundcorner 20
skinparam shadowing false
skinparam defaultFontName "Segoe UI"
skinparam titleFontSize 20
skinparam titleFontStyle bold

title RevConnect - Entity Relationship Diagram

' ========== ENTITIES DEFINITION ==========

entity "users" as users {
**user_id** : INTEGER <<PK>> <<AI>>
--
username : VARCHAR(50) <<UK>> <<NN>>
email : VARCHAR(100) <<UK>> <<NN>>
password_hash : VARCHAR(255) <<NN>>
first_name : VARCHAR(100)
last_name : VARCHAR(100)
user_type : ENUM('PERSONAL','CREATOR','BUSINESS')
created_at : TIMESTAMP <<DEFAULT>>
updated_at : TIMESTAMP <<ON_UPDATE>>
is_active : BOOLEAN <<DEFAULT>>
privacy_setting : ENUM('PUBLIC','PRIVATE')
}

entity "user_profiles" as user_profiles {
**profile_id** : INTEGER <<PK>> <<AI>>
--
user_id : INTEGER <<FK>> <<UK>> <<NN>>
full_name : VARCHAR(100)
bio : TEXT
location : VARCHAR(100)
occupation : VARCHAR(100)
profile_picture_path : VARCHAR(255)
website_url : VARCHAR(255)
}

entity "business_profiles" as business_profiles {
**business_id** : INTEGER <<PK>> <<AI>>
--
user_id : INTEGER <<FK>> <<UK>> <<NN>>
business_name : VARCHAR(100) <<NN>>
category : VARCHAR(50)
detailed_bio : TEXT
business_address : TEXT
contact_info : VARCHAR(100)
business_hours : TEXT
}

entity "creator_profiles" as creator_profiles {
**creator_id** : INTEGER <<PK>> <<AI>>
--
user_id : INTEGER <<FK>> <<UK>> <<NN>>
creator_name : VARCHAR(100) <<NN>>
category : VARCHAR(50)
detailed_bio : TEXT
portfolio_url : VARCHAR(255)
social_media_links : JSON
}

entity "posts" as posts {
**post_id** : INTEGER <<PK>> <<AI>>
--
user_id : INTEGER <<FK>> <<NN>>
content : TEXT <<NN>>
post_type : ENUM('TEXT','IMAGE','VIDEO','LINK')
visibility : ENUM('PUBLIC','FRIENDS','PRIVATE')
media_url : VARCHAR(255)
hashtags : JSON
like_count : INTEGER <<DEFAULT>>
comment_count : INTEGER <<DEFAULT>>
share_count : INTEGER <<DEFAULT>>
created_at : TIMESTAMP <<DEFAULT>>
updated_at : TIMESTAMP <<ON_UPDATE>>
is_pinned : BOOLEAN <<DEFAULT>>
original_post_id : INTEGER <<FK>>
}

entity "comments" as comments {
**comment_id** : INTEGER <<PK>> <<AI>>
--
post_id : INTEGER <<FK>> <<NN>>
user_id : INTEGER <<FK>> <<NN>>
content : TEXT <<NN>>
parent_comment_id : INTEGER <<FK>>
created_at : TIMESTAMP <<DEFAULT>>
updated_at : TIMESTAMP <<ON_UPDATE>>
}

entity "likes" as likes {
**like_id** : INTEGER <<PK>> <<AI>>
--
post_id : INTEGER <<FK>> <<NN>>
user_id : INTEGER <<FK>> <<NN>>
created_at : TIMESTAMP <<DEFAULT>>
}

entity "follows" as follows {
**follow_id** : INTEGER <<PK>> <<AI>>
--
follower_id : INTEGER <<FK>> <<NN>>
following_id : INTEGER <<FK>> <<NN>>
created_at : TIMESTAMP <<DEFAULT>>
}

entity "connections" as connections {
**connection_id** : INTEGER <<PK>> <<AI>>
--
user_id1 : INTEGER <<FK>> <<NN>>
user_id2 : INTEGER <<FK>> <<NN>>
status : ENUM('PENDING','ACCEPTED','REJECTED')
requested_by : INTEGER <<FK>> <<NN>>
created_at : TIMESTAMP <<DEFAULT>>
updated_at : TIMESTAMP <<ON_UPDATE>>
}

entity "notifications" as notifications {
**notification_id** : INTEGER <<PK>> <<AI>>
--
user_id : INTEGER <<FK>> <<NN>>
notification_type : ENUM('CONNECTION_REQUEST','CONNECTION_ACCEPTED','NEW_FOLLOWER','POST_LIKE','POST_COMMENT','POST_SHARE','NEW_POST') <<NN>>
source_id : INTEGER <<FK>> <<NN>>
post_id : INTEGER <<FK>>
comment_id : INTEGER <<FK>>
message : TEXT <<NN>>
is_read : BOOLEAN <<DEFAULT>>
created_at : TIMESTAMP <<DEFAULT>>
}

entity "post_analytics" as post_analytics {
**analytic_id** : INTEGER <<PK>> <<AI>>
--
post_id : INTEGER <<FK>> <<UK>> <<NN>>
total_likes : INTEGER <<DEFAULT>>
total_comments : INTEGER <<DEFAULT>>
total_shares : INTEGER <<DEFAULT>>
reach_count : INTEGER <<DEFAULT>>
last_updated : TIMESTAMP <<ON_UPDATE>>
}

' ========== RELATIONSHIPS ==========

' User has one profile (1:1)
users ||--|| user_profiles : "has"
users |o--o| business_profiles : "can be (business)"
users |o--o| creator_profiles : "can be (creator)"

' User creates posts (1:M)
users ||--o{ posts : "creates"

' Post has comments (1:M)
posts ||--o{ comments : "has"

' Comments can have parent comments (self-reference)
comments }o--|| comments : "replies to"

' Users like posts (M:N through likes)
users }o--o{ posts : "likes"
users }|--|| likes : "makes"
posts }|--|| likes : "receives"

' Users follow users (M:N through follows)
users }o--o{ users : "follows"
note on link #transparent
Implemented via follows table
end note

' Users connect with users (M:N through connections)
users }o--o{ users : "connects with"
note on link #transparent
Implemented via connections table
end note

' Post can be shared (self-reference)
posts }o--|| posts : "shares"

' Notifications relationships
users ||--o{ notifications : "receives"
posts }o--|| notifications : "triggers"
comments }o--|| notifications : "triggers"

' Post analytics (1:1)
posts ||--|| post_analytics : "has analytics"

' ========== LEGEND ==========

legend top
<b>Legend:</b>
|<color:#2299DD>PK</color>| Primary Key |
|<color:#2299DD>FK</color>| Foreign Key |
|<color:#2299DD>AI</color>| Auto Increment |
|<color:#2299DD>UK</color>| Unique Key |
|<color:#2299DD>NN</color>| Not Null |
|<color:#2299DD>DEFAULT</color>| Has Default Value |
|<color:#2299DD>ON_UPDATE</color>| Auto-updates on change |
end legend

' ========== CONSTRAINTS NOTES ==========

note top of posts
<b>Constraints:</b>
• UNIQUE(original_post_id) for shares
• CHECK: user_id != original_post_id.user_id
end note

note top of likes
<b>Constraint:</b>
• UNIQUE(post_id, user_id)
end note

note top of follows
<b>Constraints:</b>
• UNIQUE(follower_id, following_id)
• CHECK: follower_id != following_id
end note

note top of connections
<b>Constraints:</b>
• UNIQUE(user_id1, user_id2)
• CHECK: user_id1 < user_id2
• CHECK: requested_by IN (user_id1, user_id2)
end note

note top of notifications
<b>Constraint:</b>
• CHECK: (post_id IS NOT NULL) OR
(comment_id IS NOT NULL) OR
(notification_type IN ('CONNECTION_REQUEST',
'CONNECTION_ACCEPTED', 'NEW_FOLLOWER'))
end note

@enduml