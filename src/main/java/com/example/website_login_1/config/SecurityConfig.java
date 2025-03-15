package com.example.website_login_1.config;

import com.example.website_login_1.filter.AuthenticationJwtTokenFilter;
import com.example.website_login_1.service.SpringBootSecurityUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

import static org.springframework.http.HttpMethod.OPTIONS;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationJwtTokenFilter authenticationJwtTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO: pass a strength in constructor if needed
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    //configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:80"));
                    configuration.setAllowedOrigins(Arrays.asList("*"));
                    configuration.setAllowedMethods(Arrays.asList("*"));
                    configuration.setAllowedHeaders(Arrays.asList("*"));
                    return configuration;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(OPTIONS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/tenants").permitAll()
                        .requestMatchers(HttpMethod.POST, "/tenants", "/user", "/login", "/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/google/callback", "/reset-password-email").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/tenants", "/reset-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/messaging/**").permitAll()
                        //.requestMatchers("/register", "/login", "/tenant").permitAll()
                        //.requestMatchers(HttpMethod.POST, "/register").permitAll()
                        //.requestMatchers(HttpMethod.POST, "/login").permitAll()
                        //.requestMatchers(HttpMethod.POST, "/tenant").permitAll()
                        //.requestMatchers("/login").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                //.addFilterAfter(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            final SpringBootSecurityUserDetailService springBootSecurityUserDetailService,
            final PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(springBootSecurityUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * If you are using simple Auth, then AuthenticationManager bean is not needed to be defined.
     * This is needed only if you are going with JWT.
     * Authentication Manager talks to Authentication Provider.
     *
     * @param authConfig
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
