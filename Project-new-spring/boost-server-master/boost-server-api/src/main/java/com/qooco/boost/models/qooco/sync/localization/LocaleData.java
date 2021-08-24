package com.qooco.boost.models.qooco.sync.localization;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/2/2018 - 3:18 PM
*/
public class LocaleData {
    private String collection;
    private long timestamp;
    private String locale;

    public LocaleData() {}

    public LocaleData(String collection, long timestamp, String locale) {
        this.collection = collection;
        this.timestamp = timestamp;
        this.locale = locale;
    }
    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
