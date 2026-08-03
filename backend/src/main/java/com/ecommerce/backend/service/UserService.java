package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long userId);

    List<OrderResponse> getOrdersByUserId(Long userId);
}
