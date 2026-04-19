package com.twintech.shl_tyar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class DriverApplicationRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    
    @NotBlank(message = "National ID is required")
    private String nationalId;
    
    @NotNull(message = "National ID front image is required")
    private MultipartFile nationalIdFrontImage;
    
    @NotNull(message = "National ID back image is required")
    private MultipartFile nationalIdBackImage;
    
    @NotNull(message = "Driving license image is required")
    private MultipartFile drivingLicenseImage;
    
    private String phone;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
}