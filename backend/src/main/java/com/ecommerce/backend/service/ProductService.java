package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateProductRequest;
import com.ecommerce.backend.dto.request.UpdateProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(String tenantName, CreateProductRequest request);

    ProductResponse updateProduct(String tenantName, Long productId, UpdateProductRequest request);

    void deleteProduct(String tenantName, Long productId);

    ProductResponse updateStock(String tenantName, Long productId, Integer quantity);

    Page<ProductResponse> getAllProducts(String tenantName, int page, int size);

    List<ProductResponse> searchProducts(String tenantName, String keyword);

    List<ProductResponse> getProductsByCategory(String tenantName, Long categoryId);

    Page<ProductResponse> getMarketplaceProducts(int page, int size);

    ProductResponse getProductById(Long productId);
}