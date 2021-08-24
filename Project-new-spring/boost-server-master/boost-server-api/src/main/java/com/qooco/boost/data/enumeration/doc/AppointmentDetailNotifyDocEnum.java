package com.qooco.boost.data.enumeration.doc;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum  AppointmentDetailNotifyDocEnum {
    ID("id"),
    STAFF("staff"),
    APPOINTMENT_DETAIL_IDS("appointmentDetailIds"),


    //Custom key
    STAFF_ID("staff.id"),
    STAFF_USER_PROFILE_USER_PROFILE_ID("staff.userProfile.userProfileId"),
    STAFF_COMPANY_ID("staff.company.id"),
    ;


    private final String key;

    public String getKey() {
        return key;
    }
}
