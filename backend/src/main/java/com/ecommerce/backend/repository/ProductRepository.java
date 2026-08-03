package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByTenant(Tenant tenant);

    Page<Product> findByTenant(Tenant tenant, Pageable pageable);

    Optional<Product> findByProductIdAndTenant(Long productId, Tenant tenant);

    List<Product> findByTenantAndProductNameContainingIgnoreCase(
        Tenant tenant,
        String productName);

    Page<Product> findAll(Pageable pageable);

    List<Product> findByTenantAndCategory(Tenant tenant, Category category);
}