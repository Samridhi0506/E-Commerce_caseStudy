package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<OrderResponse> placeOrder(
            @PathVariable Long userId,
            @RequestBody CreateOrderRequest request) {

        OrderResponse response = orderService.placeOrder(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long orderId) {

        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully.");
    }
}