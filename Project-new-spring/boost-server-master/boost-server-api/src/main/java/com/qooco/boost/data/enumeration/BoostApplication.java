package com.qooco.boost.data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public enum  BoostApplication {
    SELECT_APP(1, "com.boost.select"), //RECEIVE_IN_HOTEL_APP = 1
    PROFILE_APP(2, "com.boost.profile"), //RECEIVE_IN_CAREER_APP = 2
    WEB_ADMIN_APP(3, "com.boost.web.admin");

    @Getter @Accessors(fluent = true)
    private final int value;
    @Getter @Accessors(fluent = true)
    private final String appId;

    public static BoostApplication fromValue(Integer appValue) {
        return ofNullable(appValue)
                .map(value -> Arrays.stream(BoostApplication.values())
                        .filter(it -> it.value == value)
                        .findFirst().orElse(null))
                .orElse(null);
    }

    public static BoostApplication fromAppId(String appId) {
        return ofNullable(appId)
                .map(value -> Arrays.stream(BoostApplication.values())
                        .filter(it -> it.appId.equals(value))
                        .findFirst().orElse(null))
                .orElse(null);
    }
}
