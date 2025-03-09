package com.example.website_login_1.dto;

import lombok.NonNull;

public record UserLoginRequest(
        @NonNull String email,
        @NonNull String password,
        Long tenantId // needed only for multi-tenant user
) {
}
