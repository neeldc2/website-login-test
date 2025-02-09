package com.example.website_login_1.aspect;

import com.example.website_login_1.annotation.ValidatePermission;
import com.example.website_login_1.usercontext.UserContext;
import com.example.website_login_1.usercontext.UserContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

@Aspect
@Component
public class PermissionValidationAspect {

    @Pointcut("@annotation(com.example.website_login_1.annotation.ValidatePermission)  || @within(com.example.website_login_1.annotation.ValidatePermission)")
    public void validatePermissionPointcut() {
        // Pointcut for methods annotated with @ValidatePermission
    }

    @Around("validatePermissionPointcut()")
    public Object validatePermissions(ProceedingJoinPoint joinPoint) throws Throwable {
        UserContext userContext = UserContextHolder.getUserContext();
        Set<String> userPermissions = userContext.permissions();

        // Retrieve required permissions from the annotation
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ValidatePermission validatePermission = method.getAnnotation(ValidatePermission.class);

        if (validatePermission != null && validatePermission.value() != null) {
            boolean hasPermission = Arrays.stream(validatePermission.value())
                    .allMatch(userPermissions::contains);

            if (!hasPermission) {
                throw new SecurityException("Access Denied: Insufficient permissions");
            }
        }

        return joinPoint.proceed();
    }
}
