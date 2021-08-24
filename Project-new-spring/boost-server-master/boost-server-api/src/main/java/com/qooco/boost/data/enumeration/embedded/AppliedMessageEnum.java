package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.message.AppliedMessage;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;

public enum AppliedMessageEnum {
    VACANCY("vacancy"),
    RESPONSE_STATUS("responseStatus"),
    STATUS("status");

    public Object getValue(AppliedMessage embedded) {
        String prefix = StringUtil.append(this.getKey(), ".");
        switch (this) {
            case VACANCY:
                return MongoInitData.initVacancyEmbedded(prefix, embedded.getVacancy());
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

    AppliedMessageEnum(String key) {
        this.key = key;
    }
}
