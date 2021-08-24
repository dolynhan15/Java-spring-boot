package com.qooco.boost.models.qooco.sync.levelTestHistory;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestHistorySkin {
    private Map<String, List<TestHistory> > tests;
    private Date timestamp;

    public Map<String, List<TestHistory>> getTests() {
        return tests;
    }

    public void setTests(Map<String, List<TestHistory>> tests) {
        this.tests = tests;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
