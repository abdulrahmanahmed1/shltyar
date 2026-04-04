package com.twintech.shl_tyar.domain;

/**
 * Enum representing the status of a restaurant account.
 * 
 * ACTIVE: Restaurant can create orders and use the platform
 * LOCKED: Restaurant is locked (typically due to expired subscription)
 * SUSPENDED: Restaurant is temporarily suspended by admin
 */
public enum RestaurantStatus {
    ACTIVE,
    LOCKED,
    SUSPENDED
}
