package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateCategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse updateCategory(Long categoryId, CreateCategoryRequest request);

    void deleteCategory(Long categoryId);

    List<CategoryResponse> getAllCategories();
}