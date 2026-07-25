package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.CreateCategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;
import com.ecommerce.backend.service.CategoryService;
import org.springframework.stereotype.Service;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.entity.Category;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

public CategoryServiceImpl(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
}

    @Override
public CategoryResponse createCategory(CreateCategoryRequest request) {

    Category category = new Category();
    category.setCategoryName(request.getCategoryName());

    Category savedCategory = categoryRepository.save(category);

    CategoryResponse response = new CategoryResponse();
    response.setCategoryId(savedCategory.getCategoryId());
    response.setCategoryName(savedCategory.getCategoryName());

    return response;
}

    @Override
public CategoryResponse updateCategory(Long categoryId, CreateCategoryRequest request) {

    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found"));

    category.setCategoryName(request.getCategoryName());

    Category updatedCategory = categoryRepository.save(category);

    CategoryResponse response = new CategoryResponse();
    response.setCategoryId(updatedCategory.getCategoryId());
    response.setCategoryName(updatedCategory.getCategoryName());

    return response;
}

    @Override
public void deleteCategory(Long categoryId) {

    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("Category not found"));

    categoryRepository.delete(category);
}

    @Override
public List<CategoryResponse> getAllCategories() {

    List<Category> categories = categoryRepository.findAll();

    return categories.stream().map(category -> {
        CategoryResponse response = new CategoryResponse();
        response.setCategoryId(category.getCategoryId());
        response.setCategoryName(category.getCategoryName());
        return response;
    }).toList();
}
}