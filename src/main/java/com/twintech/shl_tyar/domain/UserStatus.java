package com.twintech.shl_tyar.domain;

/**
 * User account status enumeration.
 * Controls user access to the platform.
 */
public enum UserStatus {
    /**
     * User account is active and can access the platform
     */
    ACTIVE,
    
    /**
     * User account is temporarily suspended (e.g., for policy violations)
     */
    SUSPENDED,
    
    /**
     * User account is locked (e.g., expired subscription for restaurants)
     */
    LOCKED
}
