package com.qooco.boost.data.enumeration;

import com.google.common.collect.Lists;
import com.qooco.boost.enumeration.EventAction;

import java.util.List;
import java.util.Objects;


public enum AppointmentStatus {
    PENDING(1),
    ACCEPTED(2),
    DECLINED(3),
    CANCELED(4),
    CHANGED(5),
    PENDING_ACCEPTED(12),
    PENDING_CANCELED(14),
    PENDING_CHANGED(15),
    PENDING_ACCEPTED_CANCELED(124),
    PENDING_ACCEPTED_CHANGED(125);

    private static final int DECIMAL_STEP = 10;
    private final int value;

    public static List<Integer> getAvailableStatus() {
        return Lists.newArrayList(PENDING.getValue(), PENDING_ACCEPTED.getValue(), ACCEPTED.getValue());
    }

    public static List<Integer> getPendingAndCancelStatus() {
        return Lists.newArrayList(PENDING_CANCELED.getValue(), PENDING_CHANGED.getValue());
    }


    public static List<Integer> getAcceptedStatus() {
        return Lists.newArrayList(PENDING_ACCEPTED.getValue(), ACCEPTED.getValue());
    }

    public static List<Integer> getDeclineStatus() {
        return Lists.newArrayList(DECLINED.getValue());
    }

    public static List<Integer> getCancelStatus() {
        return Lists.newArrayList(CANCELED.getValue(), CHANGED.getValue());
    }

    public int getValue() {
        return value;
    }

    AppointmentStatus(int value) {
        this.value = value;
    }

    public static AppointmentStatus fromValue(Integer value) {
        if (Objects.isNull(value)) {
            return null;
        }
        switch (value) {
            case 1:
                return PENDING;
            case 2:
                return ACCEPTED;
            case 3:
                return DECLINED;
            case 4:
                return CANCELED;
            case 5:
                return CHANGED;
            case 12:
                return ACCEPTED;
            default:
                return null;
        }
    }

    public static Integer getAppointmentStatusBy(int status) {
        if (status == PENDING_ACCEPTED.getValue() || status == ACCEPTED.getValue()) {
            return PENDING_ACCEPTED.getValue();
        }
        return DECLINED.getValue();
    }

    public static Integer mixAppointmentStatusByEventAction(int oldStatus, int eventAction) {
        EventAction action = EventAction.fromValue(eventAction);
        if (Objects.nonNull(action)) {
            switch (action) {
                case CHANGED:
                    return oldStatus * DECIMAL_STEP + AppointmentStatus.CHANGED.getValue();
                case DELETE:
                    return oldStatus * DECIMAL_STEP + AppointmentStatus.CANCELED.getValue();
                case ACCEPTED:
                    return oldStatus * DECIMAL_STEP + AppointmentStatus.ACCEPTED.getValue();
                case DECLINED:
                    return oldStatus * DECIMAL_STEP + AppointmentStatus.DECLINED.getValue();
                default:
                    return AppointmentStatus.PENDING.getValue();
            }
        }
        return null;
    }

    public static Integer mixAppointmentStatus(int oldStatus, int appointmentStatus) {
        AppointmentStatus action = AppointmentStatus.fromValue(appointmentStatus);
        if (Objects.nonNull(action)) {
            switch (action) {
                case ACCEPTED:
                    return oldStatus * DECIMAL_STEP + AppointmentStatus.ACCEPTED.getValue();
                case DECLINED:
                    return oldStatus * DECIMAL_STEP + AppointmentStatus.DECLINED.getValue();
                default:
                    return AppointmentStatus.PENDING.getValue();
            }
        }
        return null;
    }

    public static AppointmentStatus convertFromValue(int value) {
        int statusValue = value % DECIMAL_STEP;
        if (statusValue == 0) {
            return null;
        }
        return AppointmentStatus.fromValue(statusValue);
    }

    public static int getStatusValue(int mixValue) {
        return mixValue % DECIMAL_STEP;
    }
}
