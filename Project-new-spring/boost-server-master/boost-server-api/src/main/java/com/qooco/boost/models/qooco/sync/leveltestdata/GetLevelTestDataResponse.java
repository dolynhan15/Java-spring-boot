package com.qooco.boost.models.qooco.sync.leveltestdata;

import com.qooco.boost.models.qooco.sync.BaseQoocoSyncResponse;

import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/24/2018 - 4:24 PM
*/
public class GetLevelTestDataResponse extends BaseQoocoSyncResponse {

    private Map<String, Map<String, TestData>> levelTestData;

    public Map<String, Map<String, TestData>> getLevelTestData() {
        return levelTestData;
    }

    public void setLevelTestData(Map<String, Map<String, TestData>> levelTestData) {
        this.levelTestData = levelTestData;
    }
}
