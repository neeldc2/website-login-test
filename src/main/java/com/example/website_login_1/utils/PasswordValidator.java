package com.example.website_login_1.utils;

import java.util.regex.Pattern;

public class PasswordValidator {

    // Regular expressions for password validation
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");

    public static boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }

        // Check length
        if (password.length() < 8) {
            return false;
        }

        // Check for at least one uppercase letter
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return false;
        }

        // Check for at least one lowercase letter
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return false;
        }

        // Check for at least one digit
        if (!DIGIT_PATTERN.matcher(password).find()) {
            return false;
        }

        // Check for at least one special character
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return false;
        }

        return true; // Password is strong
    }

}
