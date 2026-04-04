package com.twintech.shl_tyar.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Delivery area entity representing geographic zones for delivery pricing.
 * Stores area name and geographic boundaries in JSONB format.
 * 
 * Requirements: 7.1, 7.2, 8.1
 */
@Entity
@Table(name = "delivery_areas", indexes = {
    @Index(name = "idx_delivery_areas_name", columnList = "name"),
    @Index(name = "idx_delivery_areas_is_active", columnList = "is_active")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryArea {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 255)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * Geographic boundaries stored as JSON (GeoJSON polygon format).
     * Example: {"type":"Polygon","coordinates":[[[lng,lat],[lng,lat],...]]}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private String boundaries;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Soft delete timestamp. When set, the area is considered deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    /**
     * Check if the delivery area is active and not deleted.
     * 
     * @return true if area is active and not soft-deleted
     */
    public boolean isActiveAndNotDeleted() {
        return isActive && deletedAt == null;
    }
    
    /**
     * Check if the delivery area is deleted (soft delete).
     * 
     * @return true if area is soft-deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
