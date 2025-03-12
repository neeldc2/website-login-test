package com.example.website_login_1.dto;

import com.example.website_login_1.entity.Tenant;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TenantInfoResponse(
        UUID tenantGuid,
        Long tenantId,
        String tenantName
) {

    public static TenantInfoResponse getTenantInfoResponse(
            final Tenant tenant
    ) {
        return TenantInfoResponse.builder()
                .tenantGuid(tenant.getGuid())
                .tenantId(tenant.getId())
                .tenantName(tenant.getName())
                .build();
    }
}
