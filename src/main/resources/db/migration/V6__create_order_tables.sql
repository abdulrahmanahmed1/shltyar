-- Migration: Create order tables
-- Tables: orders, order_status_history, delivery_proof
-- Requirements: 3.1, 4.1, 4.2, 5.1, 5.2, 6.1, 28.1

-- Table: orders
-- Stores delivery orders with complete lifecycle tracking from creation to completion
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    restaurant_id BIGINT NOT NULL,
    branch_id BIGINT NULL,
    customer_id BIGINT NOT NULL,
    driver_id BIGINT NULL,
    pickup_address TEXT NOT NULL,
    pickup_latitude DECIMAL(10, 8) NULL,
    pickup_longitude DECIMAL(11, 8) NULL,
    delivery_address TEXT NOT NULL,
    delivery_latitude DECIMAL(10, 8) NULL,
    delivery_longitude DECIMAL(11, 8) NULL,
    delivery_area_id BIGINT NOT NULL,
    order_total DECIMAL(10, 2) NOT NULL,
    delivery_price DECIMAL(10, 2) NOT NULL,
    customer_reference VARCHAR(255) NULL,
    notes TEXT NULL,
    image_path VARCHAR(500) NULL,
    status VARCHAR(30) DEFAULT 'CREATED',
    assigned_at TIMESTAMP NULL,
    picked_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    completion_latitude DECIMAL(10, 8) NULL,
    completion_longitude DECIMAL(11, 8) NULL,
    cancellation_reason TEXT NULL,
    cancelled_by BIGINT NULL,
    estimated_delivery_time INTEGER NULL,
    actual_delivery_time INTEGER NULL,
    driver_rating INTEGER NULL,
    driver_rating_comment TEXT NULL,
    idempotency_key VARCHAR(255) UNIQUE NULL,
    version INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    CONSTRAINT fk_orders_branch FOREIGN KEY (branch_id) REFERENCES restaurant_branches(id),
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_orders_driver FOREIGN KEY (driver_id) REFERENCES users(id),
    CONSTRAINT fk_orders_delivery_area FOREIGN KEY (delivery_area_id) REFERENCES delivery_areas(id),
    CONSTRAINT fk_orders_cancelled_by FOREIGN KEY (cancelled_by) REFERENCES users(id),
    CONSTRAINT chk_order_status CHECK (status IN ('CREATED', 'ASSIGNED', 'PICKED_FROM_RESTAURANT', 'DELIVERED', 'CUSTOMER_NOT_AVAILABLE', 'CANCELLED')),
    CONSTRAINT chk_driver_rating CHECK (driver_rating IS NULL OR (driver_rating >= 1 AND driver_rating <= 5)),
    CONSTRAINT chk_order_total_positive CHECK (order_total >= 0),
    CONSTRAINT chk_delivery_price_positive CHECK (delivery_price >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_orders_restaurant_id ON orders(restaurant_id);
CREATE INDEX idx_orders_driver_id ON orders(driver_id);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_order_number ON orders(order_number);
CREATE INDEX idx_orders_idempotency_key ON orders(idempotency_key);

-- Table: order_status_history
-- Audit trail for all order status transitions with GPS coordinates and user tracking
CREATE TABLE order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    previous_status VARCHAR(30) NULL,
    new_status VARCHAR(30) NOT NULL,
    changed_by BIGINT NULL,
    latitude DECIMAL(10, 8) NULL,
    longitude DECIMAL(11, 8) NULL,
    notes TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_status_history_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_status_history_user FOREIGN KEY (changed_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_order_status_history_order_id ON order_status_history(order_id);
CREATE INDEX idx_order_status_history_created_at ON order_status_history(created_at);

-- Table: delivery_proof
-- Stores photo evidence of delivery completion with GPS coordinates
CREATE TABLE delivery_proof (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NULL,
    mime_type VARCHAR(100) NULL,
    latitude DECIMAL(10, 8) NULL,
    longitude DECIMAL(11, 8) NULL,
    uploaded_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_delivery_proof_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_delivery_proof_uploader FOREIGN KEY (uploaded_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_delivery_proof_order_id ON delivery_proof(order_id);
CREATE INDEX idx_delivery_proof_uploaded_by ON delivery_proof(uploaded_by);
