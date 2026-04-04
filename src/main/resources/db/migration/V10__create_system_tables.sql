-- Migration: Create system tables
-- Tables: disputes, dispute_attachments, system_configurations, configuration_history, analytics_snapshots, audit_logs
-- Requirements: 31.1, 32.1, 43.1, 44.1, 45.1

-- Table: disputes
-- Stores dispute records reported by restaurants or drivers regarding orders
-- Supports multiple dispute types and tracks resolution workflow
CREATE TABLE disputes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    reported_by BIGINT NOT NULL,
    dispute_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'OPEN',
    assigned_to BIGINT NULL,
    resolution TEXT NULL,
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_disputes_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_disputes_reported_by FOREIGN KEY (reported_by) REFERENCES users(id),
    CONSTRAINT fk_disputes_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id),
    CONSTRAINT chk_dispute_type CHECK (dispute_type IN ('PAYMENT_ISSUE', 'DELIVERY_ISSUE', 'CUSTOMER_ISSUE', 'OTHER')),
    CONSTRAINT chk_dispute_status CHECK (status IN ('OPEN', 'UNDER_REVIEW', 'RESOLVED', 'CLOSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_disputes_order_id ON disputes(order_id);
CREATE INDEX idx_disputes_reported_by ON disputes(reported_by);
CREATE INDEX idx_disputes_status ON disputes(status);
CREATE INDEX idx_disputes_assigned_to ON disputes(assigned_to);

-- Table: dispute_attachments
-- Stores supporting evidence files (photos, documents) attached to disputes
-- Links to disputes and tracks uploader information
CREATE TABLE dispute_attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dispute_id BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NULL,
    mime_type VARCHAR(100) NULL,
    uploaded_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dispute_attachments_dispute FOREIGN KEY (dispute_id) REFERENCES disputes(id),
    CONSTRAINT fk_dispute_attachments_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_dispute_attachments_dispute_id ON dispute_attachments(dispute_id);

-- Table: system_configurations
-- Stores system-wide configuration key-value pairs with type information
-- Supports multiple value types and tracks editability
CREATE TABLE system_configurations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT NOT NULL,
    value_type VARCHAR(20) NOT NULL,
    description TEXT NULL,
    is_editable BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_value_type CHECK (value_type IN ('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'JSON'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_system_configurations_key ON system_configurations(config_key);

-- Table: configuration_history
-- Tracks all changes to system configurations for audit purposes
-- Records previous and new values along with who made the change
CREATE TABLE configuration_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_id BIGINT NOT NULL,
    previous_value TEXT NULL,
    new_value TEXT NOT NULL,
    changed_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_configuration_history_config FOREIGN KEY (config_id) REFERENCES system_configurations(id),
    CONSTRAINT fk_configuration_history_changed_by FOREIGN KEY (changed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_configuration_history_config_id ON configuration_history(config_id);
CREATE INDEX idx_configuration_history_changed_by ON configuration_history(changed_by);

-- Table: analytics_snapshots
-- Stores daily snapshots of key platform metrics for analytics and reporting
-- Supports multiple metric types with flexible metadata storage
CREATE TABLE analytics_snapshots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    snapshot_date DATE NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    metric_value DECIMAL(15, 2) NOT NULL,
    metadata JSON NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_snapshot_date_type UNIQUE (snapshot_date, metric_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_analytics_snapshots_date ON analytics_snapshots(snapshot_date);
CREATE INDEX idx_analytics_snapshots_type ON analytics_snapshots(metric_type);

-- Table: audit_logs
-- Comprehensive audit trail for all significant system actions
-- Tracks entity changes with before/after values and user context
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NULL,
    old_values JSON NULL,
    new_values JSON NULL,
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_entity_id ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
