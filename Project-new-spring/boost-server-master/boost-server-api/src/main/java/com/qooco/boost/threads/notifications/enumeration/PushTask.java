package com.qooco.boost.threads.notifications.enumeration;

import lombok.Getter;

public enum PushTask {
    //TO ALL APP
    PUSH_CHAT_NEW_MESSAGE(1, ""),
    PUSH_APPOINTMENT_MESSAGE(2, ""),
    PUSH_APPOINTMENT_APPLICANT_MESSAGE(3, ""),
    PUSH_SEND_FILE_MESSAGE(4, ""),
    PUSH_SEND_BOOST_HELPER_MESSAGE(5, ""),

    //TO CAREER APP
    PUSH_TO_CAREER_APPLICANT_MESSAGE(2000, ""),
    PUSH_TO_CAREER_VIEW_CANDIDATE_PROFILE(2001, ""),
    PUSH_TO_CAREER_VACANCY_MESSAGE(2002, ""),
    PUSH_TO_CAREER_CHANGE_CONTACT_MESSAGE(2006, ""),

    //TO HOTEL
    PUSH_TO_HOTEL_RESPONSE_APPLICANT_MESSAGE(5000, ""),
    PUSH_TO_HOTEL_JOIN_COMPANY_REQUEST_MESSAGE(5001, ""),
    PUSH_TO_HOTEL_APPROVAL_JOIN_COMPANY_REQUEST(5002, ""),
    PUSH_TO_HOTEL_ASSIGN_ROLE_MESSAGE(5003, ""),
    PUSH_TO_HOTEL_APPROVE_NEW_COMPANY(5004, ""),
    PUSH_TO_HOTEL_RESPONSE_APPOINTMENT_MESSAGE(5005, ""),

    ;
    @Getter
    private int value;
    @Getter
    private String decs;

    PushTask(int value, String decs) {
        this.value = value;
        this.decs = decs;
    }
}
