package com.twintech.shl_tyar.controller;

import com.twintech.shl_tyar.dto.SalesRegistrationRequest;
import com.twintech.shl_tyar.dto.SalesRegistrationResponse;
import com.twintech.shl_tyar.service.SalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales account management endpoints")
public class SalesController {

    private final SalesService salesService;

    @PostMapping("/register")
    @Operation(summary = "Register a new sales account")
    public ResponseEntity<SalesRegistrationResponse> registerSales(@Valid @RequestBody SalesRegistrationRequest request) {
        try {
            SalesRegistrationResponse response = salesService.registerSales(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Sales registration failed: " + e.getMessage());
        }
    }
}