package com.example.website_login_1.controller;

import com.example.website_login_1.annotation.ValidatePermission;
import com.example.website_login_1.dto.CreateTenantRequest;
import com.example.website_login_1.dto.CreateTenantResponse;
import com.example.website_login_1.dto.UpdateTenantRequest;
import com.example.website_login_1.dto.UpsertUserProfileRequest;
import com.example.website_login_1.service.UserService;
import com.example.website_login_1.usercontext.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthenticatedController {

    private final UserService userService;

    // This is also for basic auth
    // This is to test APIs using JWT
    @GetMapping("/test")
    @ValidatePermission({"MANAGE_USERS"})
    public String testAPI() {
        log.info("User is {}", UserContextHolder.getUserContext().userId());
        return "works";
    }

    /**
     * Create first tenant via SQL script. Add yourself as a user with MANAGE_TENANT permission.
     * Script is present in add_super_user.sql file
     * Then, you would be able to add tenants.
     *
     * @param createTenantRequest
     * @return
     */
    //@ValidatePermission({"MANAGE_TENANT"})
    @PostMapping("/register")
    public CreateTenantResponse registerTenant(@RequestBody CreateTenantRequest createTenantRequest) {
        return userService.registerTenant(createTenantRequest);
    }

    @ValidatePermission({"MANAGE_TENANT"})
    @PutMapping("/tenant")
    public void updateTenant(@RequestBody UpdateTenantRequest updateTenantRequest) {
        userService.updateTenant(updateTenantRequest);
    }

    @PutMapping("/user-profile")
    public void updateUserProfile(@RequestBody UpsertUserProfileRequest upsertUserProfileRequest) {
        userService.upsertUserProfile(upsertUserProfileRequest);
    }

    @DeleteMapping("/users")
    public void deleteAllUsers() {
        userService.deleteUsers();
    }

}
