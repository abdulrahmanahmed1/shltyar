package com.twintech.shl_tyar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.twintech.shl_tyar.domain.DeliveryArea;

/**
 * Repository interface for DeliveryArea entity.
 * Provides CRUD operations and custom query methods for delivery area management.
 * 
 * Requirements: 7.1, 8.1
 */
@Repository
public interface DeliveryAreaRepository extends JpaRepository<DeliveryArea, Long> {
    
    /**
     * Find a delivery area by name.
     * Used for area lookup and validation.
     * 
     * @param name the name of the delivery area
     * @return Optional containing the delivery area if found, empty otherwise
     */
    Optional<DeliveryArea> findByName(String name);
    
    /**
     * Find all active delivery areas (not soft-deleted).
     * Used for listing available areas for order creation and pricing configuration.
     * 
     * @return List of active delivery areas where isActive is true and deletedAt is null
     */
    @Query("SELECT da FROM DeliveryArea da WHERE da.isActive = true AND da.deletedAt IS NULL")
    List<DeliveryArea> findAllActive();
    
    /**
     * Find all delivery areas by active status.
     * Used for filtering areas by active/inactive status.
     * 
     * @param isActive the active status to filter by
     * @return List of delivery areas with the specified active status
     */
    List<DeliveryArea> findByIsActive(Boolean isActive);
}
