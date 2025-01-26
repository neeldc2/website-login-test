package com.example.website_login_1.filter;

import com.example.website_login_1.dto.UserContext;
import com.example.website_login_1.entity.User;
import com.example.website_login_1.repository.UserRepository;
import com.example.website_login_1.service.JwtService;
import com.example.website_login_1.service.SpringBootSecurityUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationJwtTokenFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION = "Authorization";
    @Autowired
    private JwtService jwtService;

    @Autowired
    private SpringBootSecurityUserDetailService springBootSecurityUserDetailService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {
        String authorisationHeader = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(authorisationHeader) &&
                authorisationHeader.startsWith("Bearer ") &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = authorisationHeader.substring(7);
            // This validated JWT as well
            String email = jwtService.getSubject(token);

            // User Repository call can be made as well if more information from user table is needed.
            UserDetails userDetails = springBootSecurityUserDetailService.loadUserByUsername(email);

            if (userDetails.getUsername().equals(email)) {
                // Once the JWT has been validated, create a new Authentication Object.
                // This Authentication Object has to be set in the Context.
                // The next Authentication filter uses this Authentication Object and understand that it does not have to authenticate
                // Similar to the check above "SecurityContextHolder.getContext().getAuthentication() == null"
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                User user = userRepository.findByEmail(email).get();
                UserContext userContextDto =
                        UserContext.builder()
                                .userId(user.getId())
                                .build();
                request = addUserDetailsToHeader(request, userContextDto);
            }
        }

        filterChain.doFilter(request, response);
    }

    private HttpServletRequest addUserDetailsToHeader(final HttpServletRequest request,
                                                      final UserContext userContextDto) {
        return new HttpServletRequestWrapper(request) {

            final String userId = userContextDto.userId().toString();

            /*@Override
            public String getHeader(String name) {
                if ("x-user-id".equalsIgnoreCase(name)) {
                    return userId;
                }
                return super.getHeader(name);
            }*/

            @Override
            public Enumeration<String> getHeaders(String name) {
                if ("x-user-id".equalsIgnoreCase(name)) {
                    return Collections.enumeration(Collections.singletonList(userId));
                }
                return super.getHeaders(name);
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-user-id", userId);
                headers.putAll(Collections.list(super.getHeaderNames())
                        .stream()
                        .collect(HashMap::new, (m, v) -> m.put(v, super.getHeader(v)), Map::putAll));
                return Collections.enumeration(headers.keySet());
            }
        };
    }

}
