package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateCategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(String tenantName, CreateCategoryRequest request);

    CategoryResponse updateCategory(String tenantName,
                                    Long categoryId,
                                    CreateCategoryRequest request);

    void deleteCategory(String tenantName, Long categoryId);

    List<CategoryResponse> getAllCategories(String tenantName);
}