package com.qooco.boost.threads.notifications.enumeration;

import lombok.Getter;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/31/2018 - 4:04 PM
 */
public enum PushType {
    SILENT(0),
    NOTIFICATION(1);

    @Getter
    private int value;

    PushType(int value) {
        this.value = value;
    }
}
