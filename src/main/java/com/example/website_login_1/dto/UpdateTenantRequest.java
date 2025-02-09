package com.example.website_login_1.dto;

import lombok.NonNull;

import java.util.UUID;

public record UpdateTenantRequest(
        @NonNull UUID tenantGuid,
        @NonNull boolean enable
) {
}
