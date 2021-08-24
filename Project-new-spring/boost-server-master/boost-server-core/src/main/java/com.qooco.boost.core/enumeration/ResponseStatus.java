package com.qooco.boost.core.enumeration;

import lombok.Getter;

public enum ResponseStatus {
    SUCCESS(1, "Success"),
    LOGIN_UNAUTHORIZED(4, "Unauthorized"),
    BAD_REQUEST(6, "Bad request"),
    TOKEN_MISSING(7, "Token is missing on header"),
    NO_PERMISSION_TO_ACCESS(32, "Access is denied"),

    ;
    @Getter
    private final int code;
    @Getter
    private final String description;

    ResponseStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return code + " - " + description;
    }
}
