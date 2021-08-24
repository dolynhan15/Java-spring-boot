package com.qooco.boost.models.qooco.sync.localization;

import java.util.Date;
import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/2/2018 - 2:13 PM
*/
public class CollectionData {

    private Map<String, String> strings;
    private Date timestamp;

    public Map<String, String> getStrings() {
        return strings;
    }

    public void setStrings(Map<String, String> strings) {
        this.strings = strings;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
