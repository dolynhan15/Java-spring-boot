package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.UserProfileBasicEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;

public enum UserProfileEmbeddedEnum {
    USER_PROFILE_ID("userProfileId"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    AVATAR("avatar"),
    USERNAME("username"),
    GENDER("gender"),
    EMAIL("email"),
    USER_TYPE("userType"),

    BIRTHDAY("birthday"),
    NATIONAL_ID("nationalId"),
    COUNTRY("country"),
    CITY("city"),
    NATIVE_LANGUAGES("nativeLanguages"),
    LANGUAGES("languages");

    private final String key;

    public String getKey() {
        return key;
    }

    public Object getValue(UserProfileBasicEmbedded embedded) {
        switch (this) {
            case USER_PROFILE_ID:
                return embedded.getUserProfileId();
            case FIRST_NAME:
                return embedded.getFirstName();
            case LAST_NAME:
                return embedded.getLastName();
            case AVATAR:
                return embedded.getAvatar();
            case USERNAME:
                return embedded.getUsername();
            case GENDER:
                return embedded.getGender();
            case EMAIL:
                return embedded.getEmail();
            case USER_TYPE:
                return embedded.getUserType();
            default:
                return null;
        }
    }

    public Object getValue(UserProfileEmbedded embedded) {
        switch (this) {
            case BIRTHDAY:
                return embedded.getBirthday();
            case NATIONAL_ID:
                return embedded.getNationalId();
            case COUNTRY:
                return embedded.getCountry();
            case CITY:
                return embedded.getCity();
            case NATIVE_LANGUAGES:
                return embedded.getNativeLanguages();
            case LANGUAGES:
                return embedded.getLanguages();
            default:
                return getValue((UserProfileBasicEmbedded) embedded);
        }
    }

    UserProfileEmbeddedEnum(String key) {
        this.key = key;
    }
}
