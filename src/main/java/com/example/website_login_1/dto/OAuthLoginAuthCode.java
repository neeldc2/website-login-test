package com.example.website_login_1.dto;

import lombok.NonNull;

import java.util.UUID;

public record OAuthLoginAuthCode(
        @NonNull String code,
        @NonNull String redirectURI,
        @NonNull Long tenantId,
        @NonNull UUID tenantGuid
) {
}
