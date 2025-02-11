package com.example.website_login_1.controller;

import com.example.website_login_1.annotation.ValidatePermission;
import com.example.website_login_1.dto.UpsertUserProfileRequest;
import com.example.website_login_1.service.UserService;
import com.example.website_login_1.usercontext.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PutMapping("/user-profile")
    public void updateUserProfile(@RequestBody UpsertUserProfileRequest upsertUserProfileRequest) {
        userService.upsertUserProfile(upsertUserProfileRequest);
    }

    @DeleteMapping("/users")
    public void deleteAllUsers() {
        userService.deleteUsers();
    }

}
