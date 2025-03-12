package com.example.website_login_1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlGeneratorService {

    private final JwtService jwtService;

    private static final String BASE_URL = "https://website.com";
    private static final String QUERY_PARAM_QUESTION = "?";
    private static final String REFRESH_TOKEN = "refresh=";
    private static final String RESET_PASSWORD = "/reset";

    public String getUrlForResetPassword(
            final String tenantAdminEmail,
            final Long tenantId
    ) {
        final String refreshToken = jwtService.generateRefreshTokenForNewTenant(
                tenantAdminEmail,
                tenantId);
        final StringBuilder url = new StringBuilder(BASE_URL);
        url.append(RESET_PASSWORD);
        url.append(QUERY_PARAM_QUESTION);
        url.append(REFRESH_TOKEN);
        url.append(refreshToken);
        return url.toString();
    }
}
