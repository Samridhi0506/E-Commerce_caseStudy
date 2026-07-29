package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.AddFavoriteRequest;
import com.ecommerce.backend.dto.response.FavoriteResponse;
import com.ecommerce.backend.entity.Favorite;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.FavoriteRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.FavoriteService;
import org.springframework.stereotype.Service;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.repository.TenantRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

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
                                    AddFavoriteRequest request){

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
        .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    validateUserAccess(userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Product product = productRepository
        .findByProductIdAndTenant(request.getProductId(), tenant)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    Favorite favorite = new Favorite();
    favorite.setUser(user);
    favorite.setProduct(product);

    Favorite savedFavorite = favoriteRepository.save(favorite);

    FavoriteResponse response = new FavoriteResponse();
    response.setFavoriteId(savedFavorite.getFavoriteId());
    response.setProductId(product.getProductId());
    response.setProductName(product.getProductName());

    return response;
}

    @Override
public List<FavoriteResponse> getFavoritesByUser(String tenantName,
                                                 Long userId) {

    tenantRepository.findByTenantName(tenantName)
        .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    validateUserAccess(userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    List<Favorite> favorites = favoriteRepository.findByUser(user);

    return favorites.stream().map(favorite -> {

        FavoriteResponse response = new FavoriteResponse();
        response.setFavoriteId(favorite.getFavoriteId());
        response.setProductId(favorite.getProduct().getProductId());
        response.setProductName(favorite.getProduct().getProductName());

        return response;

    }).toList();
}

    @Override
public void removeFavorite(String tenantName,
                           Long favoriteId) {

    tenantRepository.findByTenantName(tenantName)
        .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    Favorite favorite = favoriteRepository.findById(favoriteId)
            .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

    validateFavoriteAccess(favorite);

    favoriteRepository.delete(favorite);
}
}