package com.qooco.boost.data.enumeration.doc;

import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;
import org.springframework.data.mongodb.core.query.Criteria;

public enum MessageDocEnum {
    ID("id"),
    CONVERSATION_ID("conversationId"),
    MESSAGE_CENTER_ID("messageCenterId"),
    COMPANY_ID("companyId"),
    SENDER("sender"),
    RECIPIENT("recipient"),
    RECEIVE_IN_APP("receiveInApp"),
    STATUS("status"),
    TYPE("type"),
    CONTENT("content"),
    CREATED_DATE("createdDate"),
    UPDATED_DATE("updatedDate"),
    IS_DELETED("isDeleted"),
    APPLIED_MESSAGE("appliedMessage"),
    AUTHORIZATION_MESSAGE("authorizationMessage"),
    STAFF("staff"),
    SECRET_KEY("secretKey"),
    APPOINTMENT_DETAIL_MESSAGE("appointmentDetailMessage"),

    //Custom Key
    SENDER_USER_PROFILE_ID("sender.userProfileId"),
    SENDER_IS_DELETED_MESSAGE("sender.isDeletedMessage"),

    RECIPIENT_USER_PROFILE_ID("recipient.userProfileId"),
    RECIPIENT_IS_DELETED_MESSAGE("recipient.isDeletedMessage"),


    APPLIED_MESSAGE_STATUS("appliedMessage.status"),
    APPLIED_MESSAGE_VACANCY_ID("appliedMessage.vacancy.id"),
    APPLIED_MESSAGE_VACANCY("appliedMessage.vacancy"),
    APPLIED_MESSAGE_IS_AVAILABLE("appliedMessage.isAvailable"),
    APPLIED_MESSAGE_RESPONSE_STATUS("appliedMessage.responseStatus"),
    APPLIED_MESSAGE_VACANCY_COMPANY("appliedMessage.vacancy.company"),

    AUTHORIZATION_MESSAGE_ID("authorizationMessage.id"),
    AUTHORIZATION_MESSAGE_COMPANY("authorizationMessage.company"),
    AUTHORIZATION_MESSAGE_STATUS ("authorizationMessage.status"),
    AUTHORIZATION_MESSAGE_RESPONSE_STATUS ("authorizationMessage.responseStatus"),

    STAFF_ID("staff.id"),
    STAFF_EMBEDDED_COMPANY("staff.company"),
    STAFF_MESSAGE_STATUS ("staff.status"),


    APPOINTMENT_DETAIL_MESSAGE_ID("appointmentDetailMessage.id"),
    APPOINTMENT_DETAIL_MESSAGE_STATUS("appointmentDetailMessage.status"),
    APPOINTMENT_DETAIL_MESSAGE_RESPONSE_STATUS ( "appointmentDetailMessage.responseStatus"),
    APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_DETAIL_STATUS("appointmentDetailMessage.appointmentDetailStatus"),

    APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT ( "appointmentDetailMessage.appointment"),
    APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_ID ( "appointmentDetailMessage.appointment.id"),
    APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_IS_DELETED ( "appointmentDetailMessage.appointment.isDeleted"),
    APPOINTMENT_DETAIL_MESSAGE_TO_DATE ("appointmentDetailMessage.appointment.toDate"),
    APPOINTMENT_DATE_RANGE ("appointmentDetailMessage.appointment.dateRanges"),
    APPOINTMENT_TIME_RANGE ("appointmentDetailMessage.appointment.timeRanges"),
    APPOINTMENT_TYPE ("appointmentDetailMessage.appointment.type"),
    APPOINTMENT_FROM_DATE ("appointmentDetailMessage.appointment.fromDate"),
    APPOINTMENT_TO_DATE ("appointmentDetailMessage.appointment.toDate"),


    APPOINTMENT_DETAIL_MESSAGE_VACANCY("appointmentDetailMessage.vacancy"),
    APPOINTMENT_DETAIL_MESSAGE_VACANCY_ID("appointmentDetailMessage.vacancy.id"),
    APPOINTMENT_DETAIL_MESSAGE_VACANCY_COMPANY("appointmentDetailMessage.vacancy.company"),
    APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_LOCATION_COMPANY("appointmentDetailMessage.appointment.location.company"),

    APPOINTMENT_DETAIL_ID("appointmentDetailId"),

    BOOST_HELPER_EVENT_TYPE("boostHelperMessage.eventType"),
    BOOST_HELPER_QUALIFICATION_ID("boostHelperMessage.assessment.userQualificationId")
    ;

    private final String key;

    public String getKey() {
        return key;
    }

    MessageDocEnum(String key) {
        this.key = key;
    }

    public Object getValue(MessageDoc embedded) {
        String prefix = StringUtil.append(this.getKey(), ".");
        switch (this) {
            case ID:
                return embedded.getId();
            case CONVERSATION_ID:
                return embedded.getConversationId();
            case MESSAGE_CENTER_ID:
                return embedded.getMessageCenterId();
            case SENDER:
                return MongoInitData.initUserProfileCvEmbedded(prefix, embedded.getSender());
            case RECIPIENT:
                return MongoInitData.initUserProfileCvEmbedded(prefix, embedded.getRecipient());
            case RECEIVE_IN_APP:
                return embedded.getReceiveInApp();
            case STATUS:
                return embedded.getStatus();
            case TYPE:
                return embedded.getType();
            case CONTENT:
                return embedded.getContent();
            case CREATED_DATE:
                return embedded.getCreatedDate();
            case UPDATED_DATE:
                return embedded.getUpdatedDate();
            case IS_DELETED:
                return embedded.isDeleted();
            case APPLIED_MESSAGE:
                return MongoInitData.initAppliedMessage(prefix, embedded.getAppliedMessage());
            case AUTHORIZATION_MESSAGE:
                return MongoInitData.initAuthorizationMessage(prefix, embedded.getAuthorizationMessage());
            case STAFF:
                return MongoInitData.initStaffEmbedded(prefix, embedded.getStaff());
            case APPOINTMENT_DETAIL_MESSAGE:
                return MongoInitData.initAppointmentDetailMessage(prefix, embedded.getAppointmentDetailMessage());
            default:
                return null;
        }
    }

    public static final Criteria IS_NOT_DELETED_CRITERIA  = Criteria.where(MessageDocEnum.IS_DELETED.getKey()).is(false);
}
