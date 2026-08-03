package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.AddToCartRequest;
import com.ecommerce.backend.dto.response.CartResponse;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.CartService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
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

        private CartResponse mapToResponse(Cart cart) {

        Product product = cart.getProduct();

        CartResponse response = new CartResponse();

        response.setCartId(cart.getCartId());
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(cart.getQuantity());

        response.setSubtotal(
                product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()))
        );

        response.setStock(product.getStock());
        response.setCategory(product.getCategory().getCategoryName());
        response.setTenant(product.getTenant().getTenantName());

        return response;
    }

    @Override
public CartResponse addToCart(Long userId, AddToCartRequest request) {

    validateUserAccess(userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    Cart cart = cartRepository.findByUserAndProduct(user, product)
            .orElse(null);

    if (cart != null) {
        cart.setQuantity(cart.getQuantity() + request.getQuantity());
    } else {
        cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(request.getQuantity());
    }

    Cart savedCart = cartRepository.save(cart);

    return mapToResponse(savedCart);
}

@Override
public List<CartResponse> getCart(Long userId) {

    validateUserAccess(userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return cartRepository.findByUser(user)
            .stream()
            .map(this::mapToResponse)
            .toList();
}

@Override
public CartResponse updateQuantity(Long cartId, Integer quantity) {

    Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

    validateUserAccess(cart.getUser().getUserId());

    if (quantity <= 0) {
        cartRepository.delete(cart);
        return null;
    }

    cart.setQuantity(quantity);

    Cart updatedCart = cartRepository.save(cart);

    return mapToResponse(updatedCart);
}

@Override
public void removeFromCart(Long cartId) {

    Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

    validateUserAccess(cart.getUser().getUserId());

    cartRepository.delete(cart);
}

}