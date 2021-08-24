package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.LanguageEmbedded;

public enum LanguageEmbeddedEnum {
    ID("id"),
    NAME("name"),
    CODE("code");

    public Object getValue(LanguageEmbedded embedded) {
        switch (this) {
            case ID:
                return embedded.getId();
            case NAME:
                return embedded.getName();
            case CODE:
                return embedded.getCode();
            default:
                return null;
        }
    }

    private final String key;

    public String getKey() {
        return key;
    }

    LanguageEmbeddedEnum(String key) {
        this.key = key;
    }
}
