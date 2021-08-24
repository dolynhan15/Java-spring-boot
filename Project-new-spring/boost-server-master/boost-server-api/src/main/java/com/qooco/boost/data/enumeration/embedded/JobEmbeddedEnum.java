package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.JobEmbedded;

public enum JobEmbeddedEnum {
    ID("id"),
    NAME("name"),
    DESCRIPTION("description");

    public Object getValue(JobEmbedded embedded) {
        switch (this) {
            case ID:
                return embedded.getId();
            case NAME:
                return embedded.getName();
            case DESCRIPTION:
                return embedded.getDescription();
            default:
                return null;
        }
    }

    private final String key;

    public String getKey() {
        return key;
    }

    JobEmbeddedEnum(String key) {
        this.key = key;
    }
}
