package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.CreateOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

   @PreAuthorize("hasAnyRole('USER','TENANT')")
   @PostMapping("/{tenantName}/user/{userId}")
public ResponseEntity<OrderResponse> placeOrder(
        @PathVariable String tenantName,
        @PathVariable Long userId,
        @RequestBody CreateOrderRequest request) {

    OrderResponse response =
            orderService.placeOrder(tenantName, userId, request);

    return new ResponseEntity<>(response, HttpStatus.CREATED);
}

    @PreAuthorize("hasAnyRole('USER','TENANT')")
    @GetMapping("/{tenantName}/user/{userId}")
public ResponseEntity<List<OrderResponse>> getOrdersByUser(
        @PathVariable String tenantName,
        @PathVariable Long userId) {

    return ResponseEntity.ok(
            orderService.getOrdersByUser(tenantName, userId));
}

    @PreAuthorize("hasAnyRole('USER','TENANT')")
    @GetMapping("/{tenantName}/{orderId}")
public ResponseEntity<OrderResponse> getOrderById(
        @PathVariable String tenantName,
        @PathVariable Long orderId) {

    return ResponseEntity.ok(
            orderService.getOrderById(tenantName, orderId));
}

    @PreAuthorize("hasAnyRole('USER','TENANT')")
    @DeleteMapping("/{tenantName}/{orderId}")
public ResponseEntity<String> cancelOrder(
        @PathVariable String tenantName,
        @PathVariable Long orderId) {

    orderService.cancelOrder(tenantName, orderId);

    return ResponseEntity.ok("Order cancelled successfully.");
}
}