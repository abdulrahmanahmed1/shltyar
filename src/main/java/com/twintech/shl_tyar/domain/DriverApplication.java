package com.twintech.shl_tyar.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Driver application entity representing driver onboarding requests.
 * Stores personal information and documents for admin approval workflow.
 * 
 * Requirements: 2.1, 2.2, 2.3
 */
@Entity
@Table(name = "driver_applications", indexes = {
    @Index(name = "idx_driver_applications_user_id", columnList = "user_id"),
    @Index(name = "idx_driver_applications_status", columnList = "status"),
    @Index(name = "idx_driver_applications_reviewed_by", columnList = "reviewed_by")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "national_id", nullable = false, length = 50)
    private String nationalId;
    
    @Column(name = "driver_license_number", nullable = false, length = 50)
    private String driverLicenseNumber;
    
    @Column(name = "driver_license_expiry", nullable = false)
    private LocalDate driverLicenseExpiry;
    
    @Column(name = "motorcycle_license_number", nullable = false, length = 50)
    private String motorcycleLicenseNumber;
    
    @Column(name = "motorcycle_license_expiry", nullable = false)
    private LocalDate motorcycleLicenseExpiry;
    
    @Column(name = "motorcycle_plate_number", nullable = false, length = 50)
    private String motorcyclePlateNumber;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "emergency_contact_name", length = 255)
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone", length = 50)
    private String emergencyContactPhone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Soft delete timestamp. When set, the application is considered deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    /**
     * Check if the application is approved.
     * 
     * @return true if application status is APPROVED
     */
    public boolean isApproved() {
        return status == ApplicationStatus.APPROVED;
    }
    
    /**
     * Check if the application is pending review.
     * 
     * @return true if application status is PENDING
     */
    public boolean isPending() {
        return status == ApplicationStatus.PENDING;
    }
    
    /**
     * Check if the application is rejected.
     * 
     * @return true if application status is REJECTED
     */
    public boolean isRejected() {
        return status == ApplicationStatus.REJECTED;
    }
    
    /**
     * Check if the application is deleted (soft delete).
     * 
     * @return true if application is soft-deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
