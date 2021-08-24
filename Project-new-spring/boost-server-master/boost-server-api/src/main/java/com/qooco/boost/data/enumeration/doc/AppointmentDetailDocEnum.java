package com.qooco.boost.data.enumeration.doc;

public enum AppointmentDetailDocEnum {
    ID("id"),
    APPOINTMENT("appointment"),
    APPOINTMENT_TIME("appointmentTime"),
    CREATED_DATE("createdDate"),
    UPDATED_DATE("updatedDate"),
    VACANCY("vacancy"),
    CANDIDATE("candidate"),
    STATUS("status"),
    IS_DELETED("isDeleted"),


    //Custom key
    CANDIDATE_USER_PROFILE_ID("candidate.userProfileId"),
    CANDIDATE_USER_PROFILE_CV_ID("candidate.userProfileCvId"),

    VACANCY_ID("vacancy.id"),
    VACANCY_COMPANY("vacancy.company"),
    VACANCY_COMPANY_ID("vacancy.company.id"),
    VACANCY_STAFF_PROFILE("vacancy.contactPerson.userProfile"),
    VACANCY_STAFF_PROFILE_ID("vacancy.contactPerson.userProfile.userProfileId"),

    APPOINTMENT_ID("appointment.id"),
    APPOINTMENT_DATE_RANGE("appointment.dateRanges"),
    APPOINTMENT_TIME_RANGE("appointment.timeRanges"),
    APPOINTMENT_TYPE("appointment.type"),
    APPOINTMENT_FROM_DATE("appointment.fromDate"),
    APPOINTMENT_TO_DATE("appointment.toDate"),
    APPOINTMENT_IS_DELETED("appointment.isDeleted"),
    APPOINTMENT_MANAGER_PROFILE("appointment.manager.userProfile"),
    APPOINTMENT_MANAGER_PROFILE_ID("appointment.manager.userProfile.userProfileId"),

    APPOINTMENT_CREATOR_PROFILE_ID("creator.userProfile"),
    APPOINTMENT_CREATOR_PROFILE("creator.userProfile.userProfileId"),
    APPOINTMENT_UPDATER_PROFILE_ID("updater.userProfile"),
    APPOINTMENT_UPDATER_PROFILE("updater.userProfile.userProfileId"),
    ;

    private final String key;

    AppointmentDetailDocEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
