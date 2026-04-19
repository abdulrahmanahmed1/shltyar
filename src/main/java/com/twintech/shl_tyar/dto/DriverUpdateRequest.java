package com.twintech.shl_tyar.dto;

import com.twintech.shl_tyar.domain.UserStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DriverUpdateRequest {
    private String fullName;
    private String phone;
    private LocalDate birthDate;
    private UserStatus status;
    private Boolean emailVerified;
}