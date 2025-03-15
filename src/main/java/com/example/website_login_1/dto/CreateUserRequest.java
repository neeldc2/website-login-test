package com.example.website_login_1.dto;

import lombok.Builder;
import lombok.NonNull;

import java.util.Set;

@Builder
public record CreateUserRequest(
        @NonNull String username,
        @NonNull String email,
        @NonNull String password,
        boolean active,
        boolean approved,
        @NonNull Set<String> roleNames
) {
}
