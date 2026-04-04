package com.twintech.shl_tyar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.twintech.shl_tyar.domain.Restaurant;
import com.twintech.shl_tyar.domain.RestaurantStatus;

/**
 * Repository interface for Restaurant entity.
 * 
 * Requirements: 9.1, 10.1, 13.1
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Find restaurant by user ID.
     * 
     * @param userId the user ID
     * @return Optional containing the restaurant if found
     */
    Optional<Restaurant> findByUserId(Long userId);

    /**
     * Find all restaurants managed by a specific sales representative.
     * 
     * @param salesRepId the sales representative user ID
     * @return list of restaurants
     */
    List<Restaurant> findBySalesRepId(Long salesRepId);

    /**
     * Find all branches of a parent brand.
     * 
     * @param parentBrandId the parent brand ID
     * @return list of branch restaurants
     */
    List<Restaurant> findByParentBrandId(Long parentBrandId);

    /**
     * Find restaurants by status.
     * 
     * @param status the restaurant status
     * @return list of restaurants with the given status
     */
    List<Restaurant> findByStatus(RestaurantStatus status);

    /**
     * Find all restaurants where deleted_at is null (not soft deleted).
     * 
     * @return list of active restaurants
     */
    List<Restaurant> findByDeletedAtIsNull();
}
