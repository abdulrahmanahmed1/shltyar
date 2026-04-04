package com.twintech.shl_tyar.repository;

import com.twintech.shl_tyar.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DriverDocumentRepository.
 * Tests query methods for driver document management.
 * 
 * Requirements: 2.1, 2.2
 */
@SpringBootTest
@Transactional
class DriverDocumentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverApplicationRepository driverApplicationRepository;

    @Autowired
    private DriverDocumentRepository driverDocumentRepository;

    private User testUser;
    private DriverApplication testApplication;
    private DriverApplication anotherApplication;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        driverDocumentRepository.deleteAll();
        driverApplicationRepository.deleteAll();
        userRepository.deleteAll();
        
        // Create test user
        testUser = new User();
        testUser.setEmail("driver@test.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setFullName("Test Driver");
        testUser.setPhone("1234567890");
        testUser.setRole(Role.DRIVER);
        testUser.setStatus(UserStatus.ACTIVE);
        testUser = userRepository.save(testUser);

        // Create test applications
        testApplication = createDriverApplication(testUser);
        testApplication = driverApplicationRepository.save(testApplication);

        anotherApplication = createDriverApplication(testUser);
        anotherApplication = driverApplicationRepository.save(anotherApplication);
    }

    @Test
    void findByApplicationId_shouldReturnDocumentsForApplication() {
        // Given
        DriverDocument doc1 = createDriverDocument(testApplication, DocumentType.NATIONAL_ID);
        DriverDocument doc2 = createDriverDocument(testApplication, DocumentType.DRIVER_LICENSE);
        driverDocumentRepository.save(doc1);
        driverDocumentRepository.save(doc2);

        // When
        List<DriverDocument> result = driverDocumentRepository.findByApplicationId(testApplication.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(DriverDocument::getDocumentType)
                .containsExactlyInAnyOrder(DocumentType.NATIONAL_ID, DocumentType.DRIVER_LICENSE);
    }

    @Test
    void findByApplicationId_shouldReturnEmptyListWhenNoDocuments() {
        // When
        List<DriverDocument> result = driverDocumentRepository.findByApplicationId(testApplication.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByApplication_shouldReturnDocumentsForApplication() {
        // Given
        DriverDocument doc1 = createDriverDocument(testApplication, DocumentType.MOTORCYCLE_LICENSE);
        DriverDocument doc2 = createDriverDocument(testApplication, DocumentType.MOTORCYCLE_PHOTO);
        driverDocumentRepository.save(doc1);
        driverDocumentRepository.save(doc2);

        // When
        List<DriverDocument> result = driverDocumentRepository.findByApplication(testApplication);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(DriverDocument::getDocumentType)
                .containsExactlyInAnyOrder(DocumentType.MOTORCYCLE_LICENSE, DocumentType.MOTORCYCLE_PHOTO);
    }

    @Test
    void findByApplication_shouldReturnEmptyListWhenNoDocuments() {
        // When
        List<DriverDocument> result = driverDocumentRepository.findByApplication(testApplication);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByApplicationId_shouldOnlyReturnDocumentsForSpecificApplication() {
        // Given
        DriverDocument doc1 = createDriverDocument(testApplication, DocumentType.NATIONAL_ID);
        DriverDocument doc2 = createDriverDocument(anotherApplication, DocumentType.DRIVER_LICENSE);
        driverDocumentRepository.save(doc1);
        driverDocumentRepository.save(doc2);

        // When
        List<DriverDocument> result = driverDocumentRepository.findByApplicationId(testApplication.getId());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getApplication().getId()).isEqualTo(testApplication.getId());
        assertThat(result.get(0).getDocumentType()).isEqualTo(DocumentType.NATIONAL_ID);
    }

    private DriverApplication createDriverApplication(User user) {
        DriverApplication application = new DriverApplication();
        application.setUser(user);
        application.setNationalId("123456789");
        application.setDriverLicenseNumber("DL123456");
        application.setDriverLicenseExpiry(LocalDate.now().plusYears(2));
        application.setMotorcycleLicenseNumber("ML123456");
        application.setMotorcycleLicenseExpiry(LocalDate.now().plusYears(2));
        application.setMotorcyclePlateNumber("ABC-1234");
        application.setAddress("123 Test Street");
        application.setStatus(ApplicationStatus.PENDING);
        return application;
    }

    private DriverDocument createDriverDocument(DriverApplication application, DocumentType documentType) {
        DriverDocument document = new DriverDocument();
        document.setApplication(application);
        document.setDocumentType(documentType);
        document.setFilePath("/uploads/test-file.jpg");
        document.setFileName("test-file.jpg");
        document.setFileSize(1024L);
        document.setMimeType("image/jpeg");
        document.setUploadedBy(testUser);
        return document;
    }
}
