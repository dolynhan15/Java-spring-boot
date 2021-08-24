package com.qooco.boost.models.qooco.sync.localization;

import com.qooco.boost.models.qooco.sync.BaseQoocoSyncResponse;

import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/2/2018 - 2:09 PM
*/
public class GetLocalizationResponse extends BaseQoocoSyncResponse {
    private Map<String, Map<String, CollectionData>> localizationStrings2;

    public Map<String, Map<String, CollectionData>> getLocalizationStrings2() {
        return localizationStrings2;
    }

    public void setLocalizationStrings2(Map<String, Map<String, CollectionData>> localizationStrings2) {
        this.localizationStrings2 = localizationStrings2;
    }
}
