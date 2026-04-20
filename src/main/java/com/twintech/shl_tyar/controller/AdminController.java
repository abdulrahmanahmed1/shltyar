package com.twintech.shl_tyar.controller;

import com.twintech.shl_tyar.domain.ApplicationStatus;
import com.twintech.shl_tyar.dto.DriverApplicationResponse;
import com.twintech.shl_tyar.service.DriverApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Applications", description = "Admin endpoints for managing driver applications")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final DriverApplicationService driverApplicationService;

    @GetMapping("/applications")
    @Operation(summary = "Get all driver applications", description = "Retrieve all driver applications (Admin only)")
    public ResponseEntity<List<DriverApplicationResponse>> getAllApplications() {
        List<DriverApplicationResponse> applications = driverApplicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/applications/status/{status}")
    @Operation(summary = "Get applications by status", description = "Retrieve driver applications filtered by status (Admin only)")
    public ResponseEntity<List<DriverApplicationResponse>> getApplicationsByStatus(
            @PathVariable ApplicationStatus status) {
        List<DriverApplicationResponse> applications = driverApplicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/applications/{applicationId}/approve")
    @Operation(summary = "Approve driver application", description = "Approve a pending driver application (Admin only)")
    public ResponseEntity<DriverApplicationResponse> approveApplication(
            @PathVariable Long applicationId) {
        try {
            DriverApplicationResponse response = driverApplicationService.updateApplicationStatus(
                    applicationId, ApplicationStatus.APPROVED, null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to approve application: " + e.getMessage());
        }
    }

    @PutMapping("/applications/{applicationId}/reject")
    @Operation(summary = "Reject driver application", description = "Reject a pending driver application with reason (Admin only)")
    public ResponseEntity<DriverApplicationResponse> rejectApplication(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request) {
        try {
            String rejectionReason = request.get("rejectionReason");
            DriverApplicationResponse response = driverApplicationService.updateApplicationStatus(
                    applicationId, ApplicationStatus.REJECTED, rejectionReason);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reject application: " + e.getMessage());
        }
    }
}