package com.example.website_login_1.usercontext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.website_login_1.constant.WebsiteLoginConstants.USER_CONTEXT_ATTRIBUTE;

/**
 * Explanation on difference between Filter and Interceptor
 * https://stackoverflow.com/questions/35856454/difference-between-interceptor-and-filter-in-spring-mvc
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler)
            throws Exception {

        boolean containsUserContext = false;
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String currentAttributeName = attributeNames.nextElement();
            if (USER_CONTEXT_ATTRIBUTE.equals(currentAttributeName)) {
                containsUserContext = true;
            }
        }

        if (containsUserContext) {
            UserContext userContext = (UserContext) request.getAttribute(USER_CONTEXT_ATTRIBUTE);
            UserContextHolder.setUserContext(userContext);
        } else {
            setUserContextFromHeader(request);
        }

        // It tells Spring to further process the request (true) or not (false).
        return true;
    }

    @Override
    public void afterCompletion(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Exception ex)
            throws Exception {
        // Clear the ThreadLocal to prevent memory leaks
        UserContextHolder.clear();
    }

    private void setUserContextFromHeader(
            final HttpServletRequest request
    ) {
        String userId = request.getHeader("x-user-id");
        String tenantName = request.getHeader("x-tenant");
        String tenantId = request.getHeader("x-tenant-id");
        String tenantGuid = request.getHeader("x-tenant-guid");
        String permissionSetString = request.getHeader("x-permissions");

        if (!StringUtils.hasText(userId)) {
            return;
        }

        UserContext userContext;
        UserContext.UserContextBuilder builder = UserContext.builder();
        builder.userId(UUID.fromString(userId));
        builder.tenant(tenantName);
        builder.tenantId(Long.parseLong(tenantId));
        builder.tenantGuid(UUID.fromString(tenantGuid));
        Set<String> permissions = Stream.of(
                        permissionSetString.substring(1, permissionSetString.length() - 1).split(",\\s*"))
                .collect(Collectors.toSet());
        builder.permissions(permissions);

        userContext = builder.build();
        UserContextHolder.setUserContext(userContext);
    }
}
