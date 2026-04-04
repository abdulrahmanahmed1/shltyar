package com.twintech.shl_tyar.repository;

import com.twintech.shl_tyar.domain.DriverApplication;
import com.twintech.shl_tyar.domain.DriverDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for DriverDocument entity.
 * Provides CRUD operations and custom query methods for driver document management.
 * 
 * Requirements: 2.1, 2.2
 */
@Repository
public interface DriverDocumentRepository extends JpaRepository<DriverDocument, Long> {
    
    /**
     * Find all documents associated with a specific driver application.
     * Used to retrieve all uploaded documents for a given application.
     * 
     * @param application the driver application to find documents for
     * @return List of driver documents for the specified application
     */
    List<DriverDocument> findByApplication(DriverApplication application);
    
    /**
     * Find all documents by application ID.
     * Used to retrieve all uploaded documents for a given application ID.
     * 
     * @param applicationId the ID of the driver application
     * @return List of driver documents for the specified application ID
     */
    List<DriverDocument> findByApplicationId(Long applicationId);
}
