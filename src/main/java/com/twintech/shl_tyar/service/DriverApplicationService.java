package com.twintech.shl_tyar.service;

import com.twintech.shl_tyar.domain.*;
import com.twintech.shl_tyar.dto.DriverApplicationRequest;
import com.twintech.shl_tyar.dto.DriverApplicationResponse;
import com.twintech.shl_tyar.repository.DriverApplicationRepository;
import com.twintech.shl_tyar.repository.DriverDocumentRepository;
import com.twintech.shl_tyar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverApplicationService {

    private final DriverApplicationRepository driverApplicationRepository;
    private final DriverDocumentRepository driverDocumentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public DriverApplicationResponse submitApplication(DriverApplicationRequest request) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        User user;
        
        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Check if user already has an application
            if (driverApplicationRepository.findByUser(user).isPresent()) {
                throw new RuntimeException("Driver application already exists for this email");
            }
        } else {
            // Create new user account as part of the application process
            user = new User();
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getName());
            user.setPhone(request.getPhone());
            user.setBirthDate(request.getBirthDate());
            user.setRole(Role.DRIVER);
            user.setStatus(UserStatus.ACTIVE);
            user.setEmailVerified(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
        }

        // Create driver application
        DriverApplication application = new DriverApplication();
        application.setUser(user);
        application.setNationalId(request.getNationalId());
        application.setAddress(request.getAddress());
        application.setEmergencyContactName(request.getEmergencyContactName());
        application.setEmergencyContactPhone(request.getEmergencyContactPhone());
        application.setStatus(ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        // For now, set dummy values for required fields that aren't in the form
        application.setDriverLicenseNumber("PENDING_REVIEW");
        application.setDriverLicenseExpiry(request.getBirthDate().plusYears(50)); // Dummy expiry
        application.setMotorcycleLicenseNumber("PENDING_REVIEW");
        application.setMotorcycleLicenseExpiry(request.getBirthDate().plusYears(50)); // Dummy expiry
        application.setMotorcyclePlateNumber("PENDING_REVIEW");

        application = driverApplicationRepository.save(application);

        // Store documents
        try {
            // Store National ID front image
            String nationalIdFrontPath = fileStorageService.storeFile(
                    request.getNationalIdFrontImage(), 
                    "driver-documents/" + application.getId()
            );
            saveDocument(application, DocumentType.NATIONAL_ID, 
                    request.getNationalIdFrontImage(), nationalIdFrontPath, user);

            // Store National ID back image (we'll use NATIONAL_ID type for both)
            String nationalIdBackPath = fileStorageService.storeFile(
                    request.getNationalIdBackImage(), 
                    "driver-documents/" + application.getId()
            );
            saveDocument(application, DocumentType.NATIONAL_ID, 
                    request.getNationalIdBackImage(), nationalIdBackPath, user);

            // Store Driving License image
            String drivingLicensePath = fileStorageService.storeFile(
                    request.getDrivingLicenseImage(), 
                    "driver-documents/" + application.getId()
            );
            saveDocument(application, DocumentType.DRIVER_LICENSE, 
                    request.getDrivingLicenseImage(), drivingLicensePath, user);

        } catch (Exception e) {
            log.error("Error storing documents for application {}: {}", application.getId(), e.getMessage());
            throw new RuntimeException("Failed to store documents: " + e.getMessage());
        }

        return mapToResponse(application);
    }

    private void saveDocument(DriverApplication application, DocumentType documentType, 
                            org.springframework.web.multipart.MultipartFile file, 
                            String filePath, User user) {
        DriverDocument document = new DriverDocument();
        document.setApplication(application);
        document.setDocumentType(documentType);
        document.setFilePath(filePath);
        document.setFileName(file.getOriginalFilename());
        document.setFileSize(file.getSize());
        document.setMimeType(file.getContentType());
        document.setUploadedBy(user);
        document.setCreatedAt(LocalDateTime.now());

        driverDocumentRepository.save(document);
    }

    public DriverApplicationResponse getMyApplication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DriverApplication application = driverApplicationRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No driver application found"));

        return mapToResponse(application);
    }

    public List<DriverApplicationResponse> getAllApplications() {
        return driverApplicationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<DriverApplicationResponse> getApplicationsByStatus(ApplicationStatus status) {
        return driverApplicationRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DriverApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatus status, String rejectionReason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User reviewer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        DriverApplication application = driverApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(status);
        application.setReviewedBy(reviewer);
        application.setReviewedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());
        
        if (status == ApplicationStatus.REJECTED && rejectionReason != null) {
            application.setRejectionReason(rejectionReason);
        }

        application = driverApplicationRepository.save(application);
        return mapToResponse(application);
    }

    private DriverApplicationResponse mapToResponse(DriverApplication application) {
        DriverApplicationResponse response = new DriverApplicationResponse();
        response.setId(application.getId());
        response.setUserId(application.getUser().getId());
        response.setName(application.getUser().getFullName());
        response.setEmail(application.getUser().getEmail());
        response.setBirthDate(application.getUser().getBirthDate());
        response.setNationalId(application.getNationalId());
        response.setPhone(application.getUser().getPhone());
        response.setAddress(application.getAddress());
        response.setEmergencyContactName(application.getEmergencyContactName());
        response.setEmergencyContactPhone(application.getEmergencyContactPhone());
        response.setStatus(application.getStatus());
        response.setCreatedAt(application.getCreatedAt());
        response.setReviewedAt(application.getReviewedAt());
        response.setRejectionReason(application.getRejectionReason());
        return response;
    }
}