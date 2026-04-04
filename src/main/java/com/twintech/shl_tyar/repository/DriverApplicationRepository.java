package com.twintech.shl_tyar.repository;

import com.twintech.shl_tyar.domain.ApplicationStatus;
import com.twintech.shl_tyar.domain.DriverApplication;
import com.twintech.shl_tyar.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DriverApplication entity.
 * Provides CRUD operations and custom query methods for driver application management.
 * 
 * Requirements: 2.1, 2.2
 */
@Repository
public interface DriverApplicationRepository extends JpaRepository<DriverApplication, Long> {
    
    /**
     * Find a driver application by user ID.
     * Used to retrieve applications submitted by a specific user.
     * 
     * @param user the user who submitted the application
     * @return Optional containing the driver application if found, empty otherwise
     */
    Optional<DriverApplication> findByUser(User user);
    
    /**
     * Find all driver applications by user ID.
     * Used to retrieve all applications submitted by a specific user.
     * 
     * @param userId the ID of the user who submitted the applications
     * @return List of driver applications for the specified user
     */
    List<DriverApplication> findByUserId(Long userId);
    
    /**
     * Find all driver applications with a specific status.
     * Used for filtering applications by approval status (PENDING, APPROVED, REJECTED).
     * 
     * @param status the application status to filter by
     * @return List of driver applications with the specified status
     */
    List<DriverApplication> findByStatus(ApplicationStatus status);
}
