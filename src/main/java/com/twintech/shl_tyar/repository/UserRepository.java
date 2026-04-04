package com.twintech.shl_tyar.repository;

import com.twintech.shl_tyar.domain.Role;
import com.twintech.shl_tyar.domain.User;
import com.twintech.shl_tyar.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom query methods for user management.
 * 
 * Requirements: 1.1, 1.2
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by email address.
     * Used for authentication and user lookup.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find all users with a specific role.
     * Used for role-based user listing and management.
     * 
     * @param role the role to filter by (ADMIN, SALES, RESTAURANT, DRIVER)
     * @return List of users with the specified role
     */
    List<User> findByRole(Role role);
    
    /**
     * Find all users with a specific status.
     * Used for account status management and filtering.
     * 
     * @param status the status to filter by (ACTIVE, SUSPENDED, LOCKED)
     * @return List of users with the specified status
     */
    List<User> findByStatus(UserStatus status);
}
