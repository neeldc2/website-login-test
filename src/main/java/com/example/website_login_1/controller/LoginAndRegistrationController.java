package com.example.website_login_1.controller;

import com.example.website_login_1.dto.CreateUserRequest;
import com.example.website_login_1.dto.UserLoginRequest;
import com.example.website_login_1.dto.UserLoginResponse;
import com.example.website_login_1.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginAndRegistrationController {

    private final UserService userService;

    @PostMapping("/register")
    public void registerUser(@RequestBody CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
    }

    @PostMapping("/login")
    public UserLoginResponse userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        return userService.userLogin(userLoginRequest);
    }

    // This is for basic auth
    @GetMapping("test")
    public String testAPI() {
        return "works";
    }
}
