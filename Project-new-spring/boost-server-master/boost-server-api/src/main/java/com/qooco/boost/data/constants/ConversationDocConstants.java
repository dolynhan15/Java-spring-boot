package com.qooco.boost.data.constants;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class ConversationDocConstants {

    private ConversationDocConstants() {}

    public static final String ID = "id";
    public static final String MESSAGE_CENTER_ID = "messageCenterId";
    public static final String PARTICIPANTS = "participants";
    public static final String PARTICIPANTS_USER_PROFILE_ID = "participants.userProfileId";
    public static final String PARTICIPANTS_USER_TYPE = "participants.userType";
    public static final String CREATED_DATE = "createdDate";
    public static final String UPDATED_DATE = "updatedDate";
    public static final String CREATED_BY = "createdBy";
    public static final String CREATED_BY_USER_PROFILE_ID = "createdBy.userProfileId";
    public static final String CREATED_BY_USER_TYPE = "createdBy.userType";
    public static final String IS_DELETED = "isDeleted";
    public static final String IS_DISABLE = "isDisable";
    public static final String CONTACT_PERSON_STATUS = "contactPersonStatus";
    public static final String ENCRYPTED_PUBLIC_KEYS = "userKeys";
    public static final String SECRET_KEY = "secretKey";
    public static final String PUBLIC_KEY = "publicKey";

    public static final String COMPANY_ID = "companyId";
    public static final String USER_PROFILE_ID = "userProfileId";
    public static final String USER_TYPE = "userType";

    public static List<String> shortConversationKeys(){
        return ImmutableList.of(
                ID,
                MESSAGE_CENTER_ID,
                COMPANY_ID,
                PARTICIPANTS,
                CREATED_BY,
                CREATED_DATE,
                UPDATED_DATE,
                IS_DELETED,
                IS_DISABLE,
                CONTACT_PERSON_STATUS);
    }
}
