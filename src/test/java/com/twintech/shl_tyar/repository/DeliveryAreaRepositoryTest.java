package com.twintech.shl_tyar.repository;

import com.twintech.shl_tyar.domain.DeliveryArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DeliveryAreaRepository.
 * Tests query methods for delivery area management.
 * 
 * Requirements: 7.1, 8.1
 */
@SpringBootTest
@Transactional
class DeliveryAreaRepositoryTest {

    @Autowired
    private DeliveryAreaRepository deliveryAreaRepository;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        deliveryAreaRepository.deleteAll();
    }

    @Test
    void findByName_shouldReturnAreaWhenExists() {
        // Given
        DeliveryArea area = createDeliveryArea("Downtown", true);
        deliveryAreaRepository.save(area);

        // When
        Optional<DeliveryArea> result = deliveryAreaRepository.findByName("Downtown");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Downtown");
    }

    @Test
    void findByName_shouldReturnEmptyWhenNotExists() {
        // When
        Optional<DeliveryArea> result = deliveryAreaRepository.findByName("NonExistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findAllActive_shouldReturnOnlyActiveAndNotDeletedAreas() {
        // Given
        DeliveryArea activeArea1 = createDeliveryArea("Area1", true);
        DeliveryArea activeArea2 = createDeliveryArea("Area2", true);
        DeliveryArea inactiveArea = createDeliveryArea("Area3", false);
        DeliveryArea deletedArea = createDeliveryArea("Area4", true);
        deletedArea.setDeletedAt(LocalDateTime.now());

        deliveryAreaRepository.save(activeArea1);
        deliveryAreaRepository.save(activeArea2);
        deliveryAreaRepository.save(inactiveArea);
        deliveryAreaRepository.save(deletedArea);

        // When
        List<DeliveryArea> result = deliveryAreaRepository.findAllActive();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(DeliveryArea::getName)
            .containsExactlyInAnyOrder("Area1", "Area2");
    }

    @Test
    void findAllActive_shouldReturnEmptyListWhenNoActiveAreas() {
        // Given
        DeliveryArea inactiveArea = createDeliveryArea("Inactive", false);
        deliveryAreaRepository.save(inactiveArea);

        // When
        List<DeliveryArea> result = deliveryAreaRepository.findAllActive();

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByIsActive_shouldReturnAreasWithMatchingStatus() {
        // Given
        DeliveryArea activeArea = createDeliveryArea("Active", true);
        DeliveryArea inactiveArea = createDeliveryArea("Inactive", false);
        deliveryAreaRepository.save(activeArea);
        deliveryAreaRepository.save(inactiveArea);

        // When
        List<DeliveryArea> activeResults = deliveryAreaRepository.findByIsActive(true);
        List<DeliveryArea> inactiveResults = deliveryAreaRepository.findByIsActive(false);

        // Then
        assertThat(activeResults).hasSize(1);
        assertThat(activeResults.get(0).getName()).isEqualTo("Active");
        assertThat(inactiveResults).hasSize(1);
        assertThat(inactiveResults.get(0).getName()).isEqualTo("Inactive");
    }

    @Test
    void findByIsActive_shouldReturnEmptyListWhenNoMatchingStatus() {
        // Given
        DeliveryArea activeArea = createDeliveryArea("Active", true);
        deliveryAreaRepository.save(activeArea);

        // When
        List<DeliveryArea> result = deliveryAreaRepository.findByIsActive(false);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldPersistDeliveryArea() {
        // Given
        DeliveryArea area = createDeliveryArea("TestArea", true);

        // When
        DeliveryArea saved = deliveryAreaRepository.save(area);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("TestArea");
        assertThat(saved.getIsActive()).isTrue();
    }

    private DeliveryArea createDeliveryArea(String name, boolean isActive) {
        DeliveryArea area = new DeliveryArea();
        area.setName(name);
        area.setDescription("Test description for " + name);
        area.setBoundaries("{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[1,0],[1,1],[0,1],[0,0]]]}");
        area.setIsActive(isActive);
        return area;
    }
}
