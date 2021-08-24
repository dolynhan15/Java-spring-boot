package com.qooco.boost.data.enumeration;

public enum AssessmentType {
    FREE(1),
    CLAIM(2),
    BUY(3),
    ALL(4);

    private final int code;

    AssessmentType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
