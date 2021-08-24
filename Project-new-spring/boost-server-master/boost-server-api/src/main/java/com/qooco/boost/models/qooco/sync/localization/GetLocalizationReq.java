package com.qooco.boost.models.qooco.sync.localization;

import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/2/2018 - 2:45 PM
*/
public class GetLocalizationReq {

    private Map<String, Map<String, Long>> getLocalizationStrings2;

    public Map<String, Map<String, Long>> getGetLocalizationStrings2() {
        return getLocalizationStrings2;
    }

    public void setGetLocalizationStrings2(Map<String, Map<String, Long>> getLocalizationStrings2) {
        this.getLocalizationStrings2 = getLocalizationStrings2;
    }
}
