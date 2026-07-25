package com.ecommerce.backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    private Long productId;
    private Integer quantity;
}