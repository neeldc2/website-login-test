package com.example.website_login_1.dto;

import lombok.Builder;
import lombok.NonNull;

import java.util.UUID;

@Builder
public record CreateTenantUserRequest(
        @NonNull UUID tenantGuid,
        @NonNull CreateUserRequest createUserRequest
) {
}
