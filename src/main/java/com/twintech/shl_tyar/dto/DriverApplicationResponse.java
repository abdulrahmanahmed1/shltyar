package com.twintech.shl_tyar.dto;

import com.twintech.shl_tyar.domain.ApplicationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DriverApplicationResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDate birthDate;
    private String nationalId;
    private String phone;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private String rejectionReason;
}