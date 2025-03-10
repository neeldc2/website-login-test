package com.example.website_login_1.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WebsiteLoginConstants {
    public static final String ADMIN_ROLE_NAME = "ADMIN";
    public static final String USER_CONTEXT_ATTRIBUTE = "userContext";

    public static class Permissions {
        public static final String CREATE_POST = "CREATE_POST";
        public static final String DELETE_POST = "DELETE_POST";
        public static final String EDIT_PROFILE = "EDIT_PROFILE";
        public static final String MANAGE_USERS = "MANAGE_USERS";
    }

    public static class Roles {
        public static final String STUDENT = "STUDENT";
    }

    public static class KafkaConstants {
        public static final String NOTIFICATION_EMAIL_TOPIC = "notifications-email";
        public static final String NOTIFICATION_EMAIL_TOPIC_CONSUMER_GROUP = "notifications-email-consumer-group";
    }
}
