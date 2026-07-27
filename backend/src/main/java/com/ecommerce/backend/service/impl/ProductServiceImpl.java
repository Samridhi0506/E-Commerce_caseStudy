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

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              TenantRepository tenantRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
public ProductResponse createProduct(CreateProductRequest request) {

    Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    Tenant tenant = tenantRepository.findById(request.getTenantId())
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    Product product = new Product();
    product.setProductName(request.getProductName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    product.setCategory(category);
    product.setTenant(tenant);

    Product savedProduct = productRepository.save(product);

    ProductResponse response = new ProductResponse();
    response.setProductId(savedProduct.getProductId());
    response.setProductName(savedProduct.getProductName());
    response.setDescription(savedProduct.getDescription());
    response.setPrice(savedProduct.getPrice());
    response.setStock(savedProduct.getStock());
    response.setCategory(savedProduct.getCategory().getCategoryName());
    response.setTenant(savedProduct.getTenant().getTenantName());

    return response;
}

    @Override
public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {

    Product product = productRepository.findById(productId)
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
public void deleteProduct(Long productId) {

    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    productRepository.delete(product);
}

    @Override
public ProductResponse updateStock(Long productId, Integer quantity) {

    Product product = productRepository.findById(productId)
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
public List<ProductResponse> getAllProducts() {

    List<Product> products = productRepository.findAll();

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
public List<ProductResponse> searchProducts(String keyword) {

    List<Product> products = productRepository.findByProductNameContainingIgnoreCase(keyword);

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
public List<ProductResponse> getProductsByCategory(Long categoryId) {

    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    List<Product> products = productRepository.findByCategory(category);

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
public List<ProductResponse> getProductsByTenant(Long tenantId) {

    Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    List<Product> products = productRepository.findByTenant(tenant);

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
}