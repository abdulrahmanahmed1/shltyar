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
 * User entity representing all system users (Admin, Sales, Restaurant, Driver).
 * Supports role-based access control and account status management.
 * 
 * Requirements: 1.1, 1.2, 1.3, 42.1
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_role", columnList = "role"),
    @Index(name = "idx_users_status", columnList = "status"),
    @Index(name = "idx_users_deleted_at", columnList = "deleted_at")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    /**
     * BCrypt hashed password. Never store plain text passwords.
     * BCrypt strength: 12 (as per security requirements)
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;
    
    @Column(length = 50)
    private String phone;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Soft delete timestamp. When set, the user is considered deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    /**
     * Check if the user account is active and not deleted.
     * 
     * @return true if user is active and not soft-deleted
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE && deletedAt == null;
    }
    
    /**
     * Check if the user account is deleted (soft delete).
     * 
     * @return true if user is soft-deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
