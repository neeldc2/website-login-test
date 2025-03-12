package com.example.website_login_1.dto;

public record ResetPasswordRequest(
        String password,
        String refreshToken
) {
}
