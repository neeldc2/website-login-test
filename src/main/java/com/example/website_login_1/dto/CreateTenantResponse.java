package com.example.website_login_1.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateTenantResponse(
        Long tenantId,
        UUID tenantGuid
) {
}
