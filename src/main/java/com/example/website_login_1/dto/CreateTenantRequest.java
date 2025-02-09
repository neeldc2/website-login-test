package com.example.website_login_1.dto;

public record CreateTenantRequest(
        String tenantName,
        CreateUserRequest createUserRequest
) {
}
