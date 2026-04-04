package com.twintech.shl_tyar.domain;

/**
 * Driver document type enumeration.
 * Defines the types of documents required for driver application.
 * 
 * Requirements: 2.1, 2.2
 */
public enum DocumentType {
    /**
     * National identification document
     */
    NATIONAL_ID,
    
    /**
     * Driver's license document
     */
    DRIVER_LICENSE,
    
    /**
     * Motorcycle license document
     */
    MOTORCYCLE_LICENSE,
    
    /**
     * Photo of the motorcycle
     */
    MOTORCYCLE_PHOTO
}
