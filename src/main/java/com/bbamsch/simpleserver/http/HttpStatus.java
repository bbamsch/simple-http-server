package com.bbamsch.simpleserver.http;

/**
 * Defines HTTP Status Codes & Corresponding Messages
 */
public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
