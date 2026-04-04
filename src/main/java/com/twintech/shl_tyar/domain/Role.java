package com.twintech.shl_tyar.domain;

/**
 * User role enumeration for role-based access control.
 * Defines the four main user types in the delivery platform.
 */
public enum Role {
    /**
     * System administrator with full platform control
     */
    ADMIN,
    
    /**
     * Sales representative who manages restaurant accounts and earns commissions
     */
    SALES,
    
    /**
     * Restaurant user who creates delivery orders
     */
    RESTAURANT,
    
    /**
     * Motorcycle driver who delivers orders
     */
    DRIVER
}
