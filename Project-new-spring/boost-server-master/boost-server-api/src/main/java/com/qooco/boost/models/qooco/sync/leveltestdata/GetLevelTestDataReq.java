package com.qooco.boost.models.qooco.sync.leveltestdata;

import com.qooco.boost.models.qooco.sync.BaseQoocoSyncReq;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/25/2018 - 9:32 AM
*/
public class GetLevelTestDataReq {
    private BaseQoocoSyncReq getLevelTestData;

    public BaseQoocoSyncReq getGetLevelTests() {
        return getLevelTestData;
    }

    public void setGetLevelTests(BaseQoocoSyncReq getLevelTestData) {
        this.getLevelTestData = getLevelTestData;
    }
}
