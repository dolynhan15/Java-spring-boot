package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.RoleCompanyEmbedded;

public enum RoleCompanyEmbeddedEum {
    ROLE_ID("roleId"),
    NAME("name"),
    DISPLAY_NAME("displayName");

    public Object getValue(RoleCompanyEmbedded embedded) {
        switch (this) {
            case ROLE_ID:
                return embedded.getRoleId();
            case NAME:
                return embedded.getName();
            case DISPLAY_NAME:
                return embedded.getDisplayName();
        }
        return null;
    }

    private final String key;

    public String getKey() {
        return key;
    }

    RoleCompanyEmbeddedEum(String key) {
        this.key = key;
    }
}
