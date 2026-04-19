package com.twintech.shl_tyar.dto;

import com.twintech.shl_tyar.domain.UserStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DriverResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private LocalDate birthDate;
    private UserStatus status;
    private Boolean emailVerified;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}