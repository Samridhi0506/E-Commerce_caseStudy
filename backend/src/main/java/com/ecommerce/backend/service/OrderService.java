package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(Long userId, CreateOrderRequest request);

    List<OrderResponse> getOrdersByUser(Long userId);

    OrderResponse getOrderById(Long orderId);

    void cancelOrder(Long orderId);
}