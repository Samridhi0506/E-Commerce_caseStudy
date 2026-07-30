package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.dto.request.CreateProductRequest;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.TenantRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.impl.ProductServiceImpl;
import com.ecommerce.backend.dto.request.UpdateProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecommerce.backend.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setup() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "tenantuser",
                        null
                )
        );
    }

    @Test
    void createProduct_ShouldCreateSuccessfully() {

        Tenant tenant = new Tenant();
        tenant.setTenantName("nike");

        User user = new User();
        user.setTenant(tenant);

        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Shoes");

        CreateProductRequest request = new CreateProductRequest();
        request.setProductName("Air Max");
        request.setDescription("Running Shoe");
        request.setPrice(BigDecimal.valueOf(120));
        request.setStock(10);
        request.setCategoryId(1L);

        Product savedProduct = new Product();
        savedProduct.setProductId(1L);
        savedProduct.setProductName(request.getProductName());
        savedProduct.setDescription(request.getDescription());
        savedProduct.setPrice(request.getPrice());
        savedProduct.setStock(request.getStock());
        savedProduct.setCategory(category);
        savedProduct.setTenant(tenant);

        when(userRepository.findByUsername("tenantuser"))
                .thenReturn(Optional.of(user));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(tenantRepository.findByTenantName("nike"))
                .thenReturn(Optional.of(tenant));

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        var response = productService.createProduct("nike", request);

        assertNotNull(response);
        assertEquals("Air Max", response.getProductName());

        verify(productRepository).save(any(Product.class));
    }

    @Test
void createProduct_ShouldThrow_WhenCategoryNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    CreateProductRequest request = new CreateProductRequest();
    request.setCategoryId(1L);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(categoryRepository.findById(1L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.createProduct("nike", request));

    assertEquals("Category not found", exception.getMessage());

    verify(productRepository, never()).save(any(Product.class));
}

    @Test
void createProduct_ShouldThrow_WhenTenantNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    Category category = new Category();
    category.setCategoryId(1L);

    CreateProductRequest request = new CreateProductRequest();
    request.setCategoryId(1L);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.createProduct("nike", request));

    assertEquals("Tenant not found", exception.getMessage());

    verify(productRepository, never()).save(any(Product.class));
}

    @Test
void createProduct_ShouldThrow_WhenTenantAccessDenied() {

    Tenant userTenant = new Tenant();
    userTenant.setTenantName("nike");

    User user = new User();
    user.setTenant(userTenant);

    CreateProductRequest request = new CreateProductRequest();
    request.setCategoryId(1L);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    assertThrows(AccessDeniedException.class,
            () -> productService.createProduct("adidas", request));

    verify(categoryRepository, never()).findById(anyLong());
    verify(productRepository, never()).save(any(Product.class));
}
@Test
void updateProduct_ShouldUpdateSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    Category category = new Category();
    category.setCategoryId(1L);
    category.setCategoryName("Shoes");

    Product product = new Product();
    product.setProductId(1L);
    product.setTenant(tenant);
    product.setCategory(category);

    UpdateProductRequest request = new UpdateProductRequest();
    request.setProductName("Air Max 2025");
    request.setDescription("Updated Description");
    request.setPrice(BigDecimal.valueOf(150));
    request.setStock(25);
    request.setCategoryId(1L);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(productRepository.findByProductIdAndTenant(1L, tenant))
            .thenReturn(Optional.of(product));

    when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));

    when(productRepository.save(any(Product.class)))
            .thenReturn(product);

    var response = productService.updateProduct("nike", 1L, request);

    assertNotNull(response);
    assertEquals("Air Max 2025", response.getProductName());

    verify(productRepository).save(product);
}

@Test
void deleteProduct_ShouldDeleteSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    Product product = new Product();
    product.setProductId(1L);
    product.setTenant(tenant);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(productRepository.findByProductIdAndTenant(1L, tenant))
            .thenReturn(Optional.of(product));

    productService.deleteProduct("nike", 1L);

    verify(productRepository).delete(product);
}

@Test
void deleteProduct_ShouldThrow_WhenProductNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(productRepository.findByProductIdAndTenant(1L, tenant))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.deleteProduct("nike", 1L));

    assertEquals("Product not found", exception.getMessage());

    verify(productRepository, never()).delete(any(Product.class));
}

@Test
void deleteProduct_ShouldThrow_WhenTenantAccessDenied() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    assertThrows(AccessDeniedException.class,
            () -> productService.deleteProduct("adidas", 1L));

    verify(tenantRepository, never()).findByTenantName(anyString());
    verify(productRepository, never()).findByProductIdAndTenant(anyLong(), any(Tenant.class));
    verify(productRepository, never()).delete(any(Product.class));
}

@Test
void updateStock_ShouldUpdateSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    Category category = new Category();
    category.setCategoryId(1L);

    Product product = new Product();
    product.setProductId(1L);
    product.setTenant(tenant);
    product.setCategory(category);
    product.setProductName("Air Max");
    product.setPrice(BigDecimal.valueOf(150));
    product.setDescription("Running Shoes");
    product.setStock(10);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(productRepository.findByProductIdAndTenant(1L, tenant))
            .thenReturn(Optional.of(product));

    when(productRepository.save(any(Product.class)))
            .thenReturn(product);

    var response = productService.updateStock("nike", 1L, 25);

    assertNotNull(response);
    assertEquals(25, response.getStock());

    verify(productRepository).save(product);
}

@Test
void updateStock_ShouldThrow_WhenProductNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(productRepository.findByProductIdAndTenant(1L, tenant))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.updateStock("nike", 1L, 20));

    assertEquals("Product not found", exception.getMessage());

    verify(productRepository, never()).save(any(Product.class));
}

@Test
void updateStock_ShouldThrow_WhenTenantAccessDenied() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    assertThrows(AccessDeniedException.class,
            () -> productService.updateStock("adidas", 1L, 20));

    verify(tenantRepository, never()).findByTenantName(anyString());
    verify(productRepository, never()).findByProductIdAndTenant(anyLong(), any(Tenant.class));
    verify(productRepository, never()).save(any(Product.class));
}

@Test
void getAllProducts_ShouldReturnProductsSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    Category category = new Category();
    category.setCategoryId(1L);
    category.setCategoryName("Shoes");

    Product product = new Product();
    product.setProductId(1L);
    product.setProductName("Air Max");
    product.setDescription("Running Shoes");
    product.setPrice(BigDecimal.valueOf(150));
    product.setStock(20);
    product.setCategory(category);
    product.setTenant(tenant);

    Page<Product> productPage = new PageImpl<>(
            List.of(product),
            PageRequest.of(0, 10),
            1
    );

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(productRepository.findByTenant(eq(tenant), any(Pageable.class)))
            .thenReturn(productPage);

    Page<ProductResponse> response =
            productService.getAllProducts("nike", 0, 10);

    assertNotNull(response);
    assertEquals(1, response.getTotalElements());

    ProductResponse productResponse = response.getContent().getFirst();

    assertEquals("Air Max", productResponse.getProductName());
    assertEquals("Shoes", productResponse.getCategory());
    assertEquals("nike", productResponse.getTenant());

    verify(productRepository).findByTenant(eq(tenant), any(Pageable.class));
}

@Test
void getAllProducts_ShouldThrow_WhenTenantNotFound() {

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.getAllProducts("nike", 0, 10));

    assertEquals("Tenant not found", exception.getMessage());

    verify(productRepository, never()).findByTenant(any(Tenant.class), any(Pageable.class));
}

@Test
void updateProduct_ShouldThrow_WhenProductNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    UpdateProductRequest request = new UpdateProductRequest();
    request.setCategoryId(1L);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(productRepository.findByProductIdAndTenant(1L, tenant))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.updateProduct("nike", 1L, request));

    assertEquals("Product not found", exception.getMessage());

    verify(productRepository, never()).save(any(Product.class));
}

@Test
void updateProduct_ShouldThrow_WhenCategoryNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    Product product = new Product();
    product.setProductId(1L);
    product.setTenant(tenant);

    UpdateProductRequest request = new UpdateProductRequest();
    request.setCategoryId(1L);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(productRepository.findByProductIdAndTenant(1L, tenant))
            .thenReturn(Optional.of(product));

    when(categoryRepository.findById(1L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.updateProduct("nike", 1L, request));

    assertEquals("Category not found", exception.getMessage());

    verify(productRepository, never()).save(any(Product.class));
}

@Test
void updateProduct_ShouldThrow_WhenTenantNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    UpdateProductRequest request = new UpdateProductRequest();
    request.setCategoryId(1L);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.updateProduct("nike", 1L, request));

    assertEquals("Tenant not found", exception.getMessage());

    verify(productRepository, never()).findByProductIdAndTenant(anyLong(), any(Tenant.class));
    verify(productRepository, never()).save(any(Product.class));
}

@Test
void updateProduct_ShouldThrow_WhenTenantAccessDenied() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setTenant(tenant);

    UpdateProductRequest request = new UpdateProductRequest();
    request.setCategoryId(1L);

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    assertThrows(AccessDeniedException.class,
            () -> productService.updateProduct("adidas", 1L, request));

    verify(tenantRepository, never()).findByTenantName(anyString());
    verify(productRepository, never()).findByProductIdAndTenant(anyLong(), any(Tenant.class));
    verify(productRepository, never()).save(any(Product.class));
}

@Test
void searchProducts_ShouldReturnMatchingProducts() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    Category category = new Category();
    category.setCategoryName("Shoes");

    Product product = new Product();
    product.setProductId(1L);
    product.setProductName("Air Max");
    product.setDescription("Running Shoes");
    product.setPrice(BigDecimal.valueOf(150));
    product.setStock(20);
    product.setCategory(category);
    product.setTenant(tenant);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(productRepository.findByTenantAndProductNameContainingIgnoreCase(
            tenant, "air"))
            .thenReturn(List.of(product));

    List<ProductResponse> response =
            productService.searchProducts("nike", "air");

    assertEquals(1, response.size());
    assertEquals("Air Max", response.getFirst().getProductName());
    assertEquals("Shoes", response.getFirst().getCategory());
    assertEquals("nike", response.getFirst().getTenant());

    verify(productRepository)
            .findByTenantAndProductNameContainingIgnoreCase(tenant, "air");
}

@Test
void searchProducts_ShouldThrow_WhenTenantNotFound() {

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.searchProducts("nike", "air"));

    assertEquals("Tenant not found", exception.getMessage());

    verify(productRepository, never())
            .findByTenantAndProductNameContainingIgnoreCase(any(), anyString());
}

@Test
void getProductsByCategory_ShouldReturnProductsSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    Category category = new Category();
    category.setCategoryId(1L);
    category.setCategoryName("Shoes");

    Product product = new Product();
    product.setProductId(1L);
    product.setProductName("Air Max");
    product.setDescription("Running Shoes");
    product.setPrice(BigDecimal.valueOf(150));
    product.setStock(20);
    product.setTenant(tenant);
    product.setCategory(category);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(categoryRepository.findById(1L))
            .thenReturn(Optional.of(category));

    when(productRepository.findByTenantAndCategory(tenant, category))
            .thenReturn(List.of(product));

    List<ProductResponse> response =
            productService.getProductsByCategory("nike", 1L);

    assertEquals(1, response.size());
    assertEquals("Air Max", response.getFirst().getProductName());
    assertEquals("Shoes", response.getFirst().getCategory());
    assertEquals("nike", response.getFirst().getTenant());

    verify(productRepository)
            .findByTenantAndCategory(tenant, category);
}

@Test
void getProductsByCategory_ShouldThrow_WhenTenantNotFound() {

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.getProductsByCategory("nike", 1L));

    assertEquals("Tenant not found", exception.getMessage());

    verify(categoryRepository, never()).findById(anyLong());
    verify(productRepository, never()).findByTenantAndCategory(any(), any());
}

@Test
void getProductsByCategory_ShouldThrow_WhenCategoryNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(categoryRepository.findById(1L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> productService.getProductsByCategory("nike", 1L));

    assertEquals("Category not found", exception.getMessage());

    verify(productRepository, never()).findByTenantAndCategory(any(), any());
}
}