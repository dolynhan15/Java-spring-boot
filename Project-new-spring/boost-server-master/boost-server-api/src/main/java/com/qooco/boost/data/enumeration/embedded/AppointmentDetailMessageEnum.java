package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;

public enum AppointmentDetailMessageEnum {
    ID("id"),
    APPOINTMENT("appointment"),
    VACANCY("vacancy"),
    APPOINTMENT_TIME("appointmentTime"),
    APPOINTMENT_DETAIL_STATUS("appointmentDetailStatus"),
    RESPONSE_STATUS("responseStatus"),
    STATUS("status");

    public Object getValue(AppointmentDetailMessage embedded) {
        String prefix = StringUtil.append(this.getKey(), ".");
        switch (this) {
            case ID:
                return embedded.getId();
            case APPOINTMENT:
                return MongoInitData.initAppointmentEmbedded(prefix, embedded.getAppointment());
            case VACANCY:
                return MongoInitData.initVacancyEmbedded(prefix, embedded.getVacancy());
            case APPOINTMENT_TIME:
                return embedded.getAppointmentTime();
            case APPOINTMENT_DETAIL_STATUS:
                return embedded.getAppointmentDetailStatus();
            case RESPONSE_STATUS:
                return embedded.getResponseStatus();
            case STATUS:
                return embedded.getStatus();
            default:
                return null;
        }
    }

    private final String key;

    public String getKey() {
        return key;
    }

    AppointmentDetailMessageEnum(String key) {
        this.key = key;
    }
}
