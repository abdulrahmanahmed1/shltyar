package com.twintech.shl_tyar.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DeliveryPrice entity.
 * Tests entity behavior and business logic methods.
 * 
 * Requirements: 7.3, 8.1, 8.2, 8.3
 */
class DeliveryPriceTest {

    @Test
    void isActiveAndNotDeleted_shouldReturnTrueWhenActiveAndNotDeleted() {
        // Given
        DeliveryPrice price = new DeliveryPrice();
        price.setIsActive(true);
        price.setDeletedAt(null);

        // When
        boolean result = price.isActiveAndNotDeleted();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isActiveAndNotDeleted_shouldReturnFalseWhenInactive() {
        // Given
        DeliveryPrice price = new DeliveryPrice();
        price.setIsActive(false);
        price.setDeletedAt(null);

        // When
        boolean result = price.isActiveAndNotDeleted();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isActiveAndNotDeleted_shouldReturnFalseWhenDeleted() {
        // Given
        DeliveryPrice price = new DeliveryPrice();
        price.setIsActive(true);
        price.setDeletedAt(LocalDateTime.now());

        // When
        boolean result = price.isActiveAndNotDeleted();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isDeleted_shouldReturnTrueWhenDeletedAtIsSet() {
        // Given
        DeliveryPrice price = new DeliveryPrice();
        price.setDeletedAt(LocalDateTime.now());

        // When
        boolean result = price.isDeleted();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isDeleted_shouldReturnFalseWhenDeletedAtIsNull() {
        // Given
        DeliveryPrice price = new DeliveryPrice();
        price.setDeletedAt(null);

        // When
        boolean result = price.isDeleted();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isPriceValid_shouldReturnTrueForPositivePrice() {
        // Given
        DeliveryPrice price = new DeliveryPrice();
        price.setPrice(new BigDecimal("25.50"));

        // When
        boolean result = price.isPriceValid();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isPriceValid_shouldReturnTrueForZeroPrice() {
        // Given
        DeliveryPrice price = new DeliveryPrice();
        price.setPrice(BigDecimal.ZERO);

        // When
        boolean result = price.isPriceValid();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isPriceValid_shouldReturnFalseForNegativePrice() {
        // Given
        DeliveryPrice price = new DeliveryPrice();
        price.setPrice(new BigDecimal("-10.00"));

        // When
        boolean result = price.isPriceValid();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isPriceValid_shouldReturnFalseForNullPrice() {
        // Given
        DeliveryPrice price = new DeliveryPrice();
        price.setPrice(null);

        // When
        boolean result = price.isPriceValid();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void defaultValues_shouldBeSetCorrectly() {
        // Given & When
        DeliveryPrice price = new DeliveryPrice();

        // Then
        assertThat(price.getIsActive()).isTrue();
        assertThat(price.getCurrency()).isEqualTo("USD");
        assertThat(price.getDeletedAt()).isNull();
    }

    @Test
    void originAndDestinationAreas_shouldBeSetCorrectly() {
        // Given
        DeliveryArea originArea = new DeliveryArea();
        originArea.setId(1L);
        originArea.setName("Area A");

        DeliveryArea destinationArea = new DeliveryArea();
        destinationArea.setId(2L);
        destinationArea.setName("Area B");

        DeliveryPrice price = new DeliveryPrice();

        // When
        price.setOriginArea(originArea);
        price.setDestinationArea(destinationArea);
        price.setPrice(new BigDecimal("30.00"));

        // Then
        assertThat(price.getOriginArea()).isEqualTo(originArea);
        assertThat(price.getDestinationArea()).isEqualTo(destinationArea);
        assertThat(price.getPrice()).isEqualByComparingTo(new BigDecimal("30.00"));
    }
}
