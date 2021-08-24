package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;

public enum AppointmentEmbeddedEnum {
    ID("id"),
    LOCATION("location"),
    MANAGER("manager"),
    APPOINTMENT_DATE("appointmentDate"),
    DATE_RANGES("dateRanges"),
    TIME_RANGES("timeRanges"),
    FROM_DATE("fromDate"),
    TO_DATE("toDate"),
    TYPE("type"),
    IS_DELETED("isDeleted");

    private final String key;

    public String getKey() {
        return key;
    }

    public Object getValue(AppointmentEmbedded embedded) {
        String prefix = StringUtil.append(this.getKey(), ".");
        switch (this) {
            case ID:
                return embedded.getId();
            case LOCATION:
                return MongoInitData.initLocationEmbedded(prefix, embedded.getLocation());
            case MANAGER:
                return MongoInitData.initStaffShortEmbedded(prefix, embedded.getManager());
            case APPOINTMENT_DATE:
                return embedded.getAppointmentDate();
            case TYPE:
                return embedded.getType();
            case TO_DATE:
                return embedded.getToDate();
            case FROM_DATE:
                return embedded.getFromDate();
            case DATE_RANGES:
                return embedded.getDateRanges();
            case TIME_RANGES:
                return embedded.getTimeRanges();
            case IS_DELETED:
                return embedded.isDeleted();
            default:
                return null;
        }
    }

    AppointmentEmbeddedEnum(String key) {
        this.key = key;
    }
}
