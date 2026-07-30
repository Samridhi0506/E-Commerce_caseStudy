package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateCategoryRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.TenantRepository;
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

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;


@Test
void createCategory_ShouldCreateSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    CreateCategoryRequest request = new CreateCategoryRequest();
    request.setCategoryName("Shoes");

    Category category = new Category();
    category.setCategoryId(1L);
    category.setCategoryName("Shoes");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(categoryRepository.save(any(Category.class)))
            .thenReturn(category);

    CategoryResponse response =
            categoryService.createCategory("nike", request);

    assertNotNull(response);
    assertEquals(1L, response.getCategoryId());
    assertEquals("Shoes", response.getCategoryName());

    verify(categoryRepository).save(any(Category.class));
}

@Test
void createCategory_ShouldThrow_WhenTenantNotFound() {

    CreateCategoryRequest request = new CreateCategoryRequest();
    request.setCategoryName("Shoes");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> categoryService.createCategory("nike", request));

    assertEquals("Tenant not found", exception.getMessage());

    verify(categoryRepository, never()).save(any(Category.class));
}

@Test
void updateCategory_ShouldThrow_WhenTenantNotFound() {

    CreateCategoryRequest request = new CreateCategoryRequest();
    request.setCategoryName("Shoes");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> categoryService.updateCategory("nike", 1L, request));

    assertEquals("Tenant not found", exception.getMessage());

    verify(categoryRepository, never()).findById(anyLong());
    verify(categoryRepository, never()).save(any(Category.class));
}

@Test
void updateCategory_ShouldUpdateSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    Category category = new Category();
    category.setCategoryId(1L);
    category.setCategoryName("Old");

    CreateCategoryRequest request = new CreateCategoryRequest();
    request.setCategoryName("Shoes");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));

    when(categoryRepository.save(any(Category.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryResponse response =
            categoryService.updateCategory("nike", 1L, request);

    assertNotNull(response);
    assertEquals("Shoes", response.getCategoryName());

    verify(categoryRepository).save(category);
}

@Test
void updateCategory_ShouldThrow_WhenCategoryNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    CreateCategoryRequest request = new CreateCategoryRequest();
    request.setCategoryName("Shoes");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(categoryRepository.findById(1L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> categoryService.updateCategory("nike", 1L, request));

    assertEquals("Category not found", exception.getMessage());

    verify(categoryRepository, never()).save(any(Category.class));
}

@Test
void deleteCategory_ShouldDeleteSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    Category category = new Category();
    category.setCategoryId(1L);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));

    categoryService.deleteCategory("nike", 1L);

    verify(categoryRepository).delete(category);
}

@Test
void deleteCategory_ShouldThrow_WhenCategoryNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(categoryRepository.findById(1L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> categoryService.deleteCategory("nike", 1L));

    assertEquals("Category not found", exception.getMessage());

    verify(categoryRepository, never()).delete(any(Category.class));
}

@Test
void getAllCategories_ShouldReturnCategoriesSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    Category category = new Category();
    category.setCategoryId(1L);
    category.setCategoryName("Shoes");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(categoryRepository.findAll())
            .thenReturn(List.of(category));

    List<CategoryResponse> response =
            categoryService.getAllCategories("nike");

    assertEquals(1, response.size());
    assertEquals("Shoes", response.getFirst().getCategoryName());

    verify(categoryRepository).findAll();
}
}