package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.appointment.AppointmentDetailEmbedded;

public enum AppointmentCandidateEmbeddedEnum {
    ID("id"),
    STATUS("status"),
    USER_CV_ID("userCvId"),
    APPOINTMENT_ID("appointmentId");

    private final String key;

    public String getKey() {
        return key;
    }

    public Object getValue(AppointmentDetailEmbedded embedded) {
        switch (this) {
            case ID:
                return embedded.getId();
            case STATUS:
                return embedded.getStatus();
            case USER_CV_ID:
                return embedded.getCandidate().getUserProfileCvId();
            case APPOINTMENT_ID:
                return embedded.getAppointment().getId();
            default:
                return null;
        }
    }

    AppointmentCandidateEmbeddedEnum(String key) {
        this.key = key;
    }
}
