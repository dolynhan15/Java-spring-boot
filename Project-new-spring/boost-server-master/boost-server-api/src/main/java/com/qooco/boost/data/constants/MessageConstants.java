package com.qooco.boost.data.constants;

public class MessageConstants {
    // Message type
    public static final int APPLICANT_MESSAGE = 1;
    public static final int APPOINTMENT_MESSAGE = 2;
    public static final int AUTHORIZATION_MESSAGE = 3;
    public static final int ASSIGNMENT_ROLE_MESSAGE = 4;
    public static final int TEXT_MESSAGE = 5;
    public static final int APPOINTMENT_CANCEL_MESSAGE = 6;
    public static final int APPOINTMENT_APPLICANT_MESSAGE = 7;
    public static final int CONGRATULATION_MESSAGE = 8;
    public static final int SUSPENDED_VACANCY = 9;
    public static final int INACTIVE_VACANCY = 10;
    public static final int CHANGE_CONTACT_MESSAGE = 11;
    public static final int MEDIA_MESSAGE = 12;
    public static final int BOOST_HELPER_MESSAGE = 13;
    public static final int SESSION_MESSAGE = -1;

    // Message status
    public static final int MESSAGE_STATUS_SENT = 1;
    public static final int MESSAGE_STATUS_RECEIVED = 2;
    public static final int MESSAGE_STATUS_SEEN = 3;

    // Applied message status
    public static final int APPLIED_STATUS_PENDING = 0;
    public static final int APPLIED_STATUS_INTERESTED = 1;
    public static final int APPLIED_STATUS_NOT_INTERESTED = 2;

    // Appointment message status
    public static final int APPOINTMENT_STATUS_PENDING = 1;
    public static final int APPOINTMENT_STATUS_ACCEPTED = 2;
    public static final int APPOINTMENT_STATUS_DECLINED = 3;

//    public static final int APPOINTMENT_DETAIL_STATUS_NEW = 0;
//    public static final int APPOINTMENT_DETAIL_STATUS_CHANGE = 1;
//    public static final int APPOINTMENT_DETAIL_STATUS_CANCEL = 2;

    // Join company request message status
    public static final int JOIN_COMPANY_REQUEST_STATUS_PENDING = 0;
    public static final String JOIN_COMPANY_REQUEST_STATUS_PENDING_MESSAGE = "Join company request is pending";

    public static final int JOIN_COMPANY_REQUEST_STATUS_AUTHORIZED = 1;
    public static final String JOIN_COMPANY_REQUEST_STATUS_AUTHORIZED_MESSAGE = "Join company request is authorization";

    public static final int JOIN_COMPANY_REQUEST_STATUS_DECLINED = 2;
    public static final String JOIN_COMPANY_REQUEST_STATUS_DECLINED_MESSAGE = "Join company request is declined";

    // Define a message will received in app
    public static final int RECEIVE_IN_HOTEL_APP = 1;
    public static final int RECEIVE_IN_CAREER_APP = 2;

    // Message action type
    public static final int SUBMIT_MESSAGE_ACTION = 1;
    public static final int UPDATE_MESSAGE_ACTION = 2;
    public static final int DELETE_MESSAGE_ACTION = 3;

    public static final int APPOINTMENT_MESSAGE_ACTION_ENABLE = 4;
    public static final int APPOINTMENT_MESSAGE_ACTION_DISABLE = 5;

    // Un-assigned role id
    public static final int ROLE_COMPANY_UNASSIGNED = 5;
}