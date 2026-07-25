package com.ecommerce.backend.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    private String productName;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
    private Long tenantId;
}