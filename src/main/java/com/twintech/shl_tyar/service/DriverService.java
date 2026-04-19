package com.twintech.shl_tyar.service;

import com.twintech.shl_tyar.domain.Role;
import com.twintech.shl_tyar.domain.User;
import com.twintech.shl_tyar.dto.DriverResponse;
import com.twintech.shl_tyar.dto.DriverUpdateRequest;
import com.twintech.shl_tyar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverService {

    private final UserRepository userRepository;

    public List<DriverResponse> getAllDrivers() {
        return userRepository.findByRole(Role.DRIVER).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DriverResponse getDriverById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        
        if (user.getRole() != Role.DRIVER) {
            throw new RuntimeException("User is not a driver");
        }
        
        return mapToResponse(user);
    }

    public DriverResponse getDriverByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found with email: " + email));
        
        if (user.getRole() != Role.DRIVER) {
            throw new RuntimeException("User is not a driver");
        }
        
        return mapToResponse(user);
    }

    @Transactional
    public DriverResponse updateDriver(Long id, DriverUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        
        if (user.getRole() != Role.DRIVER) {
            throw new RuntimeException("User is not a driver");
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getEmailVerified() != null) {
            user.setEmailVerified(request.getEmailVerified());
        }

        user.setUpdatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        log.info("Driver updated successfully: {}", user.getEmail());
        return mapToResponse(user);
    }

    @Transactional
    public void deleteDriver(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        
        if (user.getRole() != Role.DRIVER) {
            throw new RuntimeException("User is not a driver");
        }

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Driver soft deleted: {}", user.getEmail());
    }

    @Transactional
    public void hardDeleteDriver(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        
        if (user.getRole() != Role.DRIVER) {
            throw new RuntimeException("User is not a driver");
        }

        userRepository.delete(user);
        log.info("Driver permanently deleted: {}", user.getEmail());
    }

    private DriverResponse mapToResponse(User user) {
        DriverResponse response = new DriverResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setBirthDate(user.getBirthDate());
        response.setStatus(user.getStatus());
        response.setEmailVerified(user.getEmailVerified());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}