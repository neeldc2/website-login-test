package com.example.website_login_1.exception;

import lombok.Builder;

@Builder
public record ExceptionHandlerDto(
        String message
) {
}
