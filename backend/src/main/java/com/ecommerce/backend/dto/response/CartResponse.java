package com.ecommerce.backend.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartResponse {

    private Long cartId;

    private Long productId;

    private String productName;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;

    private Integer stock;

    private String category;

    private String tenant;
}