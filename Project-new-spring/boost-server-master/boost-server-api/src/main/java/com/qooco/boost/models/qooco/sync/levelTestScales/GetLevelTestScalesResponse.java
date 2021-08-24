package com.qooco.boost.models.qooco.sync.levelTestScales;

import com.qooco.boost.models.qooco.sync.BaseQoocoSyncResponse;

import java.util.Date;
import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/9/2018 - 3:02 PM
*/
public class GetLevelTestScalesResponse extends BaseQoocoSyncResponse {

    private Map<String, LevelTestScale> levelTestScales;
    private Date levelTestScalesTimestamp;

    public Map<String, LevelTestScale> getLevelTestScales() {
        return levelTestScales;
    }

    public void setLevelTestScales(Map<String, LevelTestScale> levelTestScales) {
        this.levelTestScales = levelTestScales;
    }

    public Date getLevelTestScalesTimestamp() {
        return levelTestScalesTimestamp;
    }

    public void setLevelTestScalesTimestamp(Date levelTestScalesTimestamp) {
        this.levelTestScalesTimestamp = levelTestScalesTimestamp;
    }
}
