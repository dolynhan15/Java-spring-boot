package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.CountryEmbedded;

public enum CountryEmbeddedEnum {
    ID("id"),
    NAME("name"),
    CODE("code"),
    PHONE_CODE("phoneCode");

    private final String key;
    public String getKey() {
        return key;
    }

    public Object getValue(CountryEmbedded embedded) {
        switch (this) {
            case ID:
                return embedded.getId();
            case NAME:
                return embedded.getName();
            case CODE:
                return embedded.getCode();
            case PHONE_CODE:
                return embedded.getPhoneCode();
        }
        return null;
    }

    CountryEmbeddedEnum(String key) {
        this.key = key;
    }
}
