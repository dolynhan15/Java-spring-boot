package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.utils.MongoInitData;
import org.springframework.data.mongodb.core.query.Criteria;

public enum UserProfileCvEmbeddedEnum {
    USER_PROFILE_CV_ID("userProfileCvId"),
    IS_HOUR_SALARY("isHourSalary"),
    MIN_SALARY("minSalary"),
    MAX_SALARY("maxSalary"),
    IS_ASAP("isAsap"),
    EXPECTED_START_DATE("expectedStartDate"),
    IS_FULL_TIME("isFullTime"),
    CURRENCY("currency"),
    EDUCATION("education"),
    JOBS("jobs"),
    PREFERRED_HOTELS("preferredHotels"),
    PROFILE_STRENGTH("profileStrength"),
    ADDRESS("address"),
    PHONE("phone"),
    HAS_PERSONALITY("hasPersonality"),
    IS_DELETED_MESSAGE("isDeletedMessage");

    private final String key;

    public String getKey() {
        return key;
    }

    UserProfileCvEmbeddedEnum(String key) {
        this.key = key;
    }

    public Object getValue(UserProfileCvEmbedded embedded) {
        switch (this) {
            case USER_PROFILE_CV_ID:
                return embedded.getUserProfileCvId();
            case IS_HOUR_SALARY:
                return embedded.isHourSalary();
            case MIN_SALARY:
                return embedded.getMinSalary();
            case MAX_SALARY:
                return embedded.getMaxSalary();
            case IS_ASAP:
                return embedded.isAsap();
            case EXPECTED_START_DATE:
                return embedded.getExpectedStartDate();
            case IS_FULL_TIME:
                return embedded.isFullTime();
            case CURRENCY:
                return embedded.getCurrency();
            case EDUCATION:
                return embedded.getEducation();
            case JOBS:
                return embedded.getJobs();
            case PREFERRED_HOTELS:
                return embedded.getPreferredHotels();
            case PROFILE_STRENGTH:
                return embedded.getProfileStrength();
            case ADDRESS:
                return embedded.getAddress();
            case PHONE:
                return embedded.getPhone();
            case HAS_PERSONALITY:
                return embedded.isHasPersonality();
            default:
                return MongoInitData.initUserProfileEmbedded("", embedded);
        }
    }

    public static final Criteria IS_NOT_DELETED_MESSAGE_CRITERIA = new Criteria().orOperator(
            Criteria.where(UserProfileCvEmbeddedEnum.IS_DELETED_MESSAGE.getKey()).exists(false),
            Criteria.where(UserProfileCvEmbeddedEnum.IS_DELETED_MESSAGE.getKey()).is(false));
}
