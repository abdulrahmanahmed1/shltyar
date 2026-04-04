-- Migration: Create geographic tables for delivery areas and pricing
-- Tables: delivery_areas, delivery_prices
-- Requirements: 7.1, 7.2, 7.3, 8.1, 8.2

-- Table: delivery_areas
-- Stores geographic zones with defined boundaries for delivery pricing
-- Requirement 8.1: Store area name and geographic boundaries
CREATE TABLE delivery_areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    boundaries JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_delivery_areas_name ON delivery_areas(name);
CREATE INDEX idx_delivery_areas_is_active ON delivery_areas(is_active);

-- Table: delivery_prices
-- Stores area-to-area pricing matrix for delivery cost calculation
-- Requirement 8.2: Store origin area, destination area, and price amount
-- Requirement 8.3: Look up price using restaurant area and delivery area
-- Requirement 8.6: Support bidirectional pricing (Area A to B may differ from B to A)
CREATE TABLE delivery_prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    origin_area_id BIGINT NOT NULL,
    destination_area_id BIGINT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_delivery_prices_origin FOREIGN KEY (origin_area_id) REFERENCES delivery_areas(id),
    CONSTRAINT fk_delivery_prices_destination FOREIGN KEY (destination_area_id) REFERENCES delivery_areas(id),
    CONSTRAINT chk_price_positive CHECK (price >= 0),
    CONSTRAINT uq_area_pair UNIQUE (origin_area_id, destination_area_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_delivery_prices_origin ON delivery_prices(origin_area_id);
CREATE INDEX idx_delivery_prices_destination ON delivery_prices(destination_area_id);
CREATE INDEX idx_delivery_prices_is_active ON delivery_prices(is_active);
