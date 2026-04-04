package com.twintech.shl_tyar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.twintech.shl_tyar.domain.RestaurantBranch;

/**
 * Repository interface for RestaurantBranch entity.
 * 
 * Requirements: 10.1, 10.2
 */
@Repository
public interface RestaurantBranchRepository extends JpaRepository<RestaurantBranch, Long> {

    /**
     * Find all branches belonging to a specific brand.
     * 
     * @param brandId the brand restaurant ID
     * @return list of branches
     */
    List<RestaurantBranch> findByBrandId(Long brandId);

    /**
     * Find all active branches for a brand.
     * 
     * @param brandId the brand restaurant ID
     * @param isActive the active status
     * @return list of active branches
     */
    List<RestaurantBranch> findByBrandIdAndIsActive(Long brandId, Boolean isActive);

    /**
     * Find branches by delivery area.
     * 
     * @param deliveryAreaId the delivery area ID
     * @return list of branches in the delivery area
     */
    List<RestaurantBranch> findByDeliveryAreaId(Long deliveryAreaId);

    /**
     * Find all branches where deleted_at is null (not soft deleted).
     * 
     * @return list of non-deleted branches
     */
    List<RestaurantBranch> findByDeletedAtIsNull();
}
