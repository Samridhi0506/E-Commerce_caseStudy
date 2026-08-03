package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.AddToCartRequest;
import com.ecommerce.backend.dto.response.CartResponse;
import com.ecommerce.backend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest request) {

        CartResponse response = cartService.addToCart(userId, request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartResponse>> getCart(
            @PathVariable Long userId) {

        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long cartId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                cartService.updateQuantity(cartId, quantity)
        );
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> removeFromCart(
            @PathVariable Long cartId) {

        cartService.removeFromCart(cartId);

        return ResponseEntity.ok("Item removed from cart successfully.");
    }
}