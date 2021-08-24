package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;

public enum StaffEmbeddedEnum {
    ID("id"),
    COMPANY("company"),
    USER_PROFILE("userProfile"),
    ROLE_COMPANY("roleCompany");

    public Object getValue(StaffEmbedded embedded) {
        String  prefix = StringUtil.append(this.getKey(), ".");;
        switch (this) {
            case ID:
                return embedded.getId();
            case COMPANY:
                return MongoInitData.initCompanyEmbedded(prefix, embedded.getCompany());
            case USER_PROFILE:
                return MongoInitData.initUserProfileEmbedded(prefix, embedded.getUserProfile());
            case ROLE_COMPANY:
                return MongoInitData.initRoleCompanyEmbedded(prefix, embedded.getRoleCompany());
        }
        return null;
    }

    private final String key;

    public String getKey() {
        return key;
    }

    StaffEmbeddedEnum(String key) {
        this.key = key;
    }
}
