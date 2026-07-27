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

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               UserRepository userRepository,
                               ProductRepository productRepository) {

        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
public FavoriteResponse addFavorite(Long userId, AddFavoriteRequest request) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Product product = productRepository.findById(request.getProductId())
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
public List<FavoriteResponse> getFavoritesByUser(Long userId) {

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
public void removeFavorite(Long favoriteId) {

    Favorite favorite = favoriteRepository.findById(favoriteId)
            .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

    favoriteRepository.delete(favorite);
}
}