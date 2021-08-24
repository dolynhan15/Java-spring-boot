package com.qooco.boost.models.qooco.sync.leveltest;

import java.util.Date;
import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/25/2018 - 11:01 AM
*/
public class Test {
    private Map<String, LevelTest> tests;
    private Date timestamp;

    public Map<String, LevelTest> getTests() {
        return tests;
    }

    public void setTests(Map<String, LevelTest> tests) {
        this.tests = tests;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
