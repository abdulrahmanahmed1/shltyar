-- Migration: Create driver location tracking table
-- Tables: driver_locations
-- Requirements: 3.3, 3.4, 36.1, 36.2

-- Table: driver_locations
-- Stores real-time GPS location updates from drivers for tracking and dispatch
-- Location updates are received every 5-10 seconds and maintained for audit purposes
CREATE TABLE driver_locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    accuracy DECIMAL(10, 2) NULL COMMENT 'GPS accuracy in meters',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_driver_locations_driver FOREIGN KEY (driver_id) REFERENCES users(id),
    CONSTRAINT chk_latitude_range CHECK (latitude >= -90 AND latitude <= 90),
    CONSTRAINT chk_longitude_range CHECK (longitude >= -180 AND longitude <= 180)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Composite index for efficient queries: get latest location per driver
-- Supports queries like: SELECT * FROM driver_locations WHERE driver_id = ? ORDER BY created_at DESC LIMIT 1
CREATE INDEX idx_driver_locations_driver_created ON driver_locations(driver_id, created_at DESC);

-- Index for time-based queries and cleanup operations
CREATE INDEX idx_driver_locations_created_at ON driver_locations(created_at);
