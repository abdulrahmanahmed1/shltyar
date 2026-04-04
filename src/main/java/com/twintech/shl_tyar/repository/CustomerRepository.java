package com.twintech.shl_tyar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.twintech.shl_tyar.domain.Customer;

/**
 * Repository interface for Customer entity.
 * 
 * Requirements: 13.1, 13.2, 13.3, 13.4
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find all customers belonging to a specific restaurant.
     * 
     * @param restaurantId the restaurant ID
     * @return list of customers
     */
    List<Customer> findByRestaurantId(Long restaurantId);

    /**
     * Find customers by restaurant ID where deleted_at is null.
     * 
     * @param restaurantId the restaurant ID
     * @return list of non-deleted customers
     */
    List<Customer> findByRestaurantIdAndDeletedAtIsNull(Long restaurantId);

    /**
     * Search customers by phone number (partial match).
     * 
     * @param restaurantId the restaurant ID
     * @param phone the phone number to search
     * @return list of matching customers
     */
    @Query("SELECT c FROM Customer c WHERE c.restaurantId = :restaurantId AND c.phone LIKE %:phone% AND c.deletedAt IS NULL")
    List<Customer> searchByPhone(@Param("restaurantId") Long restaurantId, @Param("phone") String phone);

    /**
     * Search customers by name (partial match).
     * 
     * @param restaurantId the restaurant ID
     * @param name the name to search
     * @return list of matching customers
     */
    @Query("SELECT c FROM Customer c WHERE c.restaurantId = :restaurantId AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.deletedAt IS NULL")
    List<Customer> searchByName(@Param("restaurantId") Long restaurantId, @Param("name") String name);

    /**
     * Search customers by address (partial match).
     * 
     * @param restaurantId the restaurant ID
     * @param address the address to search
     * @return list of matching customers
     */
    @Query("SELECT c FROM Customer c WHERE c.restaurantId = :restaurantId AND LOWER(c.address) LIKE LOWER(CONCAT('%', :address, '%')) AND c.deletedAt IS NULL")
    List<Customer> searchByAddress(@Param("restaurantId") Long restaurantId, @Param("address") String address);
}
