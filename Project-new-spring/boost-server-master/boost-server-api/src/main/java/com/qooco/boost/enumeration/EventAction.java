package com.qooco.boost.enumeration;

import java.util.Objects;

public enum EventAction {
    ADD(1),
    CHANGED(2),
    DELETE(3),
    ACCEPTED(4),
    DECLINED(5);

    private final int action;

    EventAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public static EventAction fromValue(Integer value) {
        if (Objects.isNull(value)) {
            return null;
        }
        switch (value) {
            case 1:
                return ADD;
            case 2:
                return CHANGED;
            case 3:
                return DELETE;
            case 4:
                return ACCEPTED;
            case 5:
                return DECLINED;
            default:
                return null;

        }
    }
}
