package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateTenantRequest;
import com.ecommerce.backend.dto.response.TenantResponse;
import com.ecommerce.backend.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantResponse> createTenant(
            @Valid @RequestBody CreateTenantRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tenantService.createTenant(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TenantResponse>> getAllTenants() {

        return ResponseEntity.ok(tenantService.getAllTenants());
    }

    @GetMapping("/{tenantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantResponse> getTenantById(
            @PathVariable Long tenantId) {

        return ResponseEntity.ok(tenantService.getTenantById(tenantId));
    }

    @PutMapping("/{tenantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantResponse> updateTenant(
            @PathVariable Long tenantId,
            @Valid @RequestBody CreateTenantRequest request) {

        return ResponseEntity.ok(
                tenantService.updateTenant(tenantId, request)
        );
    }

    @DeleteMapping("/{tenantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTenant(
            @PathVariable Long tenantId) {

        tenantService.deleteTenant(tenantId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tenantId}/assign-admin/{userId}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<String> assignTenantAdmin(
        @PathVariable Long tenantId,
        @PathVariable Long userId) {

    tenantService.assignTenantAdmin(tenantId, userId);

    return ResponseEntity.ok("Tenant admin assigned successfully.");
}
}