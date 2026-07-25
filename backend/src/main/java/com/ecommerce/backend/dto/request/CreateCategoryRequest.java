package com.ecommerce.backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequest {

    private String categoryName;
}