package com.ecommerce.backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {

    private Long favoriteId;
    private Long productId;
    private String productName;
}