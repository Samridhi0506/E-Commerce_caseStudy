package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.CreateTenantRequest;
import com.ecommerce.backend.dto.response.TenantResponse;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.repository.TenantRepository;
import com.ecommerce.backend.service.TenantService;
import org.springframework.stereotype.Service;

import com.ecommerce.backend.entity.Role;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.RoleRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.KeycloakService;

import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
private final RoleRepository roleRepository;
private final KeycloakService keycloakService;

    public TenantServiceImpl(
        TenantRepository tenantRepository,
        UserRepository userRepository,
        RoleRepository roleRepository,
        KeycloakService keycloakService) {

    this.tenantRepository = tenantRepository;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.keycloakService = keycloakService;
}

    @Override
    public TenantResponse createTenant(CreateTenantRequest request) {

        tenantRepository.findByTenantName(request.getTenantName())
                .ifPresent(t -> {
                    throw new RuntimeException("Tenant already exists.");
                });

        Tenant tenant = Tenant.builder()
                .tenantName(request.getTenantName())
                .domain(request.getDomain())
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);

        return mapToResponse(savedTenant);
    }

    @Override
    public List<TenantResponse> getAllTenants() {

        return tenantRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public TenantResponse getTenantById(Long tenantId) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found."));

        return mapToResponse(tenant);
    }

    @Override
    public TenantResponse updateTenant(Long tenantId, CreateTenantRequest request) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found."));

        tenant.setTenantName(request.getTenantName());
        tenant.setDomain(request.getDomain());

        Tenant updatedTenant = tenantRepository.save(tenant);

        return mapToResponse(updatedTenant);
    }

    @Override
    public void deleteTenant(Long tenantId) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found."));

        tenantRepository.delete(tenant);
    }

    private TenantResponse mapToResponse(Tenant tenant) {

        return TenantResponse.builder()
                .tenantId(tenant.getTenantId())
                .tenantName(tenant.getTenantName())
                .domain(tenant.getDomain())
                .build();
    }

    @Override
public void assignTenantAdmin(Long tenantId, Long userId) {

    Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found."));

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found."));

    Role tenantRole = roleRepository.findByRoleName("ROLE_TENANT")
            .orElseThrow(() -> new RuntimeException("ROLE_TENANT not found."));

    user.setTenant(tenant);
user.setRole(tenantRole);

userRepository.save(user);

keycloakService.removeRealmRole(
        user.getKeycloakId(),
        "ROLE_USER"
);

keycloakService.assignRealmRole(
        user.getKeycloakId(),
        "ROLE_TENANT"
);
}
}