package com.qooco.boost.data.enumeration.doc;

public enum SocketConnectionDocEnum {
    TOKEN("token"),
    USERNAME("username"),
    USER_PROFILE_ID("userProfileId"),
    APP_ID("appId"),
    SESSION_IDS("sessionIds"),
    CHANNELS("channels"),
    IS_LOGOUT("isLogout"),
    LASTED_ONLINE_DATE("lastedOnlineDate"),
    UPDATED_DATE("updatedDate");

    private final String key;

    SocketConnectionDocEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
