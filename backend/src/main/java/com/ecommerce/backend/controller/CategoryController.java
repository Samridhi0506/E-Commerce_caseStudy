package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateCategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;
import com.ecommerce.backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/{tenantName}")
    public ResponseEntity<CategoryResponse> createCategory(
        @PathVariable String tenantName,
        @RequestBody CreateCategoryRequest request) {

    CategoryResponse response =
            categoryService.createCategory(tenantName, request);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
}

    @GetMapping("/{tenantName}")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
        @PathVariable String tenantName) {

    List<CategoryResponse> categories =
            categoryService.getAllCategories(tenantName);

    return ResponseEntity.ok(categories);
}

    @PutMapping("/{tenantName}/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
        @PathVariable String tenantName,
        @PathVariable Long categoryId,
        @RequestBody CreateCategoryRequest request) {

    CategoryResponse response =
            categoryService.updateCategory(tenantName, categoryId, request);

    return ResponseEntity.ok(response);
}

    @DeleteMapping("/{tenantName}/{categoryId}")
    public ResponseEntity<String> deleteCategory(
        @PathVariable String tenantName,
        @PathVariable Long categoryId) {

    categoryService.deleteCategory(tenantName, categoryId);

    return ResponseEntity.ok("Category deleted successfully.");
}
}