package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.AddFavoriteRequest;
import com.ecommerce.backend.dto.response.FavoriteResponse;
import com.ecommerce.backend.entity.Favorite;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.FavoriteRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.TenantRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.FavoriteService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TenantRepository tenantRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               UserRepository userRepository,
                               ProductRepository productRepository,
                               TenantRepository tenantRepository) {

        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.tenantRepository = tenantRepository;
    }

    private Tenant resolveTenant(String tenantName) {

        if (tenantName == null || tenantName.isBlank() || "global".equalsIgnoreCase(tenantName)) {
            return null;
        }

        return tenantRepository.findByTenantName(tenantName)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
    }

    private void validateUserAccess(Long userId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (!loggedInUser.getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied.");
        }
    }

    private void validateFavoriteAccess(Favorite favorite) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (!favorite.getUser().getUserId().equals(loggedInUser.getUserId())) {
            throw new AccessDeniedException("Access denied.");
        }
    }

    @Override
public FavoriteResponse addFavorite(String tenantName,
                                    Long userId,
                                    AddFavoriteRequest request) {

    Tenant tenant = resolveTenant(tenantName);

    validateUserAccess(userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Product product;

    if (tenant != null) {
        product = productRepository
                .findByProductIdAndTenant(request.getProductId(), tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    } else {
        product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    Favorite existingFavorite = favoriteRepository
            .findByUserAndProduct(user, product)
            .orElse(null);

    // Remove if already favorited (toggle)
    if (existingFavorite != null) {
        favoriteRepository.delete(existingFavorite);

        FavoriteResponse response = new FavoriteResponse();
        response.setFavoriteId(null);
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setCategory(product.getCategory() != null
                ? product.getCategory().getCategoryName()
                : null);
        response.setTenant(product.getTenant() != null
                ? product.getTenant().getTenantName()
                : null);

        return response;
    }

    // Add to favorites
    Favorite favorite = new Favorite();
    favorite.setUser(user);
    favorite.setProduct(product);

    Favorite savedFavorite = favoriteRepository.save(favorite);

    FavoriteResponse response = new FavoriteResponse();
    response.setFavoriteId(savedFavorite.getFavoriteId());
    response.setProductId(product.getProductId());
    response.setProductName(product.getProductName());
    response.setDescription(product.getDescription());
    response.setPrice(product.getPrice());
    response.setStock(product.getStock());
    response.setCategory(product.getCategory() != null
            ? product.getCategory().getCategoryName()
            : null);
    response.setTenant(product.getTenant() != null
            ? product.getTenant().getTenantName()
            : null);

    return response;
}

    @Override
    public List<FavoriteResponse> getFavoritesByUser(String tenantName,
                                                     Long userId) {
        resolveTenant(tenantName);
        validateUserAccess(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Favorite> favorites = favoriteRepository.findByUser(user);

        return favorites.stream().map(favorite -> {

            Product product = favorite.getProduct();

            FavoriteResponse response = new FavoriteResponse();
            response.setFavoriteId(favorite.getFavoriteId());
            response.setProductId(product.getProductId());
            response.setProductName(product.getProductName());
            response.setDescription(product.getDescription());
            response.setPrice(product.getPrice());
            response.setStock(product.getStock());
            response.setCategory(product.getCategory() != null
                    ? product.getCategory().getCategoryName()
                    : null);
            response.setTenant(product.getTenant() != null
                    ? product.getTenant().getTenantName()
                    : null);

            return response;

        }).toList();
    }

    @Override
    public void removeFavorite(String tenantName,
                               Long favoriteId) {
        resolveTenant(tenantName);

        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

        validateFavoriteAccess(favorite);

        favoriteRepository.delete(favorite);
    }
}