package com.qooco.boost.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StoreFilePurpose {
    FOR_MESSAGE(1, "File is using in message"),
    ;
    @Getter
    private final int code;
    @Getter
    private final String description;
}
