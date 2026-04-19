package com.twintech.shl_tyar.controller;

import com.twintech.shl_tyar.dto.DriverApplicationRequest;
import com.twintech.shl_tyar.dto.DriverApplicationResponse;
import com.twintech.shl_tyar.service.DriverApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverApplicationService driverApplicationService;

    @PostMapping(value = "/application", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DriverApplicationResponse> submitApplication(
            @Valid @ModelAttribute DriverApplicationRequest request) {
        try {
            DriverApplicationResponse response = driverApplicationService.submitApplication(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Application submission failed: " + e.getMessage());
        }
    }

    @GetMapping("/application")
    public ResponseEntity<DriverApplicationResponse> getMyApplication() {
        try {
            DriverApplicationResponse response = driverApplicationService.getMyApplication();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve application: " + e.getMessage());
        }
    }
}