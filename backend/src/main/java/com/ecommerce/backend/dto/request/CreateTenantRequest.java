package com.ecommerce.backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTenantRequest {

    private String tenantName;
    private String domain;
}