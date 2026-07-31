package com.ecommerce.backend.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {

    private Long favoriteId;
    private Long productId;
    private String productName;

    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private String tenant;
}