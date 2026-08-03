package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateCategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.service.impl.CategoryServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_ShouldCreateSuccessfully() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setCategoryName("Shoes");

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Shoes");

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        CategoryResponse response = categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals(1L, response.getCategoryId());
        assertEquals("Shoes", response.getCategoryName());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldUpdateSuccessfully() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Old");

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setCategoryName("Shoes");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response = categoryService.updateCategory(1L, request);

        assertNotNull(response);
        assertEquals("Shoes", response.getCategoryName());

        verify(categoryRepository).save(category);
    }

    @Test
    void updateCategory_ShouldThrow_WhenCategoryNotFound() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setCategoryName("Shoes");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> categoryService.updateCategory(1L, request));

        assertEquals("Category not found", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteSuccessfully() {
        Category category = new Category();
        category.setCategoryId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_ShouldThrow_WhenCategoryNotFound() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> categoryService.deleteCategory(1L));

        assertEquals("Category not found", exception.getMessage());

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void getAllCategories_ShouldReturnCategoriesSuccessfully() {
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Shoes");

        when(categoryRepository.findAll())
                .thenReturn(List.of(category));

        List<CategoryResponse> response =
                categoryService.getAllCategories();

        assertEquals(1, response.size());
        assertEquals("Shoes", response.get(0).getCategoryName());

        verify(categoryRepository).findAll();
    }
}
