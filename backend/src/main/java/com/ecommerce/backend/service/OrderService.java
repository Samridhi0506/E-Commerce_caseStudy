package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(String tenantName,
                             Long userId,
                             CreateOrderRequest request);

    List<OrderResponse> getOrdersByUser(String tenantName,
                                        Long userId);

    OrderResponse getOrderById(String tenantName,
                               Long orderId);

    void cancelOrder(String tenantName,
                     Long orderId);
}