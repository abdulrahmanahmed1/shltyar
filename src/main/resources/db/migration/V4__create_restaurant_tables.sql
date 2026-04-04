-- Migration: Create restaurant tables with multi-branch support
-- Tables: restaurants, restaurant_branches, customers
-- Requirements: 9.1, 9.2, 9.3, 10.1, 10.2, 13.1

-- Table: restaurants
-- Stores restaurant accounts with support for single branches or brands with multiple branches
-- Requirement 13.1: Allow selection of single branch or brand type
-- Requirement 13.2: Support brand with multiple branch entities
-- Requirement 13.3: Self-referencing foreign key for brand-branch relationship
CREATE TABLE restaurants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    business_name VARCHAR(255) NOT NULL,
    contact_name VARCHAR(255),
    contact_phone VARCHAR(50),
    contact_email VARCHAR(255),
    address TEXT,
    delivery_area_id BIGINT NULL,
    restaurant_type VARCHAR(20) DEFAULT 'SINGLE_BRANCH',
    parent_brand_id BIGINT NULL,
    sales_rep_id BIGINT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_restaurants_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_restaurants_delivery_area FOREIGN KEY (delivery_area_id) REFERENCES delivery_areas(id),
    CONSTRAINT fk_restaurants_parent_brand FOREIGN KEY (parent_brand_id) REFERENCES restaurants(id),
    CONSTRAINT fk_restaurants_sales_rep FOREIGN KEY (sales_rep_id) REFERENCES users(id),
    CONSTRAINT chk_restaurant_type CHECK (restaurant_type IN ('SINGLE_BRANCH', 'BRAND')),
    CONSTRAINT chk_restaurant_status CHECK (status IN ('ACTIVE', 'LOCKED', 'SUSPENDED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_restaurants_user_id ON restaurants(user_id);
CREATE INDEX idx_restaurants_sales_rep_id ON restaurants(sales_rep_id);
CREATE INDEX idx_restaurants_parent_brand_id ON restaurants(parent_brand_id);
CREATE INDEX idx_restaurants_status ON restaurants(status);
CREATE INDEX idx_restaurants_delivery_area_id ON restaurants(delivery_area_id);

-- Table: restaurant_branches
-- Stores individual branch locations for multi-branch restaurant brands
-- Requirement 13.2: Associate each branch with its parent brand
-- Requirement 13.6: Assign each branch to a specific delivery area
CREATE TABLE restaurant_branches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_id BIGINT NOT NULL,
    branch_name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    delivery_area_id BIGINT NOT NULL,
    contact_phone VARCHAR(50),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_restaurant_branches_brand FOREIGN KEY (brand_id) REFERENCES restaurants(id),
    CONSTRAINT fk_restaurant_branches_delivery_area FOREIGN KEY (delivery_area_id) REFERENCES delivery_areas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_restaurant_branches_brand_id ON restaurant_branches(brand_id);
CREATE INDEX idx_restaurant_branches_delivery_area_id ON restaurant_branches(delivery_area_id);
CREATE INDEX idx_restaurant_branches_is_active ON restaurant_branches(is_active);

-- Table: customers
-- Stores customer records managed by restaurants for quick order creation
-- Requirement 14.1: Allow restaurants to create customer records with name, phone, address, and notes
-- Requirement 14.2: Maintain an order count for each customer
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    address TEXT NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    notes TEXT,
    order_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_customers_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_customers_restaurant_id ON customers(restaurant_id);
CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_customers_name ON customers(name);
