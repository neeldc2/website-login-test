package com.example.website_login_1.controller;

import com.example.website_login_1.dto.CreateTenantUserRequest;
import com.example.website_login_1.dto.CreateUserRequest;
import com.example.website_login_1.dto.OAuthLoginAuthCode;
import com.example.website_login_1.dto.UserLoginResponse;
import com.example.website_login_1.enums.LoginType;
import com.example.website_login_1.exception.WebsiteException;
import com.example.website_login_1.service.UserService;
import com.example.website_login_1.utils.PasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.example.website_login_1.constant.WebsiteLoginConstants.Roles.USER;

@RestController
@Slf4j
@RequestMapping("/auth")
public class OAuthController {

    @Value("${website.website-login.registration-google-client-id}")
    private String clientId;

    @Value("${website.website-login.registration-google-client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @PostMapping("/google/callback")
    public UserLoginResponse handleGoogleCallback(
            @RequestBody OAuthLoginAuthCode oAuthLoginAuthCode,
            @RequestHeader("User-Agent") String userAgent,
            @RequestHeader("Ip-Address") String ipAddress
    ) {
        String authCode = oAuthLoginAuthCode.code();
        String redirectURI = oAuthLoginAuthCode.redirectURI();
        Long tenantId = oAuthLoginAuthCode.tenantId();
        UUID tenantGuid = oAuthLoginAuthCode.tenantGuid();

        String idToken = getIdTokenFromGoogle(authCode, redirectURI);
        String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            throw new WebsiteException("Something went wrong with Google Login");
        }

        Map<String, Object> userInfo = userInfoResponse.getBody();
        String email = (String) userInfo.get("email");

        boolean doesUserExists = userService.doesUserExists(email);

        if (!doesUserExists) {
            CreateUserRequest createUserRequest = CreateUserRequest.builder()
                    .email(email)
                    .username(email)
                    .roleNames(Set.of(USER))
                    .active(true)
                    .approved(false)
                    .password(PasswordGenerator.generateSecurePassword(10))
                    .build();
            CreateTenantUserRequest createTenantUserRequest = CreateTenantUserRequest.builder()
                    .tenantGuid(tenantGuid)
                    .createUserRequest(createUserRequest)
                    .build();
            userService.createTenantUser(createTenantUserRequest);
        }

        return userService.performUserLogin(email, tenantId, LoginType.GOOGLE_SSO, userAgent, ipAddress);
    }

    private String getIdTokenFromGoogle(
            final String authCode,
            final String redirectURI) {
        final String tokenEndpoint = "https://oauth2.googleapis.com/token";

        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectURI);
        params.add("grant_type", "authorization_code");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        final ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
        final String idToken = (String) tokenResponse.getBody().get("id_token");
        return idToken;
    }
}
