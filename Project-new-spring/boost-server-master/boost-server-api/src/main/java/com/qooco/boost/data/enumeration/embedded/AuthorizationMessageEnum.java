package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.message.AuthorizationMessage;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;

public enum AuthorizationMessageEnum {
    ID("id"),
    COMPANY("company"),
    VACANCY("vacancy"),
    RESPONSE_STATUS("responseStatus"),
    STATUS("status");

    public Object getValue(AuthorizationMessage embedded) {
        String prefix = StringUtil.append(this.getKey(), ".");
        switch (this) {
            case ID:
                return embedded.getId();
            case COMPANY:
                return MongoInitData.initCompanyEmbedded(prefix, embedded.getCompany());
            case RESPONSE_STATUS:
                return embedded.getResponseStatus();
            case STATUS:
                return embedded.getStatus();
            default:
                return null;
        }
    }

    private final String key;

    public String getKey() {
        return key;
    }

    AuthorizationMessageEnum(String key) {
        this.key = key;
    }


}
