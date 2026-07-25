package com.ecommerce.backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddFavoriteRequest {

    private Long productId;
}