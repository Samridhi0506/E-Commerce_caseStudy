package com.ecommerce.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tenantId;

    @Column(nullable = false, unique = true)
    private String tenantName;

    @Column(unique = true)
    private String domain;
}