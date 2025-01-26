package com.example.website_login_1.dto;

import lombok.NonNull;

public record CreateUserRequest(
        @NonNull String username,
        @NonNull String email,
        @NonNull String password
) {
}
