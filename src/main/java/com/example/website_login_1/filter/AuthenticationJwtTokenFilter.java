package com.example.website_login_1.filter;

import com.example.website_login_1.entity.User;
import com.example.website_login_1.repository.UserRepository;
import com.example.website_login_1.service.JwtService;
import com.example.website_login_1.service.SpringBootSecurityUserDetailService;
import com.example.website_login_1.usercontext.UserContext;
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

import static com.example.website_login_1.constant.WebsiteLoginConstants.USER_CONTEXT_ATTRIBUTE;

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
            final UserContext userContext = jwtService.getUserContext(token);

            // User Repository call can be made as well if more information from user table is needed.
            // This call can be skipped as well
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
                request.setAttribute(USER_CONTEXT_ATTRIBUTE, userContext);
                request = addUserDetailsToHeader(request, userContext);
            }
        }

        filterChain.doFilter(request, response);
    }

    private HttpServletRequest addUserDetailsToHeader(final HttpServletRequest request,
                                                      final UserContext userContextDto) {
        return new HttpServletRequestWrapper(request) {

            final String userId = userContextDto.userId().toString();
            final String tenant = userContextDto.tenant();
            final String tenantId = userContextDto.tenantId().toString();
            final String tenantGuid = userContextDto.tenantGuid().toString();
            final String permissionSet = userContextDto.permissions().toString();

            /*@Override
            public String getHeader(String tenantName) {
                if ("x-user-id".equalsIgnoreCase(tenantName)) {
                    return userId;
                }
                return super.getHeader(tenantName);
            }*/

            @Override
            public Enumeration<String> getHeaders(String name) {
                if ("x-user-id".equalsIgnoreCase(name)) {
                    return Collections.enumeration(Collections.singletonList(userId));
                }
                if ("x-tenant".equalsIgnoreCase(name)) {
                    return Collections.enumeration(Collections.singletonList(tenant));
                }
                if ("x-tenant-id".equalsIgnoreCase(name)) {
                    return Collections.enumeration(Collections.singletonList(tenantId));
                }
                if ("x-tenant-guid".equalsIgnoreCase(name)) {
                    return Collections.enumeration(Collections.singletonList(tenantGuid));
                }
                if ("x-permissions".equalsIgnoreCase(name)) {
                    return Collections.enumeration(Collections.singletonList(permissionSet));
                }
                return super.getHeaders(name);
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-user-id", userId);
                headers.put("x-tenant", tenant);
                headers.put("x-tenant-id", tenantId);
                headers.put("x-tenant-guid", tenantGuid);
                headers.put("x-permissions", permissionSet);
                headers.putAll(Collections.list(super.getHeaderNames())
                        .stream()
                        .collect(HashMap::new, (m, v) -> m.put(v, super.getHeader(v)), Map::putAll));
                return Collections.enumeration(headers.keySet());
            }
        };
    }

}
