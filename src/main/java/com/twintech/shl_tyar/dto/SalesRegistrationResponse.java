package com.twintech.shl_tyar.dto;

import com.twintech.shl_tyar.domain.Role;
import com.twintech.shl_tyar.domain.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SalesRegistrationResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private String message;
}