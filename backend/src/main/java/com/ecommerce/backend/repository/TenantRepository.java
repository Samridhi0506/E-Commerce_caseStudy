package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByTenantName(String tenantName);

}