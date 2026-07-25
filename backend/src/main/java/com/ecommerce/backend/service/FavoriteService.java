package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.AddFavoriteRequest;
import com.ecommerce.backend.dto.response.FavoriteResponse;

import java.util.List;

public interface FavoriteService {

    FavoriteResponse addFavorite(Long userId, AddFavoriteRequest request);

    List<FavoriteResponse> getFavoritesByUser(Long userId);

    void removeFavorite(Long favoriteId);
}