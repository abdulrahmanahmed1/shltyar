package com.twintech.shl_tyar.domain;

/**
 * Driver application status enumeration.
 * Tracks the approval workflow for driver applications.
 * 
 * Requirements: 2.1, 2.2, 2.3
 */
public enum ApplicationStatus {
    /**
     * Application has been submitted and is awaiting admin review
     */
    PENDING,
    
    /**
     * Application has been reviewed and approved by admin
     */
    APPROVED,
    
    /**
     * Application has been reviewed and rejected by admin
     */
    REJECTED
}
