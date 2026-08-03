package com.ecommerce.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ecommerce.backend.model.OrderStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Long userId;
    private String customerName;
    private List<OrderItemResponse> items;
}