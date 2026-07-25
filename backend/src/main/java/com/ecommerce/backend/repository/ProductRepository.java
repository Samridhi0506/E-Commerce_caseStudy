package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByTenant(Tenant tenant);

    List<Product> findByCategory(Category category);

    List<Product> findByProductNameContainingIgnoreCase(String productName);

}