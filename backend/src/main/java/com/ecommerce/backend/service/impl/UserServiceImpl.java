package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.response.OrderItemResponse;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.dto.response.UserResponse;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderItem;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.OrderItemRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public UserServiceImpl(UserRepository userRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        return mapToResponse(user);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        return orderRepository.findByUserOrderByOrderDateDesc(user).stream()
                .map(this::mapOrderToResponse)
                .toList();
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getRoleName() : null)
                .tenant(user.getTenant() != null ? user.getTenant().getTenantName() : null)
                .build();
    }

    private OrderResponse mapOrderToResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrder(order);

        List<OrderItemResponse> itemResponses = items.stream().map(item ->
                OrderItemResponse.builder()
                        .productName(item.getProduct().getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build()
        ).toList();

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .items(itemResponses)
                .build();
    }
}
