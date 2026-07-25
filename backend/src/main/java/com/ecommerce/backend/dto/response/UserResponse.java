package com.ecommerce.backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long userId;
    private String username;
    private String email;
    private String role;
    private String tenant;
}