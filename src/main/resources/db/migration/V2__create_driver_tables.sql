-- Migration: Create driver application tables
-- Tables: driver_applications, driver_documents, driver_availability
-- Requirements: 2.1, 2.2, 2.3, 2.4, 2.5

-- Table: driver_applications
-- Stores driver application submissions with personal information and license details
CREATE TABLE driver_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    national_id VARCHAR(50) NOT NULL,
    driver_license_number VARCHAR(50) NOT NULL,
    driver_license_expiry DATE NOT NULL,
    motorcycle_license_number VARCHAR(50) NOT NULL,
    motorcycle_license_expiry DATE NOT NULL,
    motorcycle_plate_number VARCHAR(50) NOT NULL,
    address TEXT,
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    reviewed_by BIGINT NULL,
    reviewed_at TIMESTAMP NULL,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_driver_applications_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_driver_applications_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id),
    CONSTRAINT chk_application_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_driver_applications_user_id ON driver_applications(user_id);
CREATE INDEX idx_driver_applications_status ON driver_applications(status);
CREATE INDEX idx_driver_applications_reviewed_by ON driver_applications(reviewed_by);

-- Table: driver_documents
-- Stores uploaded documents for driver applications (ID, licenses, photos)
CREATE TABLE driver_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(100),
    uploaded_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_driver_documents_application FOREIGN KEY (application_id) REFERENCES driver_applications(id),
    CONSTRAINT fk_driver_documents_uploader FOREIGN KEY (uploaded_by) REFERENCES users(id),
    CONSTRAINT chk_document_type CHECK (document_type IN ('NATIONAL_ID', 'DRIVER_LICENSE', 'MOTORCYCLE_LICENSE', 'MOTORCYCLE_PHOTO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_driver_documents_application_id ON driver_documents(application_id);
CREATE INDEX idx_driver_documents_document_type ON driver_documents(document_type);

-- Table: driver_availability
-- Tracks driver availability status changes over time
CREATE TABLE driver_availability (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_driver_availability_driver FOREIGN KEY (driver_id) REFERENCES users(id),
    CONSTRAINT chk_availability_status CHECK (status IN ('AVAILABLE', 'UNAVAILABLE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_driver_availability_driver_id ON driver_availability(driver_id);
CREATE INDEX idx_driver_availability_status ON driver_availability(status);
CREATE INDEX idx_driver_availability_changed_at ON driver_availability(changed_at);
