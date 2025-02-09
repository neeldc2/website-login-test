package com.example.website_login_1.dto;

import lombok.Builder;
import lombok.NonNull;

import java.util.Set;

@Builder
public record CreateUserRequest(
        @NonNull String username,
        @NonNull String email,
        @NonNull String password,
        @NonNull Set<String> roleNames
) {
}
