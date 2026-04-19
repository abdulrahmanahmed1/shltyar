package com.twintech.shl_tyar.service;

import com.twintech.shl_tyar.domain.Role;
import com.twintech.shl_tyar.domain.User;
import com.twintech.shl_tyar.domain.UserStatus;
import com.twintech.shl_tyar.dto.SalesRegistrationRequest;
import com.twintech.shl_tyar.dto.SalesRegistrationResponse;
import com.twintech.shl_tyar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SalesRegistrationResponse registerSales(SalesRegistrationRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        }

        // Create new sales user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setBirthDate(request.getBirthDate());
        user.setRole(Role.SALES);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        log.info("Sales account created successfully for email: {}", user.getEmail());

        // Map to response
        SalesRegistrationResponse response = new SalesRegistrationResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setMessage("Sales account created successfully. You can now login with your credentials.");

        return response;
    }
}