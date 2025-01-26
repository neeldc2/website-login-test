package com.example.website_login_1.service;

import com.example.website_login_1.dto.CreateUserRequest;
import com.example.website_login_1.dto.UserLoginRequest;
import com.example.website_login_1.dto.UserLoginResponse;
import com.example.website_login_1.entity.User;
import com.example.website_login_1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void createUser(final CreateUserRequest createUserRequest) {
        User user = User.builder()
                .email(createUserRequest.email())
                .enabled(true)
                .username(createUserRequest.username())
                // TODO: add salt
                .password(encoder.encode(createUserRequest.password()))
                .build();
        userRepository.save(user);
    }

    public UserLoginResponse userLogin(final UserLoginRequest userLoginRequest) {
        // UsernamePasswordAuthenticationFilter is the default filter
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.email(), userLoginRequest.password()));
        // The above code throws BadCredentialsException if username/password is invalid.
        // Below code will not run if username/password is invalid.
        // So, the below if code is not required
        if (authentication.isAuthenticated()) {
            System.out.println("Good creds");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //String jwtToken = jwtService.generateJwtToken(userDetails.getUsername());
        String jwtToken = jwtService.generateJwtToken(userLoginRequest.email());

        return UserLoginResponse.builder()
                .accessToken(jwtToken)
                .build();
    }
}
