package com.qooco.boost.data.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ContactPersonStatus {
    NORMAL(0, "Contact persion is normal"),
    CHANGED(1, "Contact persion is changed"),
    NEW(2, "New contact person");

    @Getter
    private final int code;
    @Getter
    private final String description;
}
