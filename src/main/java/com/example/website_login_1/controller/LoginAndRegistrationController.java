package com.example.website_login_1.controller;

import com.example.website_login_1.dto.CreateTenantRequest;
import com.example.website_login_1.dto.CreateTenantResponse;
import com.example.website_login_1.dto.CreateTenantUserRequest;
import com.example.website_login_1.dto.RefreshTokenRequest;
import com.example.website_login_1.dto.RefreshTokenResponse;
import com.example.website_login_1.dto.UpdateTenantRequest;
import com.example.website_login_1.dto.UserLoginRequest;
import com.example.website_login_1.dto.UserLoginResponse;
import com.example.website_login_1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginAndRegistrationController {

    private final UserService userService;

    @PostMapping("/register")
    public CreateTenantResponse registerTenant(@RequestBody CreateTenantRequest createTenantRequest) {
        return userService.registerTenant(createTenantRequest);
    }

    @PutMapping("/tenant")
    public void updateTenant(@RequestBody UpdateTenantRequest updateTenantRequest) {
        userService.updateTenant(updateTenantRequest);
    }

    @PostMapping("/user")
    public void createTenantUser(@RequestBody CreateTenantUserRequest createTenantUserRequest) {
        userService.createTenantUser(createTenantUserRequest);
    }

    @PostMapping("/login")
    public UserLoginResponse userLogin(@RequestBody UserLoginRequest userLoginRequest,
                                       @RequestHeader("User-Agent") String userAgent,
                                       @RequestHeader("Ip-Address") String ipAddress) {
        try {
            return userService.userLoginViaUsernamePassword(userLoginRequest, userAgent, ipAddress);
        } catch (Exception exception) {
            userService.captureFailedUserLoginHistory(userLoginRequest, userAgent, ipAddress, exception);
            throw exception;
        }
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse userLogin(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return userService.refreshToken(refreshTokenRequest);
    }

}
