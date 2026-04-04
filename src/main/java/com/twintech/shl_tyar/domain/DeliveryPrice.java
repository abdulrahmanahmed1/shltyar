package com.twintech.shl_tyar.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Delivery price entity representing area-to-area pricing matrix.
 * Stores origin area, destination area, and price amount for delivery cost calculation.
 * Supports bidirectional pricing (Area A to B may differ from B to A).
 * 
 * Requirements: 7.3, 8.1, 8.2, 8.3
 */
@Entity
@Table(name = "delivery_prices", 
    indexes = {
        @Index(name = "idx_delivery_prices_origin", columnList = "origin_area_id"),
        @Index(name = "idx_delivery_prices_destination", columnList = "destination_area_id"),
        @Index(name = "idx_delivery_prices_is_active", columnList = "is_active")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_area_pair", columnNames = {"origin_area_id", "destination_area_id"})
    }
)
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_area_id", nullable = false)
    private DeliveryArea originArea;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_area_id", nullable = false)
    private DeliveryArea destinationArea;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(length = 3)
    private String currency = "USD";
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Soft delete timestamp. When set, the price is considered deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    /**
     * Check if the delivery price is active and not deleted.
     * 
     * @return true if price is active and not soft-deleted
     */
    public boolean isActiveAndNotDeleted() {
        return isActive && deletedAt == null;
    }
    
    /**
     * Check if the delivery price is deleted (soft delete).
     * 
     * @return true if price is soft-deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    /**
     * Validate that the price is non-negative.
     * 
     * @return true if price is greater than or equal to zero
     */
    public boolean isPriceValid() {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }
}
