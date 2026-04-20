package com.twintech.shl_tyar.controller;

import com.twintech.shl_tyar.domain.User;
import com.twintech.shl_tyar.dto.AdminRegistrationRequest;
import com.twintech.shl_tyar.dto.DriverResponse;
import com.twintech.shl_tyar.dto.DriverUpdateRequest;
import com.twintech.shl_tyar.service.AdminService;
import com.twintech.shl_tyar.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Admin and driver management endpoints")
public class AdminManagementController {

    private final AdminService adminService;
    private final DriverService driverService;

    @PostMapping("/register")
    @Operation(summary = "Register a new admin account")
    public ResponseEntity<User> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        User admin = adminService.registerAdmin(request);
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/drivers")
    @Operation(summary = "Get all drivers", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        List<DriverResponse> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/drivers/{id}")
    @Operation(summary = "Get driver by ID", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable Long id) {
        DriverResponse driver = driverService.getDriverById(id);
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/drivers/email/{email}")
    @Operation(summary = "Get driver by email", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<DriverResponse> getDriverByEmail(@PathVariable String email) {
        DriverResponse driver = driverService.getDriverByEmail(email);
        return ResponseEntity.ok(driver);
    }

    @PutMapping("/drivers/{id}")
    @Operation(summary = "Update driver information", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<DriverResponse> updateDriver(
            @PathVariable Long id,
            @RequestBody DriverUpdateRequest request) {
        DriverResponse driver = driverService.updateDriver(id, request);
        return ResponseEntity.ok(driver);
    }

    @DeleteMapping("/drivers/{id}")
    @Operation(summary = "Soft delete driver", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Map<String, String>> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok(Map.of("message", "Driver deleted successfully"));
    }

    @DeleteMapping("/drivers/{id}/permanent")
    @Operation(summary = "Permanently delete driver", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Map<String, String>> hardDeleteDriver(@PathVariable Long id) {
        driverService.hardDeleteDriver(id);
        return ResponseEntity.ok(Map.of("message", "Driver permanently deleted"));
    }
}