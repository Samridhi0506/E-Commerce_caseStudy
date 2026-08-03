package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.CreateProductRequest;
import com.ecommerce.backend.dto.request.UpdateProductRequest;
import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.repository.CategoryRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.TenantRepository;
import com.ecommerce.backend.service.ProductService;
import org.springframework.stereotype.Service;
import com.ecommerce.backend.entity.Category;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          TenantRepository tenantRepository,
                          UserRepository userRepository) {

    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.tenantRepository = tenantRepository;
    this.userRepository = userRepository;
}

    @Override
public ProductResponse createProduct(String tenantName, CreateProductRequest request) {

    validateTenantAccess(tenantName);

    Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Product product = new Product();
    product.setProductName(request.getProductName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    product.setCategory(category);
    product.setTenant(tenant);
    product.setCreatedBy(user);

    Product savedProduct = productRepository.save(product);

    ProductResponse response = new ProductResponse();
    response.setProductId(savedProduct.getProductId());
    response.setProductName(savedProduct.getProductName());
    response.setDescription(savedProduct.getDescription());
    response.setPrice(savedProduct.getPrice());
    response.setStock(savedProduct.getStock());
    response.setCategory(savedProduct.getCategory().getCategoryName());
    response.setTenant(savedProduct.getTenant().getTenantName());
    response.setCreatedByUsername(savedProduct.getCreatedBy() != null ? savedProduct.getCreatedBy().getUsername() : null);
    response.setCreatedByEmail(savedProduct.getCreatedBy() != null ? savedProduct.getCreatedBy().getEmail() : null);

    return response;
}

    @Override
public ProductResponse updateProduct(String tenantName,Long productId,UpdateProductRequest request) {

    validateTenantAccess(tenantName);

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
        .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

Product product = productRepository.findByProductIdAndTenant(productId, tenant)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    product.setProductName(request.getProductName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    product.setCategory(category);

    Product updatedProduct = productRepository.save(product);

    ProductResponse response = new ProductResponse();
    response.setProductId(updatedProduct.getProductId());
    response.setProductName(updatedProduct.getProductName());
    response.setDescription(updatedProduct.getDescription());
    response.setPrice(updatedProduct.getPrice());
    response.setStock(updatedProduct.getStock());
    response.setCategory(updatedProduct.getCategory().getCategoryName());
    response.setTenant(updatedProduct.getTenant().getTenantName());

    return response;
}

    @Override
public void deleteProduct(String tenantName, Long productId){

    validateTenantAccess(tenantName);

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
        .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

Product product = productRepository.findByProductIdAndTenant(productId, tenant)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    productRepository.delete(product);
}

    @Override
public ProductResponse updateStock(String tenantName,Long productId,Integer quantity) {

    validateTenantAccess(tenantName);

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
        .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

Product product = productRepository.findByProductIdAndTenant(productId, tenant)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    product.setStock(quantity);

    Product updatedProduct = productRepository.save(product);

    ProductResponse response = new ProductResponse();
    response.setProductId(updatedProduct.getProductId());
    response.setProductName(updatedProduct.getProductName());
    response.setDescription(updatedProduct.getDescription());
    response.setPrice(updatedProduct.getPrice());
    response.setStock(updatedProduct.getStock());
    response.setCategory(updatedProduct.getCategory().getCategoryName());
    response.setTenant(updatedProduct.getTenant().getTenantName());

    return response;
}

    @Override
public Page<ProductResponse> getAllProducts(String tenantName,
                                            int page,
                                            int size) {

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    Pageable pageable = PageRequest.of(page, size);

    Page<Product> products = productRepository.findByTenant(tenant, pageable);

    return products.map(product -> {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setCategory(product.getCategory().getCategoryName());
        response.setTenant(product.getTenant().getTenantName());
        return response;
    });
}

   @Override
public List<ProductResponse> searchProducts(String tenantName, String keyword) {

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    List<Product> products =
            productRepository.findByTenantAndProductNameContainingIgnoreCase(
                    tenant,
                    keyword);

    return products.stream().map(product -> {

        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setCategory(product.getCategory().getCategoryName());
        response.setTenant(product.getTenant().getTenantName());

        return response;
    }).toList();
}

   @Override
public List<ProductResponse> getProductsByCategory(String tenantName,
                                                   Long categoryId) {

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    List<Product> products =
            productRepository.findByTenantAndCategory(tenant, category);

    return products.stream().map(product -> {

        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setCategory(product.getCategory().getCategoryName());
        response.setTenant(product.getTenant().getTenantName());

        return response;
    }).toList();
}

private void validateTenantAccess(String tenantName) {

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (user.getTenant() == null ||
            !user.getTenant().getTenantName().equals(tenantName)) {

        throw new AccessDeniedException("Access denied.");
    }
}

@Override
public Page<ProductResponse> getMarketplaceProducts(int page, int size) {

    Pageable pageable = PageRequest.of(page, size);

    Page<Product> products = productRepository.findAll(pageable);

    return products.map(product -> {
        ProductResponse response = new ProductResponse();

        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setCategory(product.getCategory().getCategoryName());
        response.setTenant(product.getTenant().getTenantName());

        return response;
    });
}

@Override
public ProductResponse getProductById(Long productId) {

    Product product = productRepository.findById(productId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Product not found"));

    ProductResponse response = new ProductResponse();
    response.setProductId(product.getProductId());
    response.setProductName(product.getProductName());
    response.setDescription(product.getDescription());
    response.setPrice(product.getPrice());
    response.setStock(product.getStock());
    response.setCategory(product.getCategory().getCategoryName());
    response.setTenant(product.getTenant().getTenantName());
    response.setCreatedByUsername(product.getCreatedBy() != null ? product.getCreatedBy().getUsername() : null);
    response.setCreatedByEmail(product.getCreatedBy() != null ? product.getCreatedBy().getEmail() : null);

    return response;
}
}