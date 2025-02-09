package com.example.website_login_1.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WebsiteLoginConstants {
    public static String ADMIN_ROLE_NAME = "ADMIN";
    public static String USER_CONTEXT_ATTRIBUTE = "userContext";

    public static class Permissions {
        public static String CREATE_POST = "CREATE_POST";
        public static String DELETE_POST = "DELETE_POST";
        public static String EDIT_PROFILE = "EDIT_PROFILE";
        public static String MANAGE_USERS = "MANAGE_USERS";
    }
}
