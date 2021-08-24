package com.qooco.boost.core.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum ApplicationConfig {
    BOOST_CORE_SECURITY_TOKEN_NAME("boost-token"),
    BOOST_CORE_SECURITY_TIMEZONE("timezone"),
    BOOST_CORE_SECURITY_APP_VERSION_CODE("app-version"),
    BOOST_CORE_SECURITY_APP_LOCALE("locale"),
    BOOST_CORE_SECURITY_APP_VERSION_NAME("app-version-name"),
    BOOST_CORE_SECURITY_APP_ID("app-id");

    @Getter @Accessors(fluent = true)
    private final String value;
}
