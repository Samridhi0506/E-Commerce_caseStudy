package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.AddFavoriteRequest;
import com.ecommerce.backend.dto.response.FavoriteResponse;
import com.ecommerce.backend.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{tenantName}/user/{userId}")
public ResponseEntity<FavoriteResponse> addFavorite(
        @PathVariable String tenantName,
        @PathVariable Long userId,
        @RequestBody AddFavoriteRequest request) {

    FavoriteResponse response =
            favoriteService.addFavorite(tenantName, userId, request);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
}

    @GetMapping("/{tenantName}/user/{userId}")
public ResponseEntity<List<FavoriteResponse>> getFavoritesByUser(
        @PathVariable String tenantName,
        @PathVariable Long userId) {

    return ResponseEntity.ok(
            favoriteService.getFavoritesByUser(tenantName, userId));
}

    @DeleteMapping("/{tenantName}/{favoriteId}")
public ResponseEntity<String> removeFavorite(
        @PathVariable String tenantName,
        @PathVariable Long favoriteId) {

    favoriteService.removeFavorite(tenantName, favoriteId);

    return ResponseEntity.ok("Favorite removed successfully.");
}
}