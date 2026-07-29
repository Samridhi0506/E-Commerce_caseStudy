package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.CreateCategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;
import com.ecommerce.backend.service.CategoryService;
import org.springframework.stereotype.Service;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.TenantRepository;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
private final TenantRepository tenantRepository;

public CategoryServiceImpl(CategoryRepository categoryRepository,
                           TenantRepository tenantRepository) {
    this.categoryRepository = categoryRepository;
    this.tenantRepository = tenantRepository;
}

    @Override
public CategoryResponse createCategory(String tenantName,
                                       CreateCategoryRequest request) {

    tenantRepository.findByTenantName(tenantName)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    Category category = new Category();
    category.setCategoryName(request.getCategoryName());

    Category savedCategory = categoryRepository.save(category);

    CategoryResponse response = new CategoryResponse();
    response.setCategoryId(savedCategory.getCategoryId());
    response.setCategoryName(savedCategory.getCategoryName());

    return response;
}

    @Override
public CategoryResponse updateCategory(String tenantName,
                                       Long categoryId,
                                       CreateCategoryRequest request) {

    tenantRepository.findByTenantName(tenantName)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    category.setCategoryName(request.getCategoryName());

    Category updatedCategory = categoryRepository.save(category);

    CategoryResponse response = new CategoryResponse();
    response.setCategoryId(updatedCategory.getCategoryId());
    response.setCategoryName(updatedCategory.getCategoryName());

    return response;
}

   @Override
public void deleteCategory(String tenantName, Long categoryId) {

    tenantRepository.findByTenantName(tenantName)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    categoryRepository.delete(category);
}

    @Override
public List<CategoryResponse> getAllCategories(String tenantName) {

    tenantRepository.findByTenantName(tenantName)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    List<Category> categories = categoryRepository.findAll();

    return categories.stream().map(category -> {
        CategoryResponse response = new CategoryResponse();
        response.setCategoryId(category.getCategoryId());
        response.setCategoryName(category.getCategoryName());
        return response;
    }).toList();
}
}