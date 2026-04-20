package com.twintech.shl_tyar.controller;

import com.twintech.shl_tyar.dto.DriverApplicationRequest;
import com.twintech.shl_tyar.dto.DriverApplicationResponse;
import com.twintech.shl_tyar.service.DriverApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
@Tag(name = "Driver", description = "Driver application endpoints")
public class DriverController {

    private final DriverApplicationService driverApplicationService;

    @PostMapping(value = "/application", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Submit driver application", description = "Submit a new driver application with registration (Public access)")
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
    @Operation(summary = "Get my application", description = "Retrieve the authenticated driver's application (Driver only)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<DriverApplicationResponse> getMyApplication() {
        try {
            DriverApplicationResponse response = driverApplicationService.getMyApplication();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve application: " + e.getMessage());
        }
    }
}