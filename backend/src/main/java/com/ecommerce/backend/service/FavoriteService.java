package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.AddFavoriteRequest;
import com.ecommerce.backend.dto.response.FavoriteResponse;

import java.util.List;

public interface FavoriteService {

    FavoriteResponse addFavorite(String tenantName,
                                 Long userId,
                                 AddFavoriteRequest request);

    List<FavoriteResponse> getFavoritesByUser(String tenantName,
                                              Long userId);

    void removeFavorite(String tenantName,
                        Long favoriteId);
}