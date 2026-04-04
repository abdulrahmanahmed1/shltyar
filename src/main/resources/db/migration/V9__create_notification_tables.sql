-- Migration: Create notification tables
-- Tables: notifications, notification_preferences, system_announcements, announcement_views
-- Requirements: 29.1, 29.2, 29.3, 30.1

-- Table: notifications
-- Stores user notifications for order status changes, subscription expiry, earnings summaries, etc.
-- Supports priority levels and tracks read status for notification management
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    related_entity_type VARCHAR(50) NULL,
    related_entity_id BIGINT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    priority VARCHAR(20) DEFAULT 'NORMAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_notification_type CHECK (notification_type IN ('ORDER_STATUS', 'SUBSCRIPTION_EXPIRY', 'DOCUMENT_EXPIRY', 'EARNINGS_SUMMARY', 'SYSTEM_ANNOUNCEMENT')),
    CONSTRAINT chk_notification_priority CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_type ON notifications(notification_type);

-- Table: notification_preferences
-- Stores user preferences for notification types and delivery methods
-- Allows users to control which notifications they receive and via which channels
CREATE TABLE notification_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    websocket_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_preferences_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_user_notification_type UNIQUE (user_id, notification_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_notification_preferences_user_id ON notification_preferences(user_id);

-- Table: system_announcements
-- Stores system-wide announcements that can be targeted to specific roles or all users
-- Supports scheduled publishing and automatic expiration
CREATE TABLE system_announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    target_role VARCHAR(20) NULL,
    is_urgent BOOLEAN DEFAULT FALSE,
    publish_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expire_at TIMESTAMP NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_system_announcements_created_by FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_system_announcements_target_role ON system_announcements(target_role);
CREATE INDEX idx_system_announcements_publish_at ON system_announcements(publish_at);
CREATE INDEX idx_system_announcements_expire_at ON system_announcements(expire_at);

-- Table: announcement_views
-- Tracks which users have viewed which system announcements
-- Prevents duplicate views and enables read status tracking
CREATE TABLE announcement_views (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    announcement_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_announcement_views_announcement FOREIGN KEY (announcement_id) REFERENCES system_announcements(id),
    CONSTRAINT fk_announcement_views_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_announcement_user UNIQUE (announcement_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_announcement_views_announcement_id ON announcement_views(announcement_id);
CREATE INDEX idx_announcement_views_user_id ON announcement_views(user_id);
