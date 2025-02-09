package com.example.website_login_1.usercontext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

import static com.example.website_login_1.constant.WebsiteLoginConstants.USER_CONTEXT_ATTRIBUTE;

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
}
