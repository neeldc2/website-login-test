package com.example.website_login_1.filter;

import com.example.website_login_1.service.JwtService;
import com.example.website_login_1.service.SpringBootSecurityUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
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

@Component
public class AuthenticationJwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SpringBootSecurityUserDetailService springBootSecurityUserDetailService;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {
        String authorisationHeader = request.getHeader("Authorization");

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
            }
        }

        filterChain.doFilter(request, response);
    }
}
