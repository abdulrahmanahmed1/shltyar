package com.twintech.shl_tyar.repository;

import com.twintech.shl_tyar.domain.DeliveryArea;
import com.twintech.shl_tyar.domain.DeliveryPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DeliveryPriceRepository.
 * Tests query methods for delivery price management.
 * 
 * Requirements: 7.1, 8.1
 */
@SpringBootTest
@Transactional
class DeliveryPriceRepositoryTest {

    @Autowired
    private DeliveryPriceRepository deliveryPriceRepository;

    @Autowired
    private DeliveryAreaRepository deliveryAreaRepository;

    private DeliveryArea areaA;
    private DeliveryArea areaB;
    private DeliveryArea areaC;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        deliveryPriceRepository.deleteAll();
        deliveryAreaRepository.deleteAll();

        // Create test delivery areas
        areaA = createDeliveryArea("Area A");
        areaB = createDeliveryArea("Area B");
        areaC = createDeliveryArea("Area C");

        areaA = deliveryAreaRepository.save(areaA);
        areaB = deliveryAreaRepository.save(areaB);
        areaC = deliveryAreaRepository.save(areaC);
    }

    @Test
    void findByOriginAreaAndDestinationArea_shouldReturnPriceWhenExists() {
        // Given
        DeliveryPrice price = createDeliveryPrice(areaA, areaB, new BigDecimal("10.00"));
        deliveryPriceRepository.save(price);

        // When
        Optional<DeliveryPrice> result = deliveryPriceRepository.findByOriginAreaAndDestinationArea(areaA, areaB);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getOriginArea().getId()).isEqualTo(areaA.getId());
        assertThat(result.get().getDestinationArea().getId()).isEqualTo(areaB.getId());
        assertThat(result.get().getPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void findByOriginAreaAndDestinationArea_shouldReturnEmptyWhenNotExists() {
        // When
        Optional<DeliveryPrice> result = deliveryPriceRepository.findByOriginAreaAndDestinationArea(areaA, areaB);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByOriginAndDestination_shouldReturnPriceByIds() {
        // Given
        DeliveryPrice price = createDeliveryPrice(areaA, areaB, new BigDecimal("15.00"));
        deliveryPriceRepository.save(price);

        // When
        Optional<DeliveryPrice> result = deliveryPriceRepository.findByOriginAndDestination(areaA.getId(), areaB.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPrice()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void findByOriginAndDestination_shouldNotReturnDeletedPrices() {
        // Given
        DeliveryPrice price = createDeliveryPrice(areaA, areaB, new BigDecimal("10.00"));
        price.setDeletedAt(LocalDateTime.now());
        deliveryPriceRepository.save(price);

        // When
        Optional<DeliveryPrice> result = deliveryPriceRepository.findByOriginAndDestination(areaA.getId(), areaB.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByOriginArea_shouldReturnAllPricesFromOrigin() {
        // Given
        DeliveryPrice priceAB = createDeliveryPrice(areaA, areaB, new BigDecimal("10.00"));
        DeliveryPrice priceAC = createDeliveryPrice(areaA, areaC, new BigDecimal("15.00"));
        DeliveryPrice priceBA = createDeliveryPrice(areaB, areaA, new BigDecimal("12.00"));

        deliveryPriceRepository.save(priceAB);
        deliveryPriceRepository.save(priceAC);
        deliveryPriceRepository.save(priceBA); // Different origin, should not be included

        // When
        List<DeliveryPrice> result = deliveryPriceRepository.findByOriginArea(areaA);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getOriginArea().getId().equals(areaA.getId()));
    }

    @Test
    void findByOriginArea_shouldReturnEmptyListWhenNoPrices() {
        // When
        List<DeliveryPrice> result = deliveryPriceRepository.findByOriginArea(areaA);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByOriginAreaId_shouldReturnPricesByOriginId() {
        // Given
        DeliveryPrice priceAB = createDeliveryPrice(areaA, areaB, new BigDecimal("10.00"));
        DeliveryPrice priceAC = createDeliveryPrice(areaA, areaC, new BigDecimal("15.00"));
        deliveryPriceRepository.save(priceAB);
        deliveryPriceRepository.save(priceAC);

        // When
        List<DeliveryPrice> result = deliveryPriceRepository.findByOriginAreaId(areaA.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(p -> p.getPrice())
            .containsExactlyInAnyOrder(new BigDecimal("10.00"), new BigDecimal("15.00"));
    }

    @Test
    void findByOriginAreaId_shouldNotReturnDeletedPrices() {
        // Given
        DeliveryPrice activePrice = createDeliveryPrice(areaA, areaB, new BigDecimal("10.00"));
        DeliveryPrice deletedPrice = createDeliveryPrice(areaA, areaC, new BigDecimal("15.00"));
        deletedPrice.setDeletedAt(LocalDateTime.now());

        deliveryPriceRepository.save(activePrice);
        deliveryPriceRepository.save(deletedPrice);

        // When
        List<DeliveryPrice> result = deliveryPriceRepository.findByOriginAreaId(areaA.getId());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void findAllActive_shouldReturnOnlyActiveAndNotDeletedPrices() {
        // Given
        DeliveryPrice activePrice1 = createDeliveryPrice(areaA, areaB, new BigDecimal("10.00"));
        DeliveryPrice activePrice2 = createDeliveryPrice(areaB, areaC, new BigDecimal("12.00"));
        DeliveryPrice inactivePrice = createDeliveryPrice(areaA, areaC, new BigDecimal("15.00"));
        inactivePrice.setIsActive(false);
        DeliveryPrice deletedPrice = createDeliveryPrice(areaB, areaA, new BigDecimal("11.00"));
        deletedPrice.setDeletedAt(LocalDateTime.now());

        deliveryPriceRepository.save(activePrice1);
        deliveryPriceRepository.save(activePrice2);
        deliveryPriceRepository.save(inactivePrice);
        deliveryPriceRepository.save(deletedPrice);

        // When
        List<DeliveryPrice> result = deliveryPriceRepository.findAllActive();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(p -> p.getPrice())
            .containsExactlyInAnyOrder(new BigDecimal("10.00"), new BigDecimal("12.00"));
    }

    @Test
    void findAllActive_shouldReturnEmptyListWhenNoActivePrices() {
        // Given
        DeliveryPrice inactivePrice = createDeliveryPrice(areaA, areaB, new BigDecimal("10.00"));
        inactivePrice.setIsActive(false);
        deliveryPriceRepository.save(inactivePrice);

        // When
        List<DeliveryPrice> result = deliveryPriceRepository.findAllActive();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByIsActive_shouldReturnPricesWithMatchingStatus() {
        // Given
        DeliveryPrice activePrice = createDeliveryPrice(areaA, areaB, new BigDecimal("10.00"));
        DeliveryPrice inactivePrice = createDeliveryPrice(areaB, areaC, new BigDecimal("12.00"));
        inactivePrice.setIsActive(false);

        deliveryPriceRepository.save(activePrice);
        deliveryPriceRepository.save(inactivePrice);

        // When
        List<DeliveryPrice> activeResults = deliveryPriceRepository.findByIsActive(true);
        List<DeliveryPrice> inactiveResults = deliveryPriceRepository.findByIsActive(false);

        // Then
        assertThat(activeResults).hasSize(1);
        assertThat(activeResults.get(0).getPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(inactiveResults).hasSize(1);
        assertThat(inactiveResults.get(0).getPrice()).isEqualByComparingTo(new BigDecimal("12.00"));
    }

    @Test
    void save_shouldPersistDeliveryPrice() {
        // Given
        DeliveryPrice price = createDeliveryPrice(areaA, areaB, new BigDecimal("20.00"));

        // When
        DeliveryPrice saved = deliveryPriceRepository.save(price);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPrice()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(saved.getOriginArea().getId()).isEqualTo(areaA.getId());
        assertThat(saved.getDestinationArea().getId()).isEqualTo(areaB.getId());
    }

    @Test
    void bidirectionalPricing_shouldSupportDifferentPricesForEachDirection() {
        // Given - Area A to B costs $10, but B to A costs $12
        DeliveryPrice priceAtoB = createDeliveryPrice(areaA, areaB, new BigDecimal("10.00"));
        DeliveryPrice priceBtoA = createDeliveryPrice(areaB, areaA, new BigDecimal("12.00"));

        deliveryPriceRepository.save(priceAtoB);
        deliveryPriceRepository.save(priceBtoA);

        // When
        Optional<DeliveryPrice> resultAtoB = deliveryPriceRepository.findByOriginAndDestination(areaA.getId(), areaB.getId());
        Optional<DeliveryPrice> resultBtoA = deliveryPriceRepository.findByOriginAndDestination(areaB.getId(), areaA.getId());

        // Then
        assertThat(resultAtoB).isPresent();
        assertThat(resultAtoB.get().getPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(resultBtoA).isPresent();
        assertThat(resultBtoA.get().getPrice()).isEqualByComparingTo(new BigDecimal("12.00"));
    }

    private DeliveryArea createDeliveryArea(String name) {
        DeliveryArea area = new DeliveryArea();
        area.setName(name);
        area.setDescription("Test area: " + name);
        area.setBoundaries("{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[1,0],[1,1],[0,1],[0,0]]]}");
        area.setIsActive(true);
        return area;
    }

    private DeliveryPrice createDeliveryPrice(DeliveryArea origin, DeliveryArea destination, BigDecimal price) {
        DeliveryPrice deliveryPrice = new DeliveryPrice();
        deliveryPrice.setOriginArea(origin);
        deliveryPrice.setDestinationArea(destination);
        deliveryPrice.setPrice(price);
        deliveryPrice.setCurrency("USD");
        deliveryPrice.setIsActive(true);
        return deliveryPrice;
    }
}
