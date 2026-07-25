package com.ecommerce.backend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantResponse {

    private Long tenantId;
    private String tenantName;
    private String domain;
}