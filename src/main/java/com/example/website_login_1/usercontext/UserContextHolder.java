package com.example.website_login_1.usercontext;

public class UserContextHolder {

    private static final ThreadLocal<UserContext> userContextThreadLocal = new ThreadLocal<>();

    public static void setUserContext(UserContext userContext) {
        userContextThreadLocal.set(userContext);
    }

    public static UserContext getUserContext() {
        return userContextThreadLocal.get();
    }

    public static void clear() {
        userContextThreadLocal.remove();
    }
}
