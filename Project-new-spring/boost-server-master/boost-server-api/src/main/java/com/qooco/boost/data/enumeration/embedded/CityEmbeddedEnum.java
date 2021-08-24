package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.CityEmbedded;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;

public enum CityEmbeddedEnum {
    ID("id"),
    NAME("name"),
    LATITUDE("latitude"),
    LONGITUDE("longitude"),
    PROVINCE("province");

    private final String key;

    public String getKey() {
        return key;
    }

    public Object getValue(CityEmbedded embedded) {
        String prefix;
        switch (this) {
            case ID:
                return embedded.getId();
            case NAME:
                return embedded.getName();
            case LATITUDE:
                return embedded.getLatitude();
            case LONGITUDE:
                return embedded.getLongitude();
            case PROVINCE:
                prefix = StringUtil.append(PROVINCE.getKey(), ".");
                return MongoInitData.initProvinceEmbedded(prefix, embedded.getProvince());
            default:
                return null;
        }
    }

    CityEmbeddedEnum(String key) {
        this.key = key;
    }
}
