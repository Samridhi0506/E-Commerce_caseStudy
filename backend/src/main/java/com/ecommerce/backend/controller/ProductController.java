package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateProductRequest;
import com.ecommerce.backend.dto.request.UpdateProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('TENANT')")
    @PostMapping("/{tenantName}")
    public ResponseEntity<ProductResponse> createProduct(
        @PathVariable String tenantName,
        @RequestBody CreateProductRequest request) {

        ProductResponse response =
            productService.createProduct(tenantName, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
}

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{tenantName}")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PathVariable String tenantName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductResponse> products =
                productService.getAllProducts(tenantName, page, size);

        return ResponseEntity.ok(products);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{tenantName}/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
        @PathVariable String tenantName,
        @RequestParam String keyword) {

        List<ProductResponse> products =
            productService.searchProducts(tenantName, keyword);

        return ResponseEntity.ok(products);
}

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{tenantName}/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable String tenantName,
            @PathVariable Long categoryId) {

        List<ProductResponse> products =
                productService.getProductsByCategory(tenantName, categoryId);

        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('TENANT')")
    @PutMapping("/{tenantName}/{productId}")
public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable String tenantName,
        @PathVariable Long productId,
        @RequestBody UpdateProductRequest request) {

    ProductResponse response =
            productService.updateProduct(tenantName, productId, request);

    return ResponseEntity.ok(response);
}

    @PreAuthorize("hasRole('TENANT')")
    @PatchMapping("/{tenantName}/{productId}/stock")
    public ResponseEntity<ProductResponse> updateStock(
        @PathVariable String tenantName,
        @PathVariable Long productId,
        @RequestParam Integer quantity) {

    ProductResponse response =
            productService.updateStock(tenantName, productId, quantity);

    return ResponseEntity.ok(response);
}

    @PreAuthorize("hasRole('TENANT')")
    @DeleteMapping("/{tenantName}/{productId}")
public ResponseEntity<String> deleteProduct(
        @PathVariable String tenantName,
        @PathVariable Long productId) {

    productService.deleteProduct(tenantName, productId);

    return ResponseEntity.ok("Product deleted successfully.");
}
}