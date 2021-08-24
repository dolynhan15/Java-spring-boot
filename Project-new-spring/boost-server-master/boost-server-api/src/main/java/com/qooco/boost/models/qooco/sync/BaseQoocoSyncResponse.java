package com.qooco.boost.models.qooco.sync;

import java.util.Date;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/24/2018 - 4:19 PM
*/
public class BaseQoocoSyncResponse {

    private String result;
    private Date serverTime;
    private int serverProcessingTime;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }

    public int getServerProcessingTime() {
        return serverProcessingTime;
    }

    public void setServerProcessingTime(int serverProcessingTime) {
        this.serverProcessingTime = serverProcessingTime;
    }
}
