package com.twintech.shl_tyar.repository;

import com.twintech.shl_tyar.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DriverApplicationRepository.
 * Tests query methods for driver application management.
 * 
 * Requirements: 2.1, 2.2
 */
@SpringBootTest
@Transactional
class DriverApplicationRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverApplicationRepository driverApplicationRepository;

    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        driverApplicationRepository.deleteAll();
        userRepository.deleteAll();
        
        // Create test users
        testUser = new User();
        testUser.setEmail("driver@test.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setFullName("Test Driver");
        testUser.setPhone("1234567890");
        testUser.setRole(Role.DRIVER);
        testUser.setStatus(UserStatus.ACTIVE);
        testUser = userRepository.save(testUser);

        anotherUser = new User();
        anotherUser.setEmail("another@test.com");
        anotherUser.setPasswordHash("hashedpassword");
        anotherUser.setFullName("Another Driver");
        anotherUser.setPhone("0987654321");
        anotherUser.setRole(Role.DRIVER);
        anotherUser.setStatus(UserStatus.ACTIVE);
        anotherUser = userRepository.save(anotherUser);
    }

    @Test
    void findByUserId_shouldReturnApplicationsForUser() {
        // Given
        DriverApplication application = createDriverApplication(testUser, ApplicationStatus.PENDING);
        driverApplicationRepository.save(application);

        // When
        List<DriverApplication> result = driverApplicationRepository.findByUserId(testUser.getId());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findByUserId_shouldReturnEmptyListWhenNoApplications() {
        // When
        List<DriverApplication> result = driverApplicationRepository.findByUserId(testUser.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByStatus_shouldReturnApplicationsWithMatchingStatus() {
        // Given
        DriverApplication pendingApp = createDriverApplication(testUser, ApplicationStatus.PENDING);
        DriverApplication approvedApp = createDriverApplication(anotherUser, ApplicationStatus.APPROVED);
        driverApplicationRepository.save(pendingApp);
        driverApplicationRepository.save(approvedApp);

        // When
        List<DriverApplication> pendingResults = driverApplicationRepository.findByStatus(ApplicationStatus.PENDING);
        List<DriverApplication> approvedResults = driverApplicationRepository.findByStatus(ApplicationStatus.APPROVED);

        // Then
        assertThat(pendingResults).hasSize(1);
        assertThat(pendingResults.get(0).getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(approvedResults).hasSize(1);
        assertThat(approvedResults.get(0).getStatus()).isEqualTo(ApplicationStatus.APPROVED);
    }

    @Test
    void findByStatus_shouldReturnEmptyListWhenNoMatchingStatus() {
        // Given
        DriverApplication application = createDriverApplication(testUser, ApplicationStatus.PENDING);
        driverApplicationRepository.save(application);

        // When
        List<DriverApplication> result = driverApplicationRepository.findByStatus(ApplicationStatus.REJECTED);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByUser_shouldReturnApplicationForUser() {
        // Given
        DriverApplication application = createDriverApplication(testUser, ApplicationStatus.PENDING);
        driverApplicationRepository.save(application);

        // When
        Optional<DriverApplication> result = driverApplicationRepository.findByUser(testUser);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void findByUser_shouldReturnEmptyWhenNoApplication() {
        // When
        Optional<DriverApplication> result = driverApplicationRepository.findByUser(testUser);

        // Then
        assertThat(result).isEmpty();
    }

    private DriverApplication createDriverApplication(User user, ApplicationStatus status) {
        DriverApplication application = new DriverApplication();
        application.setUser(user);
        application.setNationalId("123456789");
        application.setDriverLicenseNumber("DL123456");
        application.setDriverLicenseExpiry(LocalDate.now().plusYears(2));
        application.setMotorcycleLicenseNumber("ML123456");
        application.setMotorcycleLicenseExpiry(LocalDate.now().plusYears(2));
        application.setMotorcyclePlateNumber("ABC-1234");
        application.setAddress("123 Test Street");
        application.setStatus(status);
        return application;
    }
}
