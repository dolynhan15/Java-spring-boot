package com.qooco.boost.data.enumeration.doc;

public enum SystemLoggerDocEnum {
    ID("id"),
    URL("url"),
    USERNAME("username"),
    APP_ID("appId"),
    BOOST_TOKEN("boostToken"),
    ROLES("roles"),
    COMPANY_ID("companyId"),
    REQUEST_DATA("requestData"),
    CREATED_DATE("createdDate"),

    RESPONSE("responseData"),
    RESPONSE_DATE("responseDate"),

    STACK_TRACE("stackTrace");

    private final String key;

    SystemLoggerDocEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
