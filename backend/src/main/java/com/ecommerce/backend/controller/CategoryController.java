package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateCategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;
import com.ecommerce.backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
        @RequestBody CreateCategoryRequest request) {

    CategoryResponse response = categoryService.createCategory(request);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
}

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

    List<CategoryResponse> categories = categoryService.getAllCategories();

    return ResponseEntity.ok(categories);
}

    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN')")
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
        @PathVariable Long categoryId,
        @RequestBody CreateCategoryRequest request) {

    CategoryResponse response = categoryService.updateCategory(categoryId, request);

    return ResponseEntity.ok(response);
}

    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN')")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(
        @PathVariable Long categoryId) {

    categoryService.deleteCategory(categoryId);

    return ResponseEntity.ok("Category deleted successfully.");
}
}