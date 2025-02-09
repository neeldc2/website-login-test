package com.example.website_login_1.dto;

import lombok.NonNull;

public record RefreshTokenRequest(
        @NonNull String refreshToken
) {
}
