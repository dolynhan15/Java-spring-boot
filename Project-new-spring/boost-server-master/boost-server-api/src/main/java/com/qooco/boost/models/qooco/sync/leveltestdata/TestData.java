package com.qooco.boost.models.qooco.sync.leveltestdata;

import java.util.Date;
import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/15/2018 - 10:59 AM
*/
public class TestData {
    private List<LevelTestData> data;
    private Date timestamp;

    public List<LevelTestData> getData() {
        return data;
    }

    public void setData(List<LevelTestData> data) {
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
