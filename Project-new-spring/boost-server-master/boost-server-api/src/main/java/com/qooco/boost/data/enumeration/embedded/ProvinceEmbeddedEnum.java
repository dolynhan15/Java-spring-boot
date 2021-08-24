package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.ProvinceEmbedded;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;

public enum ProvinceEmbeddedEnum {
    ID("id"),
    NAME("name"),
    TYPE("type"),
    CODE("code"),
    COUNTRY("country");

    private final String key;

    public String getKey() {
        return key;
    }

    public Object getValue(ProvinceEmbedded embedded) {
        String prefix;
        switch (this) {
            case ID:
                return embedded.getId();
            case NAME:
                return embedded.getName();
            case TYPE:
                return embedded.getType();
            case CODE:
                return embedded.getCode();
            case COUNTRY:
                prefix = StringUtil.append(COUNTRY.getKey(), ".");
                return MongoInitData.initCountryEmbedded(prefix, embedded.getCountry());
            default:
                return null;
        }
    }

    ProvinceEmbeddedEnum(String key) {
        this.key = key;
    }
}
