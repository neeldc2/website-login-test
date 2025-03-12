package com.example.website_login_1.dto;

import lombok.NonNull;

public record CreateTenantRequest(
        @NonNull String tenantName,
        @NonNull String tenantCode,
        CreateUserRequest createUserRequest
) {
}
