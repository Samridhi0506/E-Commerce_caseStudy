package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateTenantRequest;
import com.ecommerce.backend.dto.response.TenantResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TenantService {

    TenantResponse createTenant(CreateTenantRequest request);

    List<TenantResponse> getAllTenants();

    Page<TenantResponse> getAllTenantsPage(int page, int size, String keyword);

    TenantResponse getTenantById(Long tenantId);

    TenantResponse updateTenant(Long tenantId, CreateTenantRequest request);

    void deleteTenant(Long tenantId);

    void assignTenantAdmin(Long tenantId, Long userId);
}