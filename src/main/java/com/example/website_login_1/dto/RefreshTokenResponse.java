package com.example.website_login_1.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record RefreshTokenResponse(
        @NonNull String accessToken
) {
}
