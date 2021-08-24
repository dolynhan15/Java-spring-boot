package com.qooco.boost.models.qooco.sync;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/25/2018 - 10:44 AM
*/
public class BaseQoocoSyncReq {
    @SerializedName("en_US")
    private Map<String, Long> enLocale;

    public Map<String, Long> getEnLocale() {
        return enLocale;
    }

    public void setEnLocale(Map<String, Long> enLocale) {
        this.enLocale = enLocale;
    }
}
