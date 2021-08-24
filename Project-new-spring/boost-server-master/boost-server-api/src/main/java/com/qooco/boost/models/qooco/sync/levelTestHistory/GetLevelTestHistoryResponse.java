package com.qooco.boost.models.qooco.sync.levelTestHistory;

import com.qooco.boost.models.qooco.sync.BaseQoocoSyncResponse;

import java.util.Map;

public class GetLevelTestHistoryResponse extends BaseQoocoSyncResponse {
    private Map<String, Map<String, TestHistorySkin> > levelTestHistory;

    public Map<String, Map<String, TestHistorySkin>> getLevelTestHistory() {
        return levelTestHistory;
    }

    public void setLevelTestHistory(Map<String, Map<String, TestHistorySkin>> levelTestHistory) {
        this.levelTestHistory = levelTestHistory;
    }
}
