package com.qooco.boost.models.qooco.sync.leveltest;

import com.qooco.boost.models.qooco.sync.BaseQoocoSyncResponse;

import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/24/2018 - 4:24 PM
*/
public class GetLevelTestResponse extends BaseQoocoSyncResponse {

    private Map<String, Map<String, Test>> levelTests;

    public Map<String, Map<String, Test>> getLevelTests() {
        return levelTests;
    }

    public void setLevelTests(Map<String, Map<String, Test>> levelTests) {
        this.levelTests = levelTests;
    }
}
