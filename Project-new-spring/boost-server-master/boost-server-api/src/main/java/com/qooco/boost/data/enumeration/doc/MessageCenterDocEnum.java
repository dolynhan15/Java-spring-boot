package com.qooco.boost.data.enumeration.doc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;

@RequiredArgsConstructor
public enum MessageCenterDocEnum {
    ID("id"),
    TYPE("type"),
    COMPANY("company"),
    VACANCY("vacancy"),
    NUMBER_OF_CANDIDATE("numberOfCandidate"),

    APPLIED_USER_PROFILES("appliedUserProfiles"),
    CONTACT_PERSONS("contactPersons"),

    APPOINTMENT_CANDIDATES("appointmentCandidates"),
    APPOINTMENT_MANAGERS("appointmentManagers"),

    REQUESTED_JOIN_USERS("requestedJoinUsers"),
    ADMIN_OF_COMPANY("adminOfCompany"),

    FREE_CHAT_RECRUITER("freeChatRecruiter"),
    FREE_CHAT_CANDIDATE("freeChatCandidate"),

    BOOST_HELPER_USER("boostHelperUser"),

    CREATED_DATE("createdDate"),
    UPDATED_DATE("updatedDate"),
    IS_DELETED("isDeleted"),
    CREATED_BY("createdBy"),


    //Custom Key
    VACANCY_ID("vacancy.id"),
    VACANCY_COMPANY("vacancy.company"),
    VACANCY_COMPANY_ID("vacancy.company.id"),
    COMPANY_ID("company.id"),

    APPOINTMENT_MANAGERS_USER_PROFILE_ID("appointmentManagers.userProfileId"),
    CONTACT_PERSONS_USER_PROFILE_ID("contactPersons.userProfileId"),
    FREE_CHAT_RECRUITER_USER_PROFILE_ID("freeChatRecruiter.userProfileId"),
    FREE_CHAT_CANDIDATE_USER_PROFILE_ID("freeChatCandidate.userProfileId"),
    BOOST_HELPER_USER_USER_PROFILE_ID("boostHelperUser.userProfileId"),
    BOOST_HELPER_USER_USER_TYPE("boostHelperUser.userType")
    ;


    @Getter
    private final String key;

    public static final Criteria IS_NOT_DELETED_CRITERIA = new Criteria().orOperator(
            Criteria.where(MessageCenterDocEnum.IS_DELETED.getKey()).is(false),
            Criteria.where(MessageCenterDocEnum.IS_DELETED.getKey()).exists(false)
    );
}
