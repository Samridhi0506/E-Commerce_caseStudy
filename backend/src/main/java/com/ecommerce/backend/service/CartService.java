package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.AddToCartRequest;
import com.ecommerce.backend.dto.response.CartResponse;

import java.util.List;

public interface CartService {

    CartResponse addToCart(Long userId, AddToCartRequest request);

    List<CartResponse> getCart(Long userId);

    CartResponse updateQuantity(Long cartId, Integer quantity);

    void removeFromCart(Long cartId);
}