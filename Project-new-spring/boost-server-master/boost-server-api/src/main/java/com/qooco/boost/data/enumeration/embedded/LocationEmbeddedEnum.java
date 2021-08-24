package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.LocationEmbedded;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;

public enum LocationEmbeddedEnum {
    ID("id"),
    CITY("city"),
    ADDRESS("address"),
    COMPANY("company");

    public Object getValue(LocationEmbedded embedded) {
        String prefix = StringUtil.append(this.getKey(), ".");
        switch (this) {
            case ID:
                return embedded.getId();
            case ADDRESS:
                return embedded.getAddress();
            case CITY:
                return MongoInitData.initCityEmbedded(prefix, embedded.getCity());
            case COMPANY:
                return MongoInitData.initCompanyEmbedded(prefix, embedded.getCompany());
            default:
                return null;
        }
    }

    private final String key;

    public String getKey() {
        return key;
    }

    LocationEmbeddedEnum(String key) {
        this.key = key;
    }
}
