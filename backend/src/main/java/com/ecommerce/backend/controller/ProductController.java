package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateProductRequest;
import com.ecommerce.backend.dto.request.UpdateProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody CreateProductRequest request) {

        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam String keyword) {

        List<ProductResponse> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId) {

        List<ProductResponse> products =
                productService.getProductsByCategory(categoryId);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<ProductResponse>> getProductsByTenant(
            @PathVariable Long tenantId) {

        List<ProductResponse> products =
                productService.getProductsByTenant(tenantId);

        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody UpdateProductRequest request) {

        ProductResponse response =
                productService.updateProduct(productId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        ProductResponse response =
                productService.updateStock(productId, quantity);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long productId) {

        productService.deleteProduct(productId);

        return ResponseEntity.ok("Product deleted successfully.");
    }
}