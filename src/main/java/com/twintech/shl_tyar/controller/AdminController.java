package com.twintech.shl_tyar.controller;

import com.twintech.shl_tyar.domain.ApplicationStatus;
import com.twintech.shl_tyar.dto.DriverApplicationResponse;
import com.twintech.shl_tyar.service.DriverApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DriverApplicationService driverApplicationService;

    @GetMapping("/applications")
    public ResponseEntity<List<DriverApplicationResponse>> getAllApplications() {
        List<DriverApplicationResponse> applications = driverApplicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/applications/status/{status}")
    public ResponseEntity<List<DriverApplicationResponse>> getApplicationsByStatus(
            @PathVariable ApplicationStatus status) {
        List<DriverApplicationResponse> applications = driverApplicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/applications/{applicationId}/approve")
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