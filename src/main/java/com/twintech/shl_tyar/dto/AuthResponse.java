package com.twintech.shl_tyar.dto;

import com.twintech.shl_tyar.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String email;
    private String fullName;
    private Role role;
    private Long expiresIn;
}