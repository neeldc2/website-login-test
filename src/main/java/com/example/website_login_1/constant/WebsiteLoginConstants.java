package com.example.website_login_1.constant;

import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class WebsiteLoginConstants {
    public static final String USER_CONTEXT_ATTRIBUTE = "userContext";

    public static class Permissions {
        public static final String MANAGE_TENANT = "MANAGE_TENANT";
        public static final String ADD_ADMIN = "ADD_ADMIN";
        public static final String CREATE_POST = "CREATE_POST";
        public static final String DELETE_POST = "DELETE_POST";
        public static final String MANAGE_USERS = "MANAGE_USERS";
        public static final String APPROVED_USER = "APPROVED_USER";
        public static final String EDIT_PROFILE = "EDIT_PROFILE";
    }

    public static class Roles {
        public static final String ADMIN = "ADMIN";
        public static final String MODERATOR = "MODERATOR";
        public static final String STUDENT = "STUDENT";
        public static final String USER = "USER";

        public static final Set<String> ROLES_ALLOWED_FOR_USER_SIGNUP = Set.of(USER);
    }

    public static class KafkaConstants {
        public static final String NOTIFICATION_EMAIL_TOPIC = "notifications-email";
        public static final String NOTIFICATION_EMAIL_TOPIC_CONSUMER_GROUP = "notifications-email-consumer-group";
    }

    public static class ActiveMqConstants {
        public static final String EMAIL_QUEUE = "emails";
    }
}
