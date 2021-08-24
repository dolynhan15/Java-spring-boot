package com.qooco.boost.models.qooco.sync.leveltestwizards;

import com.qooco.boost.models.qooco.sync.BaseQoocoSyncResponse;

import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/8/2018 - 11:55 AM
*/
public class GetLevelTestWizardsResponse extends BaseQoocoSyncResponse {
    private Map<String, Map<String, Map<String, Object> >> levelTestWizards;

    public Map<String, Map<String, Map<String, Object>>> getLevelTestWizards() {
        return levelTestWizards;
    }

    public void setLevelTestWizards(Map<String, Map<String, Map<String, Object>>> levelTestWizards) {
        this.levelTestWizards = levelTestWizards;
    }
}
