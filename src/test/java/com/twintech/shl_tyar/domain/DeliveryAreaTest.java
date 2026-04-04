package com.twintech.shl_tyar.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DeliveryArea entity.
 * Tests entity behavior and business logic methods.
 * 
 * Requirements: 7.1, 7.2, 8.1
 */
class DeliveryAreaTest {

    @Test
    void isActiveAndNotDeleted_shouldReturnTrueWhenActiveAndNotDeleted() {
        // Given
        DeliveryArea area = new DeliveryArea();
        area.setIsActive(true);
        area.setDeletedAt(null);

        // When
        boolean result = area.isActiveAndNotDeleted();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isActiveAndNotDeleted_shouldReturnFalseWhenInactive() {
        // Given
        DeliveryArea area = new DeliveryArea();
        area.setIsActive(false);
        area.setDeletedAt(null);

        // When
        boolean result = area.isActiveAndNotDeleted();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isActiveAndNotDeleted_shouldReturnFalseWhenDeleted() {
        // Given
        DeliveryArea area = new DeliveryArea();
        area.setIsActive(true);
        area.setDeletedAt(LocalDateTime.now());

        // When
        boolean result = area.isActiveAndNotDeleted();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isDeleted_shouldReturnTrueWhenDeletedAtIsSet() {
        // Given
        DeliveryArea area = new DeliveryArea();
        area.setDeletedAt(LocalDateTime.now());

        // When
        boolean result = area.isDeleted();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isDeleted_shouldReturnFalseWhenDeletedAtIsNull() {
        // Given
        DeliveryArea area = new DeliveryArea();
        area.setDeletedAt(null);

        // When
        boolean result = area.isDeleted();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void boundaries_shouldStoreJsonString() {
        // Given
        DeliveryArea area = new DeliveryArea();
        String geoJson = "{\"type\":\"Polygon\",\"coordinates\":[[[30.0,10.0],[40.0,40.0],[20.0,40.0],[10.0,20.0],[30.0,10.0]]]}";

        // When
        area.setBoundaries(geoJson);

        // Then
        assertThat(area.getBoundaries()).isEqualTo(geoJson);
    }

    @Test
    void defaultValues_shouldBeSetCorrectly() {
        // Given & When
        DeliveryArea area = new DeliveryArea();

        // Then
        assertThat(area.getIsActive()).isTrue();
        assertThat(area.getDeletedAt()).isNull();
    }
}
