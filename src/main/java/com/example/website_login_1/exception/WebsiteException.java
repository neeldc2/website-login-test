package com.example.website_login_1.exception;

public class WebsiteException extends RuntimeException {

    public WebsiteException() {
        super();
    }

    public WebsiteException(String message) {
        super(message);
    }

    public WebsiteException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebsiteException(Throwable cause) {
        super(cause);
    }
}
