package com.qooco.boost.data.enumeration;

import java.util.Objects;

public enum AppointmentLockedStatus {
    NONE(0),
    LOCKED_EDIT(1),
    LOCKED_DELETE(2),
    LOCKED_ALL(3);

    public static AppointmentLockedStatus fromValue(Integer value) {
        if (Objects.isNull(value)) {
            return null;
        }
        switch (value) {
            case 0:
                return NONE;
            case 1:
                return LOCKED_EDIT;
            case 2:
                return LOCKED_DELETE;
            case 3:
                return LOCKED_ALL;
            default:
                return null;
        }
    }

    private final int value;

    public int getValue() {
        return value;
    }

    AppointmentLockedStatus(int value) {
        this.value = value;
    }
}
