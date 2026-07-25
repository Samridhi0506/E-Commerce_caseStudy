package com.ecommerce.backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    private String username;
    private String email;
    private Long roleId;
    private Long tenantId;
}