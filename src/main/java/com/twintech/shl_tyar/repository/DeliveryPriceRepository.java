package com.twintech.shl_tyar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.twintech.shl_tyar.domain.DeliveryArea;
import com.twintech.shl_tyar.domain.DeliveryPrice;

/**
 * Repository interface for DeliveryPrice entity.
 * Provides CRUD operations and custom query methods for delivery price management.
 * 
 * Requirements: 7.1, 8.1
 */
@Repository
public interface DeliveryPriceRepository extends JpaRepository<DeliveryPrice, Long> {
    
    /**
     * Find delivery price by origin and destination areas.
     * Used for calculating delivery cost when creating orders.
     * Returns the price for delivering from origin area to destination area.
     * 
     * @param originArea the origin delivery area (restaurant location)
     * @param destinationArea the destination delivery area (customer location)
     * @return Optional containing the delivery price if found, empty otherwise
     */
    Optional<DeliveryPrice> findByOriginAreaAndDestinationArea(DeliveryArea originArea, DeliveryArea destinationArea);
    
    /**
     * Find delivery price by origin and destination area IDs.
     * Alternative method using area IDs instead of entity references.
     * 
     * @param originAreaId the ID of the origin delivery area
     * @param destinationAreaId the ID of the destination delivery area
     * @return Optional containing the delivery price if found, empty otherwise
     */
    @Query("SELECT dp FROM DeliveryPrice dp WHERE dp.originArea.id = :originAreaId AND dp.destinationArea.id = :destinationAreaId AND dp.deletedAt IS NULL")
    Optional<DeliveryPrice> findByOriginAndDestination(@Param("originAreaId") Long originAreaId, @Param("destinationAreaId") Long destinationAreaId);
    
    /**
     * Find all delivery prices originating from a specific area.
     * Used for viewing all delivery prices from a given origin area.
     * 
     * @param originArea the origin delivery area
     * @return List of delivery prices from the specified origin area
     */
    List<DeliveryPrice> findByOriginArea(DeliveryArea originArea);
    
    /**
     * Find all delivery prices by origin area ID.
     * Alternative method using area ID instead of entity reference.
     * 
     * @param originAreaId the ID of the origin delivery area
     * @return List of delivery prices from the specified origin area
     */
    @Query("SELECT dp FROM DeliveryPrice dp WHERE dp.originArea.id = :originAreaId AND dp.deletedAt IS NULL")
    List<DeliveryPrice> findByOriginAreaId(@Param("originAreaId") Long originAreaId);
    
    /**
     * Find all active delivery prices (not soft-deleted).
     * Used for listing all available delivery prices for admin management.
     * 
     * @return List of active delivery prices where isActive is true and deletedAt is null
     */
    @Query("SELECT dp FROM DeliveryPrice dp WHERE dp.isActive = true AND dp.deletedAt IS NULL")
    List<DeliveryPrice> findAllActive();
    
    /**
     * Find all delivery prices by active status.
     * Used for filtering prices by active/inactive status.
     * 
     * @param isActive the active status to filter by
     * @return List of delivery prices with the specified active status
     */
    List<DeliveryPrice> findByIsActive(Boolean isActive);
}
