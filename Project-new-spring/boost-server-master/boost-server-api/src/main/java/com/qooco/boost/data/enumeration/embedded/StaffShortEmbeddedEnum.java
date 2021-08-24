package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;

public enum StaffShortEmbeddedEnum {
    ID("id"),
    USER_PROFILE("userProfile"),
    ROLE_COMPANY("roleCompany");

    public Object getValue(StaffShortEmbedded embedded) {
        String prefix = StringUtil.append(this.getKey(), ".");
        switch (this) {
            case ID:
                return embedded.getId();
            case USER_PROFILE:
                return MongoInitData.initUserProfileBasicEmbedded(prefix, embedded.getUserProfile());
            case ROLE_COMPANY:
                return MongoInitData.initRoleCompanyEmbedded(prefix, embedded.getRoleCompany());
            default:
                return null;
        }
    }

    private final String key;

    public String getKey() {
        return key;
    }

    StaffShortEmbeddedEnum(String key) {
        this.key = key;
    }
}
