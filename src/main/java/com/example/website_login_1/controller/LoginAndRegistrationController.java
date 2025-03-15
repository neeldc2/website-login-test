package com.example.website_login_1.controller;

import com.example.website_login_1.dto.CreateTenantUserRequest;
import com.example.website_login_1.dto.RefreshTokenRequest;
import com.example.website_login_1.dto.RefreshTokenResponse;
import com.example.website_login_1.dto.ResetPasswordRequest;
import com.example.website_login_1.dto.TenantInfoResponse;
import com.example.website_login_1.dto.UserLoginRequest;
import com.example.website_login_1.dto.UserLoginResponse;
import com.example.website_login_1.service.TenantService;
import com.example.website_login_1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.example.website_login_1.enums.LoginType.USERNAME_PASSWORD;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginAndRegistrationController {

    private final UserService userService;
    private final TenantService tenantService;

    @PostMapping("/user")
    public void createTenantUser(@RequestBody CreateTenantUserRequest createTenantUserRequest) {
        userService.userSignUp(createTenantUserRequest);
    }

    @PostMapping("/login")
    public UserLoginResponse userLogin(@RequestBody UserLoginRequest userLoginRequest,
                                       @RequestHeader("User-Agent") String userAgent,
                                       @RequestHeader("Ip-Address") String ipAddress) {
        try {
            return userService.userLoginViaUsernamePassword(userLoginRequest, userAgent, ipAddress);
        } catch (Exception exception) {
            userService.captureFailedUserLoginHistory(userLoginRequest, USERNAME_PASSWORD, userAgent, ipAddress, exception);
            throw exception;
        }
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse userLogin(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return userService.refreshToken(refreshTokenRequest);
    }

    @GetMapping("/tenants")
    public Set<TenantInfoResponse> getTenantInfoList() {
        return tenantService.getRegisteredTenants();
    }

    /**
     * Audit wont be needed if Email service captures all the emails sent
     *
     * @param email
     */
    @PostMapping("/reset-password-email")
    public void sendResetPasswordEmail(
            @RequestParam String email
    ) {
        userService.sendResetPasswordEmail(email);
    }

    /**
     * Audit wont be needed if Email service captures all the emails sent
     *
     * @param resetPasswordRequest
     */
    @PutMapping("/reset-password")
    public void resetPassword(
            @RequestBody ResetPasswordRequest resetPasswordRequest
    ) {
        userService.resetPassword(resetPasswordRequest);
    }

}
